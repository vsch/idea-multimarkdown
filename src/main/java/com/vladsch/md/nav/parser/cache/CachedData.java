// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.md.nav.parser.cache.data.CachedDataKey;
import com.vladsch.md.nav.parser.cache.data.CachedDataOwner;
import com.vladsch.md.nav.parser.cache.data.ProjectCachedData;
import com.vladsch.md.nav.parser.cache.data.PsiFileCachedData;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependencyManager;
import com.vladsch.md.nav.parser.cache.data.dependency.DataDependencyProvider;
import com.vladsch.md.nav.parser.cache.data.dependency.PsiFileDependency;
import com.vladsch.md.nav.parser.cache.data.dependency.VirtualFileDependency;
import com.vladsch.md.nav.parser.cache.data.transaction.CachedTransactionManager;
import com.vladsch.md.nav.parser.cache.data.transaction.IndentingLogger;
import com.vladsch.md.nav.parser.cache.data.transaction.LogIndenter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.HashMap;

import static com.intellij.openapi.application.ApplicationManager.getApplication;

public class CachedData {

    @NotNull
    public static CachedData getInstance() {
        return ApplicationManager.getApplication().getService(CachedData.class);
    }

    private final Application myApplication = getApplication();
    private final ThreadLocal<CachedTransactionManager> myManager;
    private final HashMap<Long, String> myThreadIds = new HashMap<>();
    private final HashMap<Class<?>, DataDependencyProvider> myDependencyProviders = new HashMap<>();
    final @NotNull DataDependencyManager myDependencyManager;
    private int myNextTimestamp = 1;
    final private @NotNull MutableDataHolder myTestDataHolder = new MutableDataSet();

    public CachedData() {
        myDependencyManager = new DataDependencyManager() {
            @NotNull
            @Override
            public DataDependency getDependency(@NotNull Object dependent) {
                return CachedData.this.getDependency(dependent);
            }

            @NotNull
            @Override
            public String getLogIndent() {
                return "";
            }

            @Override
            public long getTimestamp(boolean wantReal) {
                return CachedData.this.getTimestamp(wantReal);
            }

            @NotNull
            @Override
            public String getId(boolean wantReal) {
                return CachedData.this.getId(wantReal);
            }
        };

        myManager = ThreadLocal.withInitial(() -> new CachedTransactionManager(myDependencyManager));
    }

    @NotNull
    public MutableDataHolder getTestDataHolder() {
        return myTestDataHolder;
    }

    @NotNull
    DataDependency getDependency(@NotNull Object dependent) {
        if (dependent instanceof PsiFile) {
            return new PsiFileDependency((PsiFile) dependent);
        } else if (dependent instanceof VirtualFile) {
            return new VirtualFileDependency((VirtualFile) dependent);
        } else {
            {
                DataDependencyProvider provider;
                synchronized (myDependencyProviders) {
                    provider = myDependencyProviders.get(dependent.getClass());
                }

                if (provider != null) {
                    DataDependency dependency = provider.getDependency(dependent);
                    if (dependency != null) return dependency;

                    IndentingLogger.LOG_DEPENDENCY.error("Dependency provider returned null dependency, provider: " + provider + ", dependent: " + dependent);
                    synchronized (myDependencyProviders) {
                        myDependencyProviders.remove(dependent.getClass());
                    }
                }
            }

            for (DataDependencyProvider provider : DataDependencyProvider.EXTENSIONS.getValue()) {
                DataDependency dependency = provider.getDependency(dependent);
                if (dependency != null) {
                    synchronized (myDependencyProviders) {
                        myDependencyProviders.put(dependent.getClass(), provider);
                    }
                    return dependency;
                }
            }
        }

        throw new IllegalStateException("No Dependency provider for " + dependent);
    }

    @TestOnly
    public void resetLogId() {
        myNextTimestamp = 1;
        myThreadIds.clear();
    }

    @NotNull
    String getLogId(boolean wantReal) {
        if (wantReal) {
            return getThreadId(wantReal);
        } else {
            synchronized (myThreadIds) {
                String id = myThreadIds.get(Thread.currentThread().getId());
                if (id != null) return id;
                id = getThreadId(wantReal);
                myThreadIds.put(Thread.currentThread().getId(), id);
                return id;
            }
        }
    }

    private String getThreadId(boolean wantReal) {
        if (myApplication.isDispatchThread()) {
            return " **";
        } else {
            if (!wantReal) {
                // otherwise don't get consistent logs for test comparison
                return " ++";
//                synchronized (myManager) {
//                    return String.format("%02d", myNextThreadId++);
//                }
            } else {
                return String.format("%03d", Thread.currentThread().getId());
            }
        }
    }

    @NotNull
    String getId(boolean wantReal) {
        return getLogId(wantReal);
    }

    long getTimestamp(boolean wantReal) {
        return wantReal ? System.currentTimeMillis() : myNextTimestamp++;
    }

    @NotNull
    public static LogIndenter getLogIndenter() {
        return getInstance().myManager.get();
    }

    @NotNull
    public static <H extends CachedDataOwner, T> T get(@NotNull H host, @NotNull CachedDataKey<H, T> dataKey) {
        return getInstance().myManager.get().get(host, dataKey);
    }

    @Nullable
    public static <H extends CachedDataOwner, T> T getOrNull(@NotNull H host, @NotNull CachedDataKey<H, T> dataKey) {
        //noinspection unchecked
        return (T) host.getCachedData().getOrNull(dataKey);
    }

    @NotNull
    public static <T> T get(@NotNull PsiFile psiFile, @NotNull CachedDataKey<PsiFileCachedData, T> dataKey) {
        PsiFileCachedData cachedData = ProjectCachedData.getInstance(psiFile.getProject()).getFileCachedData(psiFile);
        return getInstance().myManager.get().get(cachedData, dataKey);
    }

    @Nullable
    public static <T> T getOrNull(@NotNull PsiFile psiFile, @NotNull CachedDataKey<PsiFileCachedData, T> dataKey) {
        PsiFileCachedData cachedData = ProjectCachedData.getInstance(psiFile.getProject()).getFileCachedData(psiFile);
        //noinspection unchecked
        return (T) cachedData.getCachedData().getOrNull(dataKey);
    }

    @NotNull
    public static <T> T get(@NotNull Project project, @NotNull CachedDataKey<ProjectCachedData, T> dataKey) {
        ProjectCachedData cachedData = ProjectCachedData.getInstance(project);
        return getInstance().myManager.get().get(cachedData, dataKey);
    }

    @Nullable
    public static <T> T getOrNull(@NotNull Project project, @NotNull CachedDataKey<ProjectCachedData, T> dataKey) {
        ProjectCachedData cachedData = ProjectCachedData.getInstance(project);
        //noinspection unchecked
        return (T) cachedData.getCachedData().getOrNull(dataKey);
    }

    @NotNull
    public static DataDependency dependency(@NotNull Object dependent) {
        return getInstance().getDependency(dependent);
    }
}
