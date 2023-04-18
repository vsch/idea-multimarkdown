// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.language;

import com.vladsch.flexmark.ext.toc.internal.TocOptions;
import org.jetbrains.annotations.Nullable;

public class MdTocOptions extends TocOptions {
    public static final MdTocOptions DEFAULT = new MdTocOptions();

    public final int onFormat;
    public final boolean isFormatOnSave;

    public MdTocOptions() {
        this.onFormat = TocGenerateOnFormatType.ADAPTER.getDefault().getIntValue();
        this.isFormatOnSave = TocGenerateOnSaveType.ADAPTER.getDefault() == TocGenerateOnSaveType.FORMAT;
    }

    public MdTocOptions(int onFormat, boolean isFormatOnSave, int levels, boolean isHtml, boolean isTextOnly, boolean isNumbered, int titleLevel, @Nullable String title, ListType listType) {
        super(levels, isHtml, isTextOnly, isNumbered, titleLevel, title, listType);
        this.onFormat = onFormat;
        this.isFormatOnSave = isFormatOnSave;
    }

    protected MdTocOptions(MdTocOptions.AsMutable other) {
        super(other);
        this.onFormat = other.onFormat;
        this.isFormatOnSave = other.isFormatOnSave;
    }

    public MdTocOptions(TocOptions other) {
        this(other, TocGenerateOnFormatType.ADAPTER.getDefault().getIntValue(), TocGenerateOnSaveType.ADAPTER.getDefault() == TocGenerateOnSaveType.FORMAT);
    }

    public MdTocOptions(TocOptions other, int onFormat, boolean isFormatOnSave) {
        super(other);
        this.onFormat = onFormat;
        this.isFormatOnSave = isFormatOnSave;
    }

    @Override
    public AsMutable toMutable() {
        return new AsMutable(this);
    }

    public static class AsMutable extends TocOptions.AsMutable {
        public int onFormat;
        public boolean isFormatOnSave;

        AsMutable(MdTocOptions other) {
            super(other);
            this.onFormat = other.onFormat;
            this.isFormatOnSave = other.isFormatOnSave;
        }

        @Override
        public AsMutable setLevelList(final int... levelList) {
            super.setLevelList(levelList);
            return this;
        }

        public AsMutable setTitle(CharSequence title) {
            this.title = title == null ? "" : title.toString();
            normalizeTitle();
            return this;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof MdTocOptions.AsMutable || o instanceof MdTocOptions)) return false;
            if (!super.equals(o)) return false;

            MdTocOptions.AsMutable mutable = o instanceof MdTocOptions.AsMutable ? (MdTocOptions.AsMutable) o : ((MdTocOptions) o).toMutable();

            if (onFormat != mutable.onFormat) return false;
            return isFormatOnSave == mutable.isFormatOnSave;
        }

        @Override
        public MdTocOptions toImmutable() {
            return new MdTocOptions(this);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + onFormat;
            result = 31 * result + (isFormatOnSave ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MarkdownTocOptions.AsMutable {" +
                    ", onFormat=" + onFormat +
                    ", isFormatOnSave=" + isFormatOnSave +
                    ", levels=" + levels +
                    ", isHtml=" + isHtml +
                    ", isTextOnly=" + isTextOnly +
                    ", isNumbered=" + isNumbered +
                    ", titleLevel=" + titleLevel +
                    ", title='" + title + '\'' +
                    ", listType=" + listType +
                    " }";
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MdTocOptions.AsMutable || o instanceof MdTocOptions)) return false;
        if (!super.equals(o)) return false;

        MdTocOptions options = o instanceof MdTocOptions ? (MdTocOptions) o : ((MdTocOptions.AsMutable) o).toImmutable();

        if (onFormat != options.onFormat) return false;
        return isFormatOnSave == options.isFormatOnSave;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + onFormat;
        result = 31 * result + (isFormatOnSave ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MarkdownTocOptions {" +
                ", onFormat=" + onFormat +
                ", isFormatOnSave=" + isFormatOnSave +
                ", levels=" + levels +
                ", isHtml=" + isHtml +
                ", isTextOnly=" + isTextOnly +
                ", isNumbered=" + isNumbered +
                ", titleLevel=" + titleLevel +
                ", title='" + title + '\'' +
                ", listType=" + listType +
                " }";
    }
}


