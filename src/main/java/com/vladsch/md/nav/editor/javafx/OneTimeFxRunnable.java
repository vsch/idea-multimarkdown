// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.javafx;

import com.vladsch.plugin.util.CancelableJobScheduler;
import com.vladsch.plugin.util.CancellableRunnable;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Used to create a task that can be run at most once and the run can be cancelled before it has run, in which case further attempts to run it will do nothing.
 * <p>
 * Can also specify that it should run on the AWT thread, otherwise it will run on the application thread
 * <p>
 * Useful for triggering actions after a delay that may need to be run before the delay triggers
 */
public class OneTimeFxRunnable implements CancellableRunnable {
    final private Runnable myCommand;
    final private AtomicBoolean myHasRun;
    final private String myID;

    public OneTimeFxRunnable(String id, Runnable command) {
        myID = id;
        myCommand = command;
        myHasRun = new AtomicBoolean(false);
    }

    /**
     * Cancels the scheduled task run if it has not run yet
     *
     * @return true if cancelled, false if it has already run
     */
    @Override
    public boolean cancel() {
        return !myHasRun.getAndSet(true);
    }

    /**
     * Tests whether it has run or been cancelled
     *
     * @return true if cancelled, false if it has already run
     */
    @Override
    public boolean canRun() {
        return !myHasRun.get();
    }

    @Override
    @NotNull
    public String getId() {
        return myID;
    }

    @Override
    public void run() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this);
        } else {
            if (!myHasRun.getAndSet(true)) {
                myCommand.run();
            }
        }
    }

    /**
     * Creates a one-shot runnable that will run after a delay, can be run early, or cancelled
     * <p>
     * the given command will only be executed once, either by the delayed trigger or by the run method. if you want to execute the task early just invoke #run, it will do nothing if the task has already run.
     *
     * @param scheduler
     * @param command   the task to execute
     * @param delay     the time from now to delay execution
     *
     * @return a {@link OneTimeFxRunnable} which will run after the given delay or if {@link #run()} is invoked before {@link #cancel()} is invoked
     *
     * @throws NullPointerException if command is null
     */
    public static OneTimeFxRunnable schedule(final CancelableJobScheduler scheduler, @NotNull String id, @NotNull Runnable command, int delay) {
        OneTimeFxRunnable runnable = command instanceof OneTimeFxRunnable ? (OneTimeFxRunnable) command : new OneTimeFxRunnable(id, command);
        scheduler.schedule(runnable.getId(), delay, runnable);
        return runnable;
    }
}
