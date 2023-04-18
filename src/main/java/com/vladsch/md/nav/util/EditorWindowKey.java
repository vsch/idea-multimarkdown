// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class EditorWindowKey {
    @Nullable
    public final Key<Integer> myKey;

    public EditorWindowKey() {
        Key<Integer> key = null;
        try {
            key = EditorWindow.INITIAL_INDEX_KEY;
        } catch (IllegalAccessError e) {
            try {
                Field f = EditorWindow.class.getDeclaredField("INITIAL_INDEX_KEY"); //NoSuchFieldException
                f.setAccessible(true);
                //noinspection unchecked
                key = (Key<Integer>) f.get(null);
            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                key = null;
            }
        }
        myKey = key;
    }

    public boolean haveKey() {
        return myKey != null;
    }

    public boolean setEditorWindowInitialIndex(@NotNull VirtualFile file, @Nullable Integer editorIndex) {
        if (myKey != null) {
            file.putUserData(myKey, editorIndex);
        }
        return myKey != null;
    }
}
