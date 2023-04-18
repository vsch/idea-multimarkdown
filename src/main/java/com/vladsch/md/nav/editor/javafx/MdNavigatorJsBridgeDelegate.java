// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.editor.javafx;

import netscape.javascript.JSObject;

public class MdNavigatorJsBridgeDelegate implements MdNavigatorJsBridge {
    final private MdNavigatorJsBridge myBridge;

    public MdNavigatorJsBridgeDelegate(final MdNavigatorJsBridge bridge) {
        myBridge = bridge;
    }

    @Override
    public void consoleLog(final String type, final JSObject args) {myBridge.consoleLog(type, args);}

    @Override
    public void println(final String text) {myBridge.println(text);}

    @Override
    public void print(final String text) {myBridge.print(text);}

    @Override
    public void pageLoadComplete() {myBridge.pageLoadComplete();}

    @Override
    public void setEventHandledBy(final String handledBy) {myBridge.setEventHandledBy(handledBy);}

    @Override
    public Object getState(final String name) {return myBridge.getState(name);}

    @Override
    public void setState(final String name, final Object state) {myBridge.setState(name, state);}

    @Override
    public void toggleTask(final String pos) {myBridge.toggleTask(pos);}
}
