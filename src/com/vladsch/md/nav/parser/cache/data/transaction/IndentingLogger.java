// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.transaction;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.vladsch.flexmark.util.misc.Utils;
import com.vladsch.md.nav.parser.cache.CachedData;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

public class IndentingLogger extends Logger {
    final public static String ACTIVITY = "com.vladsch.md.nav.cache";
    final public static String COMPUTE = ACTIVITY + ".compute";
    final public static String COMPUTE_RESULT = ACTIVITY + ".compute.result.summary";
    final public static String COMPUTE_DETAIL = ACTIVITY + ".compute.result";
    final public static String DEPENDENCY = ACTIVITY + ".dependency";
    final public static String INVALIDATION = ACTIVITY + ".invalidation";

    public static final String INDENT = "  ";
    private static boolean WANT_CACHE_LOGGING = ApplicationManager.getApplication() != null && !ApplicationManager.getApplication().isUnitTestMode();
    private static boolean WANT_CACHE_TRACE = false;
    private static boolean WANT_TIMESTAMP = true;

    // these are in transactions
    public static IndentingLogger LOG_COMPUTE = new IndentingLogger(Logger.getInstance(COMPUTE));
    public static IndentingLogger LOG_COMPUTE_RESULT = new IndentingLogger(Logger.getInstance(COMPUTE_RESULT));
    public static IndentingLogger LOG_COMPUTE_DETAIL = new IndentingLogger(Logger.getInstance(COMPUTE_DETAIL));
    public static IndentingLogger LOG_DEPENDENCY = new IndentingLogger(Logger.getInstance(DEPENDENCY));

    public static boolean getWantTimeStamp() {
        return WANT_TIMESTAMP;
    }

    private static StringBuilder ourLogCapture = null;

    private final @NotNull Logger myLogger;

    public static void setWantCacheLogging(boolean wantDebugLogging, boolean wantCacheStackTrace, boolean wantTimestamp) {
        WANT_CACHE_LOGGING = wantDebugLogging;
        WANT_CACHE_TRACE = wantCacheStackTrace;
        WANT_TIMESTAMP = wantTimestamp;
    }

    public static void setLogCapture(@Nullable StringBuilder builder) {
        ourLogCapture = builder;
    }

    public IndentingLogger(@NotNull Logger logger) {
        myLogger = logger;
    }

    @NotNull
    public Logger getLogger() {
        return myLogger;
    }

    @NotNull
    private LogIndenter getIndenter() {
        return CachedData.getLogIndenter();
    }

    @Override
    public boolean isDebugEnabled() {
        return WANT_CACHE_LOGGING && myLogger.isDebugEnabled();
    }

    public boolean isStackTraceEnabled() {
        return WANT_CACHE_TRACE;
    }

    public void debug(@NotNull Supplier<String> message) { if (isDebugEnabled()) debug(message.get());}

    public void debug(@NotNull Supplier<String> message, @Nullable Throwable t) {debug(message.get(), t);}

    public void debug(@NotNull Supplier<String> message, @NotNull Object... details) {debug(message.get(), details);}

    @Override
    public void debug(String message) {
        if (isDebugEnabled()) {
            String indentMessage = getIndenter().indentMessage(message, false, WANT_TIMESTAMP);
            myLogger.debug(indentMessage);

            if (ourLogCapture != null) {
                synchronized (myLogger) {
                    ourLogCapture.append(Utils.suffixWith(indentMessage, "\n"));
                }
            }
        }
    }

    @Override
    public void debug(@Nullable Throwable t) {
        if (isDebugEnabled() && t != null) {
            debug("", t);
        }
    }

    @Override
    public void debug(String message, @Nullable Throwable t) {
        if (isDebugEnabled()) {
            if (t != null) {
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    PrintStream printStream = new PrintStream(byteArrayOutputStream);
                    if (!message.isEmpty()) printStream.println(message);
                    printStream.println(t.getMessage());
                    t.printStackTrace(printStream);
                    printStream.close();
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();

                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    String s = new String(bytes, "UTF-8");
                    debug(s);
                } catch (Exception ignored) {
                }
            } else {
                debug(message);
            }
        }
    }

    @Override
    public void info(String message) {myLogger.info(getIndenter().indentMessage(message, false, false));}

    @Override
    public void info(String message, @Nullable Throwable t) {myLogger.info(getIndenter().indentMessage(message, false, false), t);}

    @Override
    public void warn(String message, @Nullable Throwable t) {myLogger.warn(getIndenter().indentMessage(message, false, false), t);}

    @Override
    public void error(String message, @Nullable Throwable t, @NotNull String... details) {myLogger.error(getIndenter().indentMessage(message, false, false), t, details);}

    @Override
    public void setLevel(Level level) {myLogger.setLevel(level);}
}
