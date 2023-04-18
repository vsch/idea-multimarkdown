// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.vladsch.md.nav.editor.util.HtmlScriptResourceProvider;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class ScriptResource implements Serializable, Cloneable {

    private boolean selected = false;
    private boolean enabled = false;
    @NotNull private final HtmlScriptResourceProvider.Info providerInfo;

    public ScriptResource() {
        this(false, false, new HtmlScriptResourceProvider.Info());
    }

    public ScriptResource(boolean enabled, boolean selected, @NotNull HtmlScriptResourceProvider.Info providerInfo) {
        this.enabled = enabled;
        this.selected = selected;
        this.providerInfo = providerInfo;
    }

    @NotNull
    public HtmlScriptResourceProvider.Info getProviderInfo() {
        return providerInfo;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDisplayName() {
        return providerInfo.getName();
    }

    public String getProviderId() {
        return providerInfo.getProviderId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScriptResource)) return false;

        ScriptResource that = (ScriptResource) o;

        if (selected != that.selected) return false;
        if (enabled != that.enabled) return false;
        return providerInfo.equals(that.providerInfo);
    }

    @Override
    public int hashCode() {
        int result = (selected ? 1 : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + providerInfo.hashCode();
        return result;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            //ignore this
        }
        return null;
    }
}
