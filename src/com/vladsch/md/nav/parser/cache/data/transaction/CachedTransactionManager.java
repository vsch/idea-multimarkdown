// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.transaction;

import com.intellij.openapi.application.ApplicationManager;
import com.vladsch.flexmark.util.sequence.RepeatedSequence;
import com.vladsch.md.nav.parser.cache.ProjectFileMonitor;
import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.CachedDataOwner;
import com.vladsch.md.nav.parser.cache.data.CachedDataSet;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependencyManager;
import com.vladsch.md.nav.parser.cache.data.dependency.ProjectFilePredicate;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;

import static com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger.INDENT;
import static com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger.LOG_COMPUTE;
import static com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger.LOG_DEPENDENCY;

// per thread instance of this class
public class CachedTransactionManager implements LogIndenter {
    public static final String ADD_DEPENDENCY = "ADD_DEPENDENCY: ";

    private final @NotNull Stack<CachedDataTransaction<?, ?>> myOpenTransactions = new Stack<>();

    final @NotNull DataDependencyManager myManager;
    int myIndentOffset = 0;

    public CachedTransactionManager(@NotNull DataDependencyManager manager) {
        myManager = manager;
    }

    @NotNull
    @Override
    public String getId(boolean wantReal) {
        return myManager.getId(wantReal);
    }

    @Override
    public long getTimestamp(boolean wantReal) {
        return myManager.getTimestamp(wantReal);
    }

    @NotNull
    @Override
    public String getLogIndent() {
        return RepeatedSequence.ofSpaces((myOpenTransactions.size() + myIndentOffset) * IndentingLogger.INDENT.length()).toString();
    }

    public <H extends CachedDataOwner, T> T get(@NotNull H host, @NotNull CachedDataKey<H, T> dataKey) {
        if (myOpenTransactions.size() == 0) {
            Object[] result = new Object[] { null };

            ApplicationManager.getApplication().runReadAction(() -> {
                result[0] = getRaw(host, dataKey);
            });

            //noinspection unchecked
            return (T) result[0];
        } else {
            return getRaw(host, dataKey);
        }
    }

    private <H extends CachedDataOwner, T> T getRaw(@NotNull H host, @NotNull CachedDataKey<H, T> dataKey) {
        HashMap<DataDependency, Long> versionedDependencies = new HashMap<>();

        CachedDataSet cachedData = host.getCachedData();
        CachedDataTransaction<H, T> transaction = new CachedDataTransaction<H, T>(host, cachedData, dataKey) {
            @NotNull
            @Override
            public <D> D get(@NotNull CachedDataKey<H, D> dataKey) {
                // transaction manager adds dependency on the key being computed, to parent transaction key/cached data
                return CachedTransactionManager.this.get(host, dataKey);
            }

            @Override
            public void addDependency(@NotNull Object dependency) {
                if (dependency instanceof Collection) {
                    for (Object dependent : (Collection<?>) dependency) {
                        addDependency(dependent);
                    }
                } else if (dependency instanceof Object[]) {
                    for (Object dependent : (Object[]) dependency) {
                        addDependency(dependent);
                    }
                } else {
                    DataDependency dataDependency;

                    if (dependency instanceof ProjectFilePredicate) {
                        dataDependency = ProjectFileMonitor.getInstance(host.getProject()).getDependency(cachedData.getDependency(dataKey), (ProjectFilePredicate) dependency);
                    } else {
                        dataDependency = dependency instanceof DataDependency ? (DataDependency) dependency : myManager.getDependency(dependency);
                    }

                    long version = dataDependency.getVersion();
                    Long previousVersion = versionedDependencies.get(dataDependency);

                    if (version == -1) {
                        LOG_DEPENDENCY.error(ADD_DEPENDENCY + "Invalidated dependency " + dependency);
                    } else {
                        if (previousVersion != null && previousVersion != version) {
                            LOG_DEPENDENCY.error(ADD_DEPENDENCY + "Existing " + dependency + " changed version from " + previousVersion + " to " + version);
                            versionedDependencies.remove(dataDependency);
                        }

                        if (!versionedDependencies.containsKey(dataDependency)) {
                            versionedDependencies.put(dataDependency, version);
                            LOG_DEPENDENCY.debug(INDENT + ADD_DEPENDENCY + "dataKey: " + dataKey.getName() + ", cachedData: " + cachedData.getName() + ", dataDependency: " + dataDependency + " version: " + version);
                        }
                    }
                }
            }
        };

        int alreadyComputing = getComputingKeyIndex(transaction);

        if (alreadyComputing >= 0) {
            // already computing this key/cachedData combination, recursive call
            myOpenTransactions.push(transaction);
            throwWithTransactionTrace(alreadyComputing);
        }

        ReentrantLock lock = cachedData.getKeyLock(dataKey);
        try {
            lock.lock();

            try {
                myOpenTransactions.push(transaction);

                try {
                    myIndentOffset = -1;
                    if (LOG_COMPUTE.isDebugEnabled()) {
                        if (LOG_COMPUTE.isStackTraceEnabled()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("GET: ").append(cachedData.getDataKeyWithCacheDescription(dataKey)).append("\n");
                            getStackTraces(sb, myOpenTransactions.size() - 1);
                            LOG_COMPUTE.debug(sb.toString());
                        } else {
                            LOG_COMPUTE.debug("GET: " + transaction.myCachedData.getDataKeyWithCacheDescription(transaction.myDataKey));
                        }
                    }
                } finally {
                    myIndentOffset = 0;
                }

                //noinspection unchecked
                T value = (T) cachedData.getOrNull(dataKey);

                if (value != null) {
                    if (dataKey.isValid(value)) {
                        // need to add dependency to parent since it depends on this key, even if this time around the key is valid
                        if (myOpenTransactions.size() > 1) {
                            // this transaction is at size-1, parent at size-2
                            myOpenTransactions.get(myOpenTransactions.size() - 2).addDependency(cachedData.getDependency(dataKey));
                        }
                        return value;
                    }

                    // data not valid
                    if (LOG_COMPUTE.isDebugEnabled()) LOG_COMPUTE.debug("STALE_DATA: " + cachedData.getDataKeyWithCacheDescription(dataKey));
                    else LOG_COMPUTE.warn("STALE_DATA: " + cachedData.getDataKeyWithCacheDescription(dataKey));
                    cachedData.remove(dataKey);
                }

                if (LOG_COMPUTE.isDebugEnabled()) LOG_COMPUTE.debug("COMPUTING: " + cachedData.getDataKeyWithCacheDescription(dataKey));
                value = dataKey.compute(transaction);
                assert dataKey.isValid(value);

                DataDependency[] dependencies = new DataDependency[versionedDependencies.size()];
                long[] versions = new long[versionedDependencies.size()];
                int i = 0;

                for (DataDependency dependency : versionedDependencies.keySet()) {
                    assert dependency != null;

                    dependencies[i] = dependency;
                    versions[i] = versionedDependencies.get(dependency);

                    long version = dependency.getVersion();
                    if (version == -1) {
                        LOG_DEPENDENCY.error("Dependency was invalidated by end of computation, " + dependency);
                    } else if (versions[i] != version) {
                        LOG_DEPENDENCY.error("Dependency changed versions by end of computation, " + dependency + " from " + versions[i] + " to " + version);
                    }

                    i++;
                }

                transaction.myCachedData.setValue(dataKey, value, dependencies, versions);

                // now add dependency to parent transaction on this key
                if (myOpenTransactions.size() > 1) {
                    // this transaction is at size-1, parent at size-2
                    myOpenTransactions.get(myOpenTransactions.size() - 2).addDependency(cachedData.getDependency(dataKey));
                }

                return value;
            } finally {
                myOpenTransactions.pop();
            }
        } finally {
            lock.unlock();
        }
    }

    private int getComputingKeyIndex(@NotNull CachedDataTransaction<?, ?> transaction) {
        CachedDataKey<?, ?> dataKey = transaction.getDataKey();
        CachedDataSet cachedData = transaction.myCachedData;

        int iMax = myOpenTransactions.size();
        for (int i = iMax; i-- > 0; ) {
            CachedDataTransaction<?, ?> openTransaction = myOpenTransactions.get(i);
            if (openTransaction.myCachedData == cachedData && openTransaction.myDataKey == dataKey) return i;
        }

        return -1;
    }

    private void throwWithTransactionTrace(int highlightIndex) {
        CachedDataTransaction<?, ?> transaction = myOpenTransactions.peek();

        StringBuilder sb = new StringBuilder();
        sb.append("RECURSIVE_COMPUTE: ").append(transaction.myCachedData.getDataKeyWithCacheDescription(transaction.myDataKey));

        getStackTraces(sb, highlightIndex);

        myOpenTransactions.clear();
        throw new IllegalStateException(sb.toString());
    }

    private void getStackTraces(StringBuilder out, Integer highlightIndex) {
        String sep = "\n";
        CachedDataTransaction<?, ?> transaction;
        int iMax = myOpenTransactions.size();
        for (int i = 0; i < iMax; i++) {
            transaction = myOpenTransactions.get(i);
            out.append(sep).append(transaction.myCachedData.getDataKeyWithCacheDescription(transaction.myDataKey)).append("\n");

            if (highlightIndex == null || i == highlightIndex || i == iMax - 1) {
                // show stack trace for these
                for (StackTraceElement element : transaction.myStackTraceElements) {
                    out.append(IndentingLogger.INDENT).append(sep).append(element.toString());
                }
            }

            sep = IndentingLogger.INDENT + sep;
        }
    }
}
