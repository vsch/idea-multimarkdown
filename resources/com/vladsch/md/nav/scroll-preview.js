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

// script used to only restore last scroll top position on load
// noinspection ES6ConvertVarToLetConst
var markdownNavigator;

(function () {
    "use strict";

    markdownNavigator.onJsBridge(() => {
        window.addEventListener('scroll', function () {
            let onLoadScroll = {x: window.pageXOffset, y: window.pageYOffset};
            console.debug("onLoadScroll", onLoadScroll);
            markdownNavigator.setState("onLoadScroll", onLoadScroll);
        });
    });

    let onLoadScroll = markdownNavigator.getState("onLoadScroll");
    if (onLoadScroll) {
        window.scrollTo(window.pageXOffset, onLoadScroll.y);
    }
})();

