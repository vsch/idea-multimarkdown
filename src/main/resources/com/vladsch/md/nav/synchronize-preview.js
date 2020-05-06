// on load script to synchronize preview
var markdownNavigator;
var firstCollapsedHeading;  // if set then a function taking 1 element, see github-collapse-markdown.js
var scrollToSourcePosition;  // if set then a function taking 1 element, see github-collapse-markdown.js

(function () {
    "use strict";

    const HIGHLIGHT = "focus-highlight";
    let lastHighlighted = null;
    let highlightFadeTimeout = null;
    let scrollTimeout = null;
    let scrolledToSourcePosition = false;

    function clearLastHighlight() {
        if (highlightFadeTimeout !== null) {
            window.clearTimeout(highlightFadeTimeout);
            highlightFadeTimeout = null;
        }

        if (lastHighlighted) {
            if (lastHighlighted.classList.contains(HIGHLIGHT)) {
                lastHighlighted.classList.toggle(HIGHLIGHT);
                if (lastHighlighted.classList.length === 0) {
                    lastHighlighted.removeAttribute("class");
                }
            }
            lastHighlighted = null;
        }
    }

    function highlightElement(highlightElem, highlightFadeOut) {
        clearLastHighlight();
        lastHighlighted = highlightElem;
        console.debug('highlightElement: ', highlightElem);
        lastHighlighted.classList.add(HIGHLIGHT);

        if (highlightFadeOut > 0) {
            highlightFadeTimeout = window.setTimeout(function () {
                highlightFadeTimeout = null;
                clearLastHighlight();
            }, highlightFadeOut)
        }
    }

    scrollToSourcePosition = function (verticalLocation, scrollTag, scrollAttribute, scrollReference, highlightEnabled, onTypingUpdate, highlightOnTyping, highlightFadeOut) {
        if (scrollTimeout) {
            window.clearTimeout(scrollTimeout);
        }
        scrollTimeout = window.setTimeout(function () {
            scrollTimeout = null;
            scrollToSourcePositionRaw(verticalLocation, scrollTag, scrollAttribute, scrollReference, highlightEnabled, onTypingUpdate, highlightOnTyping, highlightFadeOut);
        }, 250);
    };

    function scrollToSourcePositionRaw(verticalLocation, scrollTag, scrollAttribute, scrollReference, highlightEnabled, onTypingUpdate, highlightOnTyping, highlightFadeOut) {
        //    markdownNavigator.debug('scrollToPosition(' + verticalLocation + ', ' + scrollTag + ', ' + scrollAttribute + ', ' + scrollReference + ')');
        try {
            if (!(scrollTag !== '' && scrollAttribute !== '' && scrollReference !== '')) {
                return;
            }

            let elemTop = 0;
            let elements = null;
            let margin = 50;
            let elemList = window.document.getElementsByTagName(scrollTag);
            let highlightElem = null;
            let elementCount = 0;

            for (let a in elemList) {
                if (!elemList.hasOwnProperty(a)) {
                    continue;
                }

                let aElem = elemList[a];
                try {
                    if (aElem.hasAttribute(scrollAttribute) && aElem.getAttribute(scrollAttribute) === scrollReference) {
                        elements = aElem;
                        elementCount++;
                        if (aElem.tagName !== "SPAN" || elementCount > 1) {
                            break;
                        }
                    }
                } catch (e) {
                }
            }

            if (elements) {
                let theElem = elements;
                let inTable = false;
                let tagName;

                while (elements && elements.tagName !== 'HTML') {
                    if (elements.tagName === 'TABLE') {
                        inTable = true;
                        break;
                    }

                    elements = elements.parentNode
                }

                elements = theElem;
                let highlightElemSet = false;
                tagName = elements.tagName;
                while (elements && tagName !== 'HTML') {
                    let style = window.getComputedStyle(elements),
                        position = style.getPropertyValue('position');

                    //                markdownNavigator.debug("elems: offsetTop:" + elements.offsetTop + ' elementTag:' + tagName + ' position:' + position);

                    if (!highlightElemSet && (!inTable || tagName === 'TR')) {
                        highlightElem = elements;
                    }

                    if (elements.offsetTop) {
                        if (tagName !== 'TD' && (tagName !== 'DETAILS' || !elements.attributes.hasOwnProperty("open"))) {
                            elemTop += elements.offsetTop;
                        }

                        if (tagName === 'SPAN') {
                            if (!inTable) {
                                highlightElemSet = true;
                                let parentTag = elements.parentNode.tagName;
                                if (parentTag !== 'H1' && parentTag !== 'H2' && parentTag !== 'H3' && parentTag !== 'H4' && parentTag !== 'H5' && parentTag !== 'H6') {
                                    break;
                                }
                            }
                        } else {
                            if (!inTable) {
                                highlightElemSet = true;
                                if (!highlightElem) {
                                    highlightElem = elements;
                                }
                                if (position !== 'relative') {
                                    break;
                                }
                                if (!(tagName !== 'PRE' && tagName !== 'H1' && tagName !== 'H2' && tagName !== 'H3' && tagName !== 'H4' && tagName !== 'H5' && tagName !== 'H6')) {
                                    break;
                                }
                            } else {
                                if (tagName === 'TABLE') {
                                    break;
                                }
                            }
                        }
                    } else {
                        if (firstCollapsedHeading) {
                            let firstHeading = firstCollapsedHeading(elements);
                            if (firstHeading) {
                                highlightElemSet = true;
                                highlightElem = firstHeading;
                                elements = highlightElem;
                                elemTop = highlightElem.offsetTop;
                            }
                        }
                    }
                    elements = elements.parentNode;
                    tagName = elements.tagName;
                }

                if (!highlightElem) {
                    highlightElem = elements;
                }

                // need to clear highlight before computing scrollTo position otherwise the highlight
                // can change its size and make the page jump
                clearLastHighlight();

                let pageTop = window.pageYOffset;
                let pageHeight = window.innerHeight;
                let pageBottom = pageTop + pageHeight;
                let elemHeight = highlightElem.offsetHeight;
                let elemBottom = elemTop + elemHeight;

                //console.debug("elementTop:" + elemTop + ', elementBottom: ' + elemBottom + ' pageTop:' + pageTop + ' pageBottom:' + pageBottom + ' elementTag:' + highlightElem.tagName + ' parentTag:' + elements.tagName);

                if (verticalLocation !== null) {
                    // place at % of height the same as caret
                    let elemCenter = (elemTop + elemBottom) / 2;
                    let centerMargin = 0;
                    let pageLocation = (pageHeight - centerMargin * 2) * verticalLocation / 100;
                    let scrollTo;

                    //console.debug('verticalLocation:' + verticalLocation + ', elementCenter:' + elemCenter + ', pageLocation: ' + pageLocation + ' pageTop:' + pageTop + ' pageBottom:' + pageBottom);

                    scrollTo = elemCenter > pageLocation + centerMargin ? elemCenter - centerMargin - pageLocation : 0;
                    let onLoadScroll = {x: window.pageXOffset, y: scrollTo};
                    markdownNavigator.setState("onLoadScroll", onLoadScroll);

                    scrolledToSourcePosition = true;
                    window.scrollTo(window.pageXOffset, scrollTo);
                } else {
                    if (elemTop < pageTop + margin || elemBottom + margin > pageBottom) {
                        if (elemTop <= pageTop + margin && elemBottom + margin > pageBottom) {
                            console.debug("already visible:" + elemTop + ', elementBottom: ' + elemBottom + ' elementTag:' + ' pageTop:' + pageTop + ' pageBottom:' + pageBottom);
                        } else {
                            // already in view but too big to fit
                            let scrollTo;
                            if (elemBottom < pageTop) {
                                // scroll down to show element top, do minimal scroll to show bottom and top if fits into view or just bottom if not
                                if (elemHeight <= pageHeight) {
                                    // scroll to show element top
                                    scrollTo = elemTop >= margin ? elemTop - margin : 0;
                                } else {
                                    // scroll to show element bottom
                                    scrollTo = pageTop + elemBottom - pageBottom + margin;
                                }
                            } else {
                                if (elemTop < pageTop + margin) {
                                    // scroll up to show element top
                                    scrollTo = elemTop >= margin ? elemTop - margin : 0;
                                } else {
                                    if (elemTop >= pageBottom) {
                                        // scroll up to show element top, do minimal scroll to show bottom and top if fits into view or just top if not
                                        if (elemHeight <= pageHeight) {
                                            // scroll to show element bottom
                                            scrollTo = pageTop + elemBottom - pageBottom + margin;
                                        } else {
                                            // scroll to show element top
                                            scrollTo = elemTop >= margin ? elemTop - margin : 0;
                                        }
                                    } else {
                                        if (elemBottom + margin >= pageBottom) {
                                            // scroll up to show element bottom
                                            scrollTo = pageTop + elemBottom - pageBottom + margin;
                                        } else {
                                            // all conditions should have been handled
                                            console.error("unexpected path:" + elemTop + ', elementBottom: ' + elemBottom + ' elementTag:' + ' pageTop:' + pageTop + ' pageBottom:' + pageBottom);
                                        }
                                    }
                                }
                            }

                            let onLoadScroll = {x: window.pageXOffset, y: scrollTo};
                            markdownNavigator.setState("onLoadScroll", onLoadScroll);

                            scrolledToSourcePosition = true;
                            window.scrollTo(window.pageXOffset, scrollTo);
                        }
                    }
                }

                clearLastHighlight();

                if (highlightEnabled && (!onTypingUpdate || highlightOnTyping)) {
                    highlightElement(highlightElem, highlightFadeOut);
                }
            }
        } catch (e) {
            console.error("exception in scrollToSourcePosition " + e);
        }
    }

    markdownNavigator.onJsBridge(() => {
        window.addEventListener('scroll', function () {
            if (!scrolledToSourcePosition && clearLastHighlight) {
                clearLastHighlight();
            }

            let onLoadScroll = {x: window.pageXOffset, y: window.pageYOffset};
            // markdownNavigator.debug("onLoadScroll" + JSON.stringify(onLoadScroll, null, "  "));
            console.debug("onLoadScroll", onLoadScroll);
            markdownNavigator.setState("onLoadScroll", onLoadScroll);
            scrolledToSourcePosition = false;
        });
    });

    let onLoadScroll = markdownNavigator.getState("onLoadScroll");
    if (onLoadScroll) {
        window.scrollTo(onLoadScroll.x, onLoadScroll.y);
    }
})();
