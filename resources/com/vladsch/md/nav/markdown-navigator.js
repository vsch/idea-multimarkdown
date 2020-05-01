//# sourceURL=__MarkdownNavigatorHelperModuleSource__
/*
 *   The MIT License (MIT)
 *   <p>
 *   Copyright (c) 2018-2018 Vladimir Schneider (https://github.com/vsch)
 *   <p>
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   <p>
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *   <p>
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE
 *
 */

// noinspection ES6ConvertVarToLetConst
var firstCollapsedHeading = () => {
};
// noinspection ES6ConvertVarToLetConst
var scrollToSourcePosition = () => {
};

// noinspection ES6ConvertVarToLetConst
var markdownNavigator;

// do nothing until jsBridge is established
// noinspection ES6ConvertVarToLetConst
var debugBreak = () => {
};

// lets keep this around for our scripts
// noinspection ES6ConvertVarToLetConst
var __api = console.__commandLineAPI || {};

// map console functions to our bridge
// noinspection ES6ConvertVarToLetConst
var console = (() => {
    let __tmp = {
        logDebugBreak: () => {
        },
        consoleLog: (type, args) => {
            markdownNavigator.consoleLog(type, args);
        },
        println: text => markdownNavigator.println(text),
        print: text => markdownNavigator.print(text),
    };

    return {
        assert: function () { return __tmp.consoleLog("assert", arguments); },
        clear: function () { return __tmp.consoleLog("clear", arguments); },
        count: function () { return __tmp.consoleLog("count", arguments); },
        debug: function () { return __tmp.consoleLog("debug", arguments); },
        dir: function () { return __tmp.consoleLog("dir", arguments); },
        dirxml: function () { return __tmp.consoleLog("dirxml", arguments); },
        error: function () { return __tmp.consoleLog("error", arguments); },
        exception: function () { return __tmp.consoleLog("exception", arguments); },
        group: function () { return __tmp.consoleLog("startGroup", arguments); },
        groupCollapsed: function () { return __tmp.consoleLog("startGroupCollapsed", arguments); },
        groupEnd: function () { return __tmp.consoleLog("endGroup", arguments); },
        info: function () { return __tmp.consoleLog("info", arguments); },
        log: function () { return __tmp.consoleLog("log", arguments); },
        profile: function () { return __tmp.consoleLog("profile", arguments); },
        profileEnd: function () { return __tmp.consoleLog("profileEnd", arguments); },
        select: function () { return __tmp.consoleLog("select", arguments); },
        table: function () { return __tmp.consoleLog("table", arguments); },
        time: function () { return __tmp.consoleLog("time", arguments); },
        timeEnd: function () { return __tmp.consoleLog("timeEnd", arguments); },
        trace: function () { return __tmp.consoleLog("trace", arguments); },
        warn: function () { return __tmp.consoleLog("warning", arguments); },
        print: text => __tmp.print(text),
        println: text => __tmp.println(text),
        setJsBridge: (jsBridge) => {
            __tmp = jsBridge;
            // function for cached logs
            return ((type, args) => {
                __tmp.consoleLog(type, args);
            });
        },
    };
})();

// noinspection JSValidateTypes
window.console = console;

markdownNavigator = (function () {
    "use strict";

    // FIX: change this for real implementation using computed CSS properties of element
    // and an overlay element
    const HIGHLIGHT = "markdown-navigator-highlight";
    const HIGHLIGHT_STYLE = document.createElement("style");

    // just so we get a color chooser in IDEA, uncomment
    HIGHLIGHT_STYLE.textContent = `.${HIGHLIGHT} {
    background-color: rgba(255, 0, 255, 0.07) !important;
}`;

    let __markdownNavigator,
        __tmp = {
            __state: {},
            __onJsBridge: [],
            __onJsConsole: [],
            __consoleSetJsBridge: console.setJsBridge, // functions to use before JSBridge is established
            onJsBridge: () => {
            },
            onJsConsole: () => {
            },
        },
        __consoleLog = (type, args) => {
        },
        __lastHighlight = null;

    delete console["setJsBridge"];

    __tmp.onJsBridge = op => {
        __tmp.__onJsBridge[__tmp.__onJsBridge.length] = op;
    };

    __tmp.onJsConsole = op => {
        __tmp.__onJsConsole[__tmp.__onJsConsole.length] = op;
    };

    let __unbridged = {
        // functions to be replaced with real ones when JsBridge is established
        setEventHandledBy: handledBy => {
        },

        getState: name => {
            return __tmp.__state[name] || null;
        },

        setState: (name, state) => {
            __tmp.__state[name] = state;
        },

        toggleTask: function (pos) {
            __tmp.onJsBridge(() => {
                __markdownNavigator.toggleTask(pos);
            });
        },

        onJsBridge: function (op) {
            __tmp.onJsBridge(op);
        },

        // functions mimicking jsBridge until it is connected
        consoleLog: (type, args) => {
            __tmp.onJsConsole(() => {
                __consoleLog(type, args);
            });
        },

        println: text => {
            __tmp.onJsConsole(() => {
                console.println(text);
            });
        },

        print: text => {
            __tmp.onJsConsole(() => {
                console.print(text);
            });
        },

        highlightNode: (nodeOrdinals) => {
            function getChildNode(node, nodeOrdinal) {
                let iMax = node.childNodes.length;
                let index = 0;
                for (let i = 0; i < iMax; i++) {
                    let child = node.childNodes.item(i);
                    if (child.nodeName.startsWith("#") && (child.nodeName !== "#text" || child.textContent.trim() === "")) {
                        // skip non-element nodes or empty text
                        continue;
                    }

                    if (index === nodeOrdinal) {
                        return child;
                    }
                    index++;
                }
                return null;
            }

            // need to find the node, path is ordinal in parent, in reverse order, all the way to document, first index to be ignored
            let iMax = nodeOrdinals.length;
            let node = document;
            try {
                for (let i = iMax; i-- > 1;) {
                    let nodeOrdinal = nodeOrdinals[i];
                    let child = getChildNode(node, nodeOrdinal);

                    if (child === null) {
                        if (i === 1) {
                            // if last one take the parent
                            break;
                        }

                        console.error("Node ordinal not in children", nodeOrdinal, node, nodeOrdinals);
                        node = null;
                        break;
                    }

                    if (child.nodeName.startsWith("#")) {
                        // FIX: when overlays are implemented to handle non-element nodes, highlight the child
                        break;
                    }

                    node = child;
                }
                // console.debug("Final node", node);
                if (node !== null && node !== __lastHighlight) {
                    __unbridged.hideHighlight();
                    node.classList.add(HIGHLIGHT);
                    __lastHighlight = node;
                }
            } catch (e) {
                console.error(e)
            }
        },

        hideHighlight: () => {
            if (__lastHighlight) {
                __lastHighlight.classList.remove(HIGHLIGHT);
                if (__lastHighlight.classList.length === 0) {
                    __lastHighlight.removeAttribute("class");
                }
                __lastHighlight = null;
            }
        },

        setJsBridge: jsBridge => {
            // map to real JsBridge
            __consoleLog = __tmp.__consoleSetJsBridge(jsBridge);
            __markdownNavigator = jsBridge;
            debugBreak = jsBridge.debugBreak;

            // we don't need these anymore
            delete __unbridged["consoleLog"];
            delete __unbridged["setJsBridge"];
            delete __unbridged["println"];
            delete __unbridged["print"];

            // these now point directly to the jsBridge implementations or invocations
            __unbridged.setEventHandledBy = (name) => jsBridge.setEventHandledBy(name);
            __unbridged.getState = (name) => jsBridge.getState(name);
            __unbridged.setState = (name, state) => jsBridge.setState(name, state);
            __unbridged.toggleTask = position => jsBridge.toggleTask(position);
            __unbridged.onJsBridge = op => op();

            document.querySelector("head").appendChild(HIGHLIGHT_STYLE);
            console.debug(`Created ${HIGHLIGHT} style element`, HIGHLIGHT_STYLE);

            // dump any accumulated console/print before bridge connected
            for (const __onJsConsoleItem of __tmp.__onJsConsole) {
                try {
                    __onJsConsoleItem();
                } catch (e) {
                    console.println("onJsConsole exception: calling " + __onJsConsoleItem);
                    console.println("onJsConsole exception: " + e);
                }
            }

            console.groupCollapsed("markdownNavigator:OnLoad");

            // save any state changes requested before jsBridge was setup
            console.groupCollapsed("cachedState");
            for (let f in __tmp.__state) {
                if (__tmp.__state.hasOwnProperty(f)) {
                    console.log(f, __tmp.__state[f]);
                    // console.println(name + " = " + JSON.stringify( __state[name],null,2));
                    __markdownNavigator.setState(f, __tmp.__state[f]);
                }
            }
            console.groupEnd();

            // run any ops needed on connection
            for (const __onJsBridgeItem of __tmp.__onJsBridge) {
                console.debug("onLoad", __onJsBridgeItem);
                try {
                    __onJsBridgeItem();
                } catch (e) {
                    console.println("onJsBridge exception: calling " + __onJsBridgeItem);
                    console.println("onJsBridge exception: " + e);
                }
            }
            console.groupEnd();

            __tmp = null;

            // signal JsBridge connection complete
            __markdownNavigator.pageLoadComplete();
        },
    };

    return __unbridged;
})();

