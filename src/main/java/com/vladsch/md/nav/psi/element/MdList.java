// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.psi.element;

import com.vladsch.md.nav.util.looping.MdPsiIterator;
import com.vladsch.plugin.util.Wrapped;
import org.jetbrains.annotations.Nullable;

public interface MdList extends MdTaskItemContainer, MdComposite, MdStructureViewPresentableElement, MdStructureViewPresentableItem, MdBlockElementWithChildren {
    Wrapped<MdListItem> WRAPPED_LIST_ITEM = Wrapped.nullOf(MdListItem.class);

    default MdPsiIterator<MdListItem> itemLooping() {
        return childLooping().filter(MdListItem.class);
    }

    @Nullable
    default MdListItem getFirstItem() {
        return itemLooping().doLoop(WRAPPED_LIST_ITEM, (it, loop) -> loop.Return(Wrapped.of(it))).unwrap();
    }

    @Nullable
    default MdListItem getLastItem() {
        return itemLooping().reversed().doLoop(WRAPPED_LIST_ITEM, (it, loop) -> loop.Return(Wrapped.of(it))).unwrap();
    }
}
