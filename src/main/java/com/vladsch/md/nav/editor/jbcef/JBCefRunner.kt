// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.jbcef

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.jcef.JBCefBrowser
import com.vladsch.plugin.util.CancellableRunnable
import com.vladsch.plugin.util.OneTimeRunnable
import com.vladsch.plugin.util.debug
import com.vladsch.plugin.util.timeIt
import org.cef.browser.CefBrowser
import org.cef.handler.CefLoadHandlerAdapter

/**
 * Class used to handle loading of documents, running initialization scripts and in-page scripts after the first two have run
 * all without worrying about the state of WebView being ready. Just ask for what you want when you want it and this class will handle it, either right away
 * or when WebView is ready. All methods are callable from any thread. All runnables will be executed in the isFxApplicationThread.
 *
 * Document Loading interrupts document load and initialization but not page interactions to allow for clean saving of page parameters
 *
 * Initializer is cancellable by requesting another initializer but it will not interrupt
 * the one already running since it may screw up the page. if you want to initialize the document
 * and run another script, then schedule the second script from the body of the first.
 * It will run after the first one is complete
 * The document state will be LOADED until the last initializer invokes the {@link #setInitialized} method to signal that page interactors
 * can now be scheduled
 *
 * PageInteractions are also cancelable by other interactions but not interruptible, they will simply run after the last one has completed
 *
 * On failing or cancelling, DocumentState will:
 * Document Loader -> NONE
 * Initializer  -> LOADED
 * Interaction  -> READY
 *
 * A Document loader restarts the process so it is the only one that can do it
 * Init Scripts if they fail will not schedule anything until another init script runs successfully
 * Page scripts can fail and simply the next one will be run
 */
class JBCefRunner(jbCefBrowser: JBCefBrowser) : Disposable {

    enum class Type {
        LOADER,
        INITIALIZER,
        PAGE_INTERACTION;
    }

    enum class DocumentState {
        NONE, // no document loaded
        LOADING, // loading started
        LOADED, // document loaded, transitioning to INITIALIZING as soon as there is an initializer to run
        INITIALIZING, // loading done, running init script if there was one scheduled before loading completed
        INITIALIZED, // dummy state to transition to READY once the worker reports success
        READY, // document loaded  and init scripts have run
        INTERACTING,
        CANCELLED;  // cancelled
    }

    private val myWorker: JBCefBrowser = jbCefBrowser
    private val myCefLoadHandler: CefLoadHandlerAdapter

    init {
        myWorker.getJBCefClient().addLoadHandler(object : CefLoadHandlerAdapter() {
            override fun onLoadingStateChange(browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
                workerStateChanged(browser, isLoading, canGoBack, canGoForward)
            }
        }.also { myCefLoadHandler = it }, myWorker.cefBrowser)
    }

    private var myLoader = CancellableRunnable.NULL
    private var myInitializer = CancellableRunnable.NULL
    private var myPageInteraction = CancellableRunnable.NULL
    private val REQUESTS_LOCK = Object()
    private var myDocumentState: DocumentState = DocumentState.NONE

    private var myIsLoading: Boolean = false
    private var myLastRunnable: CancellableRunnable = CancellableRunnable.NULL
    private var myLastType: Type = Type.LOADER
    private var myLastStart: Long = 0L
    private var myIsPanelInitialized = false

    fun panelInitialized() {
        myIsPanelInitialized = true
    }

    val documentState: DocumentState get() = myDocumentState
    val workerState: Boolean
        get() {
            var workerState: Boolean?
            synchronized(REQUESTS_LOCK) {
                workerState = myIsLoading
            }
            return workerState!!
        }

    private fun workerStateChanged(browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean) {
        val endTime = System.nanoTime()

        LOG.debug { "WorkerStateChanged isLoading: $isLoading, was $myIsLoading" }

        synchronized(REQUESTS_LOCK) {
            myIsLoading = isLoading
        }

        if (myLastRunnable.isNotNull && !myIsLoading) {
            profile.debug { "$myLastType ${myLastRunnable.id} $myIsLoading" + String.format(" in %3.3fms", (endTime - myLastStart) / 10000000.0) }
            myLastRunnable = CancellableRunnable.NULL
        }
        scheduleWorkRaw()
    }

    override fun dispose() {
        myWorker.jbCefClient.removeLoadHandler(myCefLoadHandler, myWorker.cefBrowser);
    }

    fun pageReloadTriggered() {
        synchronized(REQUESTS_LOCK) {
            LOG.debug { "pageReloadTriggered() docState: $myDocumentState" }
            val id = "pageReloadTriggered"
            if (myLoader.cancel()) LOG.debug { "cancelled by $id: ${myLoader.id}" }
            if (myInitializer.cancel()) LOG.debug { "cancelled by $id: ${myInitializer.id}" }
            if (myPageInteraction.cancel()) LOG.debug { "cancelled by $id: ${myPageInteraction.id}, docState: $myDocumentState" }
            myLoader = CancellableRunnable.NULL
            myInitializer = CancellableRunnable.NULL
            myPageInteraction = CancellableRunnable.NULL

            myLoader.cancel()
            myInitializer.cancel()
            myPageInteraction.cancel()

            myDocumentState = DocumentState.LOADING

            synchronized(REQUESTS_LOCK) {
                myIsLoading = myWorker.cefBrowser.isLoading
            }
            myLastType = Type.LOADER
        }
    }

    /**
     * Used to interrupt all work in preparation for a new document load
     */
    fun cancelAll(id: String) {
        var cancelWorker = false

        synchronized(REQUESTS_LOCK) {
            if (myDocumentState != DocumentState.NONE && myDocumentState != DocumentState.CANCELLED) {
                LOG.debug { "cancelAll($id) docState: $myDocumentState" }

                if (myLoader.cancel()) LOG.debug { "cancelled by $id: ${myLoader.id}" }
                if (myInitializer.cancel()) LOG.debug { "cancelled by $id: ${myInitializer.id}" }
                if (myPageInteraction.cancel()) LOG.debug { "cancelled by $id: ${myPageInteraction.id}, docState: $myDocumentState" }

                myLoader = CancellableRunnable.NULL
                myInitializer = CancellableRunnable.NULL
                myPageInteraction = CancellableRunnable.NULL
                myDocumentState = DocumentState.CANCELLED

                cancelWorker = true
            }
        }

        if (cancelWorker) {
            synchronized(REQUESTS_LOCK) {
                LOG.debug { "cancelling worker by $id" }
                myWorker.cefBrowser.stopLoad()
                myIsLoading = false
            }
        }
    }

    fun cancel(id: String, type: Type) {
        when (type) {
            Type.LOADER -> cancelAll(id)
            Type.INITIALIZER -> {
                synchronized(REQUESTS_LOCK) {
                    if (myInitializer.cancel()) LOG.debug { "cancelled by $id: ${myInitializer.id}" }
                    myInitializer = CancellableRunnable.NULL
                }
            }

            Type.PAGE_INTERACTION -> {
                synchronized(REQUESTS_LOCK) {
                    if (myPageInteraction.cancel()) LOG.debug { "cancelled by $id: ${myPageInteraction.id}, docState: $myDocumentState" }
                    myPageInteraction = CancellableRunnable.NULL
                }
            }
        }
    }

    private fun prepareRunnable(id: String, type: Type, runnable: () -> Unit) {
        synchronized(REQUESTS_LOCK) {
            when (type) {
                Type.LOADER -> {
                    // new document request, we cancel all and reload
                    cancelAll(id)
                    myLoader = OneTimeRunnable(id) {
                        synchronized(REQUESTS_LOCK) {
                            myDocumentState = DocumentState.LOADING
                            myLoader = CancellableRunnable.NULL
                        }
                        try {
                            profile.timeIt({ "running loader: ${id}" }, runnable)
                            synchronized(REQUESTS_LOCK) {
                                myDocumentState = DocumentState.LOADED
                            }
                        } catch (e: Throwable) {
                            LOG.debug(e)
                            synchronized(REQUESTS_LOCK) {
                                myDocumentState = DocumentState.NONE
                            }
                        }
                        if (myWorker.cefBrowser.isLoading == false) {
                            scheduleWorkRaw()
                        }
                    }
                }
                Type.INITIALIZER -> {
                    if (myInitializer.cancel()) LOG.debug { "cancelled by $id: ${myInitializer.id}" }
                    myInitializer = OneTimeRunnable(id) {
                        synchronized(REQUESTS_LOCK) {
                            myDocumentState = DocumentState.INITIALIZING
                            myInitializer = CancellableRunnable.NULL
                        }
                        try {
                            profile.timeIt({ "running initializer: ${id}" }, runnable)
                            synchronized(REQUESTS_LOCK) {
                                if (myDocumentState == DocumentState.INITIALIZING) {
                                    myDocumentState = DocumentState.LOADED
                                }
                            }
                        } catch (e: Throwable) {
                            LOG.debug(e)
                            synchronized(REQUESTS_LOCK) {
                                myDocumentState = DocumentState.LOADED
                            }
                            if (myWorker.cefBrowser.isLoading == false) {
                                scheduleWorkRaw()
                            }
                        }
                    }
                }
                Type.PAGE_INTERACTION -> {
                    myPageInteraction = OneTimeRunnable(id) {
                        synchronized(REQUESTS_LOCK) {
                            myDocumentState = DocumentState.INTERACTING
                            myPageInteraction = CancellableRunnable.NULL
                        }
                        try {
                            profile.timeIt({ "running interaction: ${id}" }, runnable)
                        } catch (e: Throwable) {
                            LOG.debug(e)
                        }
                        synchronized(REQUESTS_LOCK) {
                            myDocumentState = DocumentState.READY
                        }
                        if (myWorker.cefBrowser.isLoading == false) {
                            scheduleWorkRaw()
                        }
                    }
                }
            }
        }
    }

    fun setInitialized() {
        var wasInitialized = true

        synchronized(REQUESTS_LOCK) {
            if (myDocumentState in listOf(DocumentState.LOADED, DocumentState.INITIALIZING)) {
                LOG.debug { "Initialized" }
                myDocumentState = DocumentState.INITIALIZED
                wasInitialized = false
            }
        }

        if (!wasInitialized) {
            scheduleWork()
        } else {
            LOG.debug { "was already set to initialized!!" }
        }
    }

    fun scheduleWork() {
        if (!myIsPanelInitialized) return

        var schedule: Boolean

        synchronized(REQUESTS_LOCK) {
            schedule = !myIsLoading
        }

        if (schedule) scheduleWorkRaw()
    }

    private fun scheduleWorkRaw() {
//        val dispatchThread = ApplicationManager.getApplication().isDispatchThread
//        if (!dispatchThread) {
//            val tmp = 0
//        }
//        assert(dispatchThread)

        var nextRunner = CancellableRunnable.NULL
        var nextType = Type.LOADER

        LOG.debug { "scheduleWorkRaw in: workerState: $myIsLoading, documentState: $myDocumentState" }

        synchronized(REQUESTS_LOCK) {
            do {
                var doneLooping = true

                when (myIsLoading) {
                    false -> {
                        when (myDocumentState) {
                            DocumentState.NONE -> {
                                nextRunner = myLoader
                                nextType = Type.LOADER
                                doneLooping = true
                            }
                            DocumentState.LOADING -> {
                                myDocumentState = DocumentState.LOADED
                                doneLooping = false
                            }
                            DocumentState.LOADED -> {
                                nextRunner = myInitializer
                                nextType = Type.INITIALIZER
                                doneLooping = true
                            }
                            DocumentState.INITIALIZING -> {
                                myDocumentState = DocumentState.LOADED
                                doneLooping = false
                            }
                            DocumentState.INITIALIZED -> {
                                myDocumentState = DocumentState.READY
                                doneLooping = false
                            }
                            DocumentState.READY -> {
                                nextRunner = myPageInteraction
                                nextType = Type.PAGE_INTERACTION
                                doneLooping = true
                            }
                            DocumentState.INTERACTING -> {
                                myDocumentState = DocumentState.READY
                                doneLooping = false
                            }
                            DocumentState.CANCELLED -> {
                                myDocumentState = DocumentState.NONE
                                doneLooping = false
                            }
                        }
                    }
                    true -> {
                    }
                }
            } while (!doneLooping)
        }

        if (nextRunner.isNotNull) {
            if (nextRunner.canRun()) {
                LOG.debug { "running next: ${nextRunner.id}" }
                myLastRunnable = nextRunner
                myLastType = nextType
                myLastStart = System.nanoTime()
                nextRunner.run()
            }
        }
        LOG.debug { "scheduleWorkRaw out: workerState: $myIsLoading, documentState: $myDocumentState" }
    }

    fun fxRun(runnable: () -> Unit) {
        val application = ApplicationManager.getApplication()
        if (!application.isDispatchThread) {
            application.invokeLater(runnable)
        } else {
            runnable()
        }
    }

    fun schedule(id: String, type: Type, runnable: () -> Unit) {
        if (!myIsPanelInitialized) return

        LOG.debug { "schedule in: $id, type: $type, workerState: $myIsLoading, documentState: $myDocumentState" }
        prepareRunnable(id, type, runnable)
        scheduleWork()
    }

    companion object {
        private val LOG = Logger.getInstance("com.vladsch.md.nav.editor.jcef.runner")
        private val profile = Logger.getInstance("com.vladsch.md.nav.editor.jcef.profile")
    }
}
