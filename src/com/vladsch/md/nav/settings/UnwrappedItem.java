// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// was Kotlin class
//open class UnwrappedItem<T : StateHolder>(val getter: () -> T, val setter: (T) -> Any?, val factory: (Element?) -> T) : Item<T> {
//    override fun saveState(element: Element?): Element? {
//    return getter().saveState(element)
//}
//
//    override fun loadState(element: Element?): Any? {
//    return setter(createItem(element))
//}
//
//    override fun createItem(element: Element?): T {
//        return factory(element)
//    }
//}
public class UnwrappedItem<T extends StateHolder> implements Item<T> {
    private final Supplier<T> getter;
    private final Consumer<T> setter;
    private final Function<Element, T> factory;

    public UnwrappedItem(@Nullable final Supplier<T> getter, final Consumer<T> setter, final Function<Element, T> factory) {
        this.getter = getter;
        this.setter = setter;
        this.factory = factory;
    }

    public UnwrappedItem(boolean readOnly, @Nullable final Supplier<T> getter, final Consumer<T> setter, final Function<Element, T> factory) {
        this(readOnly ? null : getter, setter, factory);
    }

    @NotNull
    @Override
    public T createItem(@Nullable final Element element) {
        return factory.apply(element);
    }

    @Nullable
    @Override
    public Element saveState(@Nullable final Element element) {
        T item = getter == null ? null : getter.get();
        return item == null ? null : item.saveState(element);
    }

    @Override
    public void loadState(@Nullable final Element element) {
        if (element == null) return;
        setter.accept(createItem(element));
    }
}
