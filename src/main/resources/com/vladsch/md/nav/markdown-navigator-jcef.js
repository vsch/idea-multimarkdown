/*
 *   The MIT License (MIT)
 *   <p>
 *   Copyright (c) 2018-2020 Vladimir Schneider (https://github.com/vsch)
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
var firstCollapsedHeading = function() {
};

// noinspection ES6ConvertVarToLetConst
var scrollToSourcePosition = function() {
};

var markdownNavigator;

(function() {
    "use strict";
    let __onJsBridge = [];
    
    // noinspection ES6ConvertVarToLetConst
    markdownNavigator = Object.assign({
        // these will be set by handler
        setStateString: function(stateObjectString) { }, 
        getStateString: function(stateName) { return null; },
        toggleTask: function(pos) { },
        openLinkInBrowser: function(href) { },
        synchronizeCaretPos: function(sourceOffset) { },
        
        // not used
        pageLoadComplete: function() { },
        setEventHandledBy: function (taskOffset) { },
        
        // implementations invoked by JS or Java
        runJsBridge:function() {
          // run any ops needed on connection
          for (const __onJsBridgeItem of __onJsBridge) {
            console.debug("onLoad", __onJsBridgeItem);
            try {
              __onJsBridgeItem();
            } catch (e) {
              console.println("onJsBridge exception: calling " + __onJsBridgeItem);
              console.println("onJsBridge exception: " + e);
            }
          }
    
          // change default implementations 
          markdownNavigator.runJsBridge = function() { };
          markdownNavigator.onJsBridge = function(op) { 
            op();
          };
          __onJsBridge = null;
            
          // signal JsBridge connection complete
          markdownNavigator.pageLoadComplete();
        },
        
        onJsBridge: function(op) {
          __onJsBridge[__onJsBridge.length] = op;
        },
        
        getState: function(stateName) { 
          let stateString = markdownNavigator.getStateString(stateName); 
          if (stateString === null) return null;
          try {
            return JSON.parse(stateString); 
          } catch (e) {
            console.debug("Error in getStateString returned JSON: " + stateString, e);
            return null;
          }
        },
        
        setState: function(stateName, stateValue) { 
          let state = { 
            stateName: stateName,
            stateValue: stateValue,
          };
          
          markdownNavigator.setStateString(JSON.stringify(state));
        },
    }, markdownNavigator || {});
    
    window.addEventListener("load", function() {
       const aTags = document.getElementsByTagName('a');
       for (let aTag of aTags) {
         try {
           let href = aTag.getAttribute('href');
           aTag.addEventListener("click", function(evt) { 
             if (href[0] !== '#') {
               if (markdownNavigator) {
                 evt.stopPropagation();
                 evt.preventDefault();
                 markdownNavigator.openLinkInBrowser(href);
               }
             } else {
                evt.stopPropagation();
             }
           }, false);
         } catch(e) {
         
         }
       }
       
       const spans = document.getElementsByTagName('span');
       for (let span of spans) {
         try {
           let spanClass = span.getAttribute("class")
           if (spanClass == "task-item-closed" || spanClass == "task-item-open") {
             let taskOffset = listItem.getAttribute("task-offset")
             if (taskOffset) {
               span.addEventListener("click", function(evt) { 
               if (markdownNavigator) {
                   evt.stopPropagation();
                   evt.preventDefault();
                   markdownNavigator.toggleTask(taskOffset);
                 }
               }, false);
             }
           }
         } catch(e) {
         
         }
       }
       
       window.document.addEventListener("click", function(evt) {
          // scroll source to preview element
          let element = evt.target;
          while (element != null) {
            try {
              let srcPos = element.getAttribute("md-pos");
              if (srcPos) {
//                console.debug("document.onClick: sync pos")
                markdownNavigator.synchronizeCaretPos(srcPos);
                break;
              }
              element = element.parentNode;
            } catch(e) {
            
            }
          }
       }, false);
    }, false);
})();

