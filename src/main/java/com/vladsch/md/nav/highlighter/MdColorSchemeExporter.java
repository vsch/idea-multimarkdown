// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.highlighter;

import com.intellij.application.options.schemes.SerializableSchemeExporter;
import com.intellij.configurationStore.SerializableScheme;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.options.Scheme;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.project.Project;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Exports a scheme as .icls file.
 * <p>
 * Removes all synthetic attributes
 */
public class MdColorSchemeExporter extends SerializableSchemeExporter {
    public MdColorSchemeExporter() {
        super();
    }

    @Override
    public void exportScheme(@Nullable Project project, @NotNull final Scheme scheme, @NotNull final OutputStream outputStream) throws Exception {
        if (scheme instanceof SerializableScheme) {
            final Element state = ((SerializableScheme) scheme).writeScheme();
            final Element attributes = state.getChild("attributes");
            Set<String> colorKeys = new HashSet<>();
            for (AttributesDescriptor attr : MdColorSettingsPage.attributeDescriptors) {
                String externalName = attr.getKey().getExternalName();
                colorKeys.add(externalName);
            }

            attributes.removeContent((Filter<Content>) o -> {
                if (o instanceof Element) {
                    final String name = ((Element) o).getAttributeValue("name");
                    if (name.startsWith("MARKDOWN_NAVIGATOR.")) {
                        return !colorKeys.contains(name);
                    }
                }
                return false;
            });
            writeToStream(outputStream, state);
        } else {
            super.exportScheme(project, scheme, outputStream);
        }
    }

    @Override
    public String getExtension() {
        return EditorColorsManager.COLOR_SCHEME_FILE_EXTENSION.substring(1);
    }

    private static void writeToStream(@NotNull OutputStream outputStream, @NotNull Element element) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        Format format = Format.getPrettyFormat();
        format.setLineSeparator("\n");
        new XMLOutputter(format).output(element, writer);
    }
}
