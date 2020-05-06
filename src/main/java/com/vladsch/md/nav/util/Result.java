// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Result<T> {
    private final @NotNull Type myType;
    private final @Nullable T myValue;
    private final boolean myHaveValue;

    private enum Type {
        CONTINUE,
        SKIP,
        STOP,
        RETURN,
    }

    private Result(@NotNull Type type, @Nullable T value) {
        myType = type;
        myValue = value;
        myHaveValue = value != null;
    }

    @NotNull
    public T get() {
        assert (myType == Type.RETURN || myHaveValue) && myValue != null;
        return myValue;
    }

    @Override
    public String toString() {
        return "Result{" +
                "type=" + myType +
                ", haveValue=" + myHaveValue +
                (myHaveValue ? ", value=" + myValue : "") +
                '}';
    }

    @Nullable
    public T getOrNull() {
        return myValue;
    }

    public boolean isReturn() {
        return myType == Type.RETURN;
    }

    public boolean isStop() {
        return myType == Type.STOP || myType == Type.RETURN;
    }

    public boolean isSkip() {
        return myType == Type.SKIP;
    }

    public boolean isContinue() {
        return myType == Type.CONTINUE;
    }

    private Result<T> withType(@NotNull Type newType) {
        return new Result<>(newType, null);
    }

    private Result<T> withReturn(@NotNull T value) {
        return new Result<>(Type.RETURN, value);
    }

    public Result<T> Continue() { return withType(Type.CONTINUE); }

    public Result<T> Skip() { return withType(Type.SKIP); }

    public Result<T> Stop() { return withType(Type.STOP); }

    public Result<T> Return(@NotNull T value) { return withReturn(value); }

    public static <T> Result<T> RETURN(@NotNull T value) {
        return new Result<>(Type.RETURN, value);
    }

    public static <T> Result<T> CONTINUE() { return new Result<>(Type.CONTINUE, null); }

    public static Result<Boolean> VOID() { return new Result<>(Type.CONTINUE, null); }

    public static Result<Boolean> TRUE() { return new Result<>(Type.RETURN, true);}

    public static Result<Boolean> FALSE() { return new Result<>(Type.RETURN, false);}

    public static <T> Result<T> VALUE(@Nullable T value) { return new Result<>(value != null ? Type.RETURN : Type.CONTINUE, value); }

    public static <T> Result<T> SKIP() { return new Result<>(Type.SKIP, null); }

    public static <T> Result<T> STOP() { return new Result<>(Type.STOP, null); }
}
