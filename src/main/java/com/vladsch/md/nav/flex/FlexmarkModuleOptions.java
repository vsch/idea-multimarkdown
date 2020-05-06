// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.flex;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class FlexmarkModuleOptions {
    public interface Holder {
        @NotNull
        FlexmarkModuleOptions getOptions();

        void setOptions(@NotNull FlexmarkModuleOptions options);
    }

    public static final String BLOCK_PARSER = "BLOCK_PARSER";
    public static final String BLOCK_PRE_PROCESSOR = "BLOCK_PRE_PROCESSOR";
    public static final String DELIMITER_PROCESSOR = "DELIMITER_PROCESSOR";
    public static final String INLINE_PARSER_EXTENSION = "INLINE_PARSER_EXTENSION";
    public static final String LINK_REF_PROCESSOR = "LINK_REF_PROCESSOR";
    public static final String NODE_RENDERER = "NODE_RENDERER";
    public static final String JIRA_RENDERER = "JIRA_RENDERER";
    public static final String PHASED_NODE_RENDERER = "PHASED_NODE_RENDERER";
    public static final String LINK_RESOLVER = "LINK_RESOLVER";
    public static final String ATTRIBUTE_PROVIDER = "ATTRIBUTE_PROVIDER";
    public static final String CUSTOM_PROPERTIES = "CUSTOM_PROPERTIES";
    public static final String PARAGRAPH_PRE_PROCESSOR = "PARAGRAPH_PRE_PROCESSOR";
    public static final String NODE_POST_PROCESSOR = "NODE_POST_PROCESSOR";
    public static final String DOCUMENT_POST_PROCESSOR = "DOCUMENT_POST_PROCESSOR";
    public static final String CUSTOM_NODE_REPOSITORY = "CUSTOM_NODE_REPOSITORY";
    public static final String CUSTOM_NODE = "CUSTOM_NODE";
    public static final String CUSTOM_BLOCK_NODE = "CUSTOM_BLOCK_NODE";

    public @NotNull String extensionName = "ExtModule";
    public @NotNull String extensionPackage = "com.vladsch.flexmark.ext.module";
    public @NotNull String extensionPackagePrefix = "com.vladsch.flexmark";
    public boolean delimiterProcessor = true;
    public boolean inlineParserExtension = false;
    public boolean blockParser = true;
    public boolean blockPreProcessor = true;
    public boolean linkRefProcessor = true;
    public boolean paragraphPreProcessor = true;
    public boolean nodePostProcessor = true;
    public boolean documentPostProcessor = true;
    public boolean nodeRenderer = true;
    public boolean jiraRenderer = true;
    public boolean phasedNodeRenderer = false;
    public boolean linkResolver = false;
    public boolean attributeProvider = false;
    public boolean customBlockNode = true;
    public boolean customNode = true;
    public boolean customNodeRepository = true;
    public boolean customProperties = true;

    public FlexmarkModuleOptions() {
    }

    public FlexmarkModuleOptions(FlexmarkModuleOptions other) {
        copyFrom(other);
    }

    public FlexmarkModuleOptions(
            @NotNull String extensionName,
            @NotNull String extensionPackage,
            @NotNull String extensionPackagePrefix,
            boolean delimiterProcessor,
            boolean inlineParserExtension,
            boolean blockParser,
            boolean blockPreProcessor,
            boolean linkRefProcessor,
            boolean paragraphPreProcessor,
            boolean nodePostProcessor,
            boolean documentPostProcessor,
            boolean nodeRenderer,
            boolean phasedNodeRenderer,
            boolean linkResolver,
            boolean attributeProvider,
            boolean customBlockNode,
            boolean customNode,
            boolean customNodeRepository,
            boolean customProperties,
            boolean jiraRenderer
    ) {
        this.extensionName = extensionName;
        this.extensionPackage = extensionPackage;
        this.extensionPackagePrefix = extensionPackagePrefix;
        this.delimiterProcessor = delimiterProcessor;
        this.inlineParserExtension = inlineParserExtension;
        this.blockParser = blockParser;
        this.blockPreProcessor = blockPreProcessor;
        this.linkRefProcessor = linkRefProcessor;
        this.paragraphPreProcessor = paragraphPreProcessor;
        this.nodePostProcessor = nodePostProcessor;
        this.documentPostProcessor = documentPostProcessor;
        this.nodeRenderer = nodeRenderer;
        this.phasedNodeRenderer = phasedNodeRenderer;
        this.linkResolver = linkResolver;
        this.attributeProvider = attributeProvider;
        this.customBlockNode = customBlockNode;
        this.customNode = customNode;
        this.customNodeRepository = customNodeRepository;
        this.customProperties = customProperties;
        this.jiraRenderer = jiraRenderer;
    }

    public void copyFrom(FlexmarkModuleOptions other) {
        this.extensionName = other.extensionName;
        this.extensionPackage = other.extensionPackage;
        this.extensionPackagePrefix = other.extensionPackagePrefix;
        this.delimiterProcessor = other.delimiterProcessor;
        this.inlineParserExtension = other.inlineParserExtension;
        this.blockParser = other.blockParser;
        this.blockPreProcessor = other.blockPreProcessor;
        this.linkRefProcessor = other.linkRefProcessor;
        this.paragraphPreProcessor = other.paragraphPreProcessor;
        this.nodePostProcessor = other.nodePostProcessor;
        this.documentPostProcessor = other.documentPostProcessor;
        this.nodeRenderer = other.nodeRenderer;
        this.jiraRenderer = other.jiraRenderer;
        this.phasedNodeRenderer = other.phasedNodeRenderer;
        this.linkResolver = other.linkResolver;
        this.attributeProvider = other.attributeProvider;
        this.customBlockNode = other.customBlockNode;
        this.customNode = other.customNode;
        this.customNodeRepository = other.customNodeRepository;
        this.customProperties = other.customProperties;
    }

    // @formatter:off
    public FlexmarkModuleOptions extensionName(@NotNull String extensionName)                       { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions extensionPackage(@NotNull String extensionPackage)                 { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions extensionPackagePrefix(@NotNull String extensionPackagePrefix)     { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions delimiterProcessor(boolean delimiterProcessor)                     { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions inlineParserExtension(boolean inlineParserExtension)               { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions blockParser(boolean blockParser)                                   { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions blockPreProcessor(boolean blockPreProcessor)                       { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions linkRefProcessor(boolean linkRefProcessor)                         { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions paragraphPreProcessor(boolean paragraphPreProcessor)               { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions nodePostProcessor(boolean nodePostProcessor)                       { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions documentPostProcessor(boolean documentPostProcessor)               { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions nodeRenderer(boolean nodeRenderer)                                 { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions phasedNodeRenderer(boolean phasedNodeRenderer)                     { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions linkResolver(boolean linkResolver)                                 { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions attributeProvider(boolean attributeProvider)                       { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions customBlockNode(boolean customBlockNode)                           { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions customNode(boolean customNode)                                     { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions customNodeRepository(boolean customNodeRepository)                 { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions customProperties(boolean customProperties)                         { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    public FlexmarkModuleOptions jiraRenderer(boolean jiraRenderer)                                 { return new FlexmarkModuleOptions(extensionName, extensionPackage, extensionPackagePrefix, delimiterProcessor, inlineParserExtension, blockParser, blockPreProcessor, linkRefProcessor, paragraphPreProcessor, nodePostProcessor, documentPostProcessor, nodeRenderer, phasedNodeRenderer, linkResolver,  attributeProvider, customBlockNode, customNode, customNodeRepository, customProperties, jiraRenderer); }
    // @formatter:on

    public Set<String> getOptionsSet() {
        HashSet<String> options = new HashSet<String>();

        if (blockParser) options.add(BLOCK_PARSER);
        if (blockPreProcessor) options.add(BLOCK_PRE_PROCESSOR);
        if (delimiterProcessor) options.add(DELIMITER_PROCESSOR);
        if (inlineParserExtension) options.add(INLINE_PARSER_EXTENSION);
        if (linkRefProcessor) options.add(LINK_REF_PROCESSOR);
        if (nodeRenderer) options.add(NODE_RENDERER);
        if (phasedNodeRenderer) options.add(PHASED_NODE_RENDERER);
        if (linkResolver) options.add(LINK_RESOLVER);
        if (attributeProvider) options.add(ATTRIBUTE_PROVIDER);
        if (customProperties) options.add(CUSTOM_PROPERTIES);
        if (paragraphPreProcessor) options.add(PARAGRAPH_PRE_PROCESSOR);
        if (nodePostProcessor) options.add(NODE_POST_PROCESSOR);
        if (documentPostProcessor) options.add(DOCUMENT_POST_PROCESSOR);
        if (customNodeRepository) options.add(CUSTOM_NODE_REPOSITORY);
        if (customNode) options.add(CUSTOM_NODE);
        if (customBlockNode) options.add(CUSTOM_BLOCK_NODE);
        if (jiraRenderer) options.add(JIRA_RENDERER);
        return options;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FlexmarkModuleOptions options = (FlexmarkModuleOptions) o;

        if (delimiterProcessor != options.delimiterProcessor) return false;
        if (inlineParserExtension != options.inlineParserExtension) return false;
        if (blockParser != options.blockParser) return false;
        if (blockPreProcessor != options.blockPreProcessor) return false;
        if (linkRefProcessor != options.linkRefProcessor) return false;
        if (paragraphPreProcessor != options.paragraphPreProcessor) return false;
        if (nodePostProcessor != options.nodePostProcessor) return false;
        if (documentPostProcessor != options.documentPostProcessor) return false;
        if (nodeRenderer != options.nodeRenderer) return false;
        if (phasedNodeRenderer != options.phasedNodeRenderer) return false;
        if (linkResolver != options.linkResolver) return false;
        if (attributeProvider != options.attributeProvider) return false;
        if (customBlockNode != options.customBlockNode) return false;
        if (customNode != options.customNode) return false;
        if (customNodeRepository != options.customNodeRepository) return false;
        if (customProperties != options.customProperties) return false;
        if (jiraRenderer != options.jiraRenderer) return false;

        if (!extensionName.equals(options.extensionName)) return false;
        if (!extensionPackage.equals(options.extensionPackage)) return false;
        if (!extensionPackagePrefix.equals(options.extensionPackagePrefix)) return false;

        return false;
    }

    @Override
    public int hashCode() {
        int result = 0;
        result = 31 * result + extensionName.hashCode();
        result = 31 * result + extensionPackage.hashCode();
        result = 31 * result + extensionPackagePrefix.hashCode();
        result = 31 * result + (delimiterProcessor ? 1 : 0);
        result = 31 * result + (inlineParserExtension ? 1 : 0);
        result = 31 * result + (blockParser ? 1 : 0);
        result = 31 * result + (blockPreProcessor ? 1 : 0);
        result = 31 * result + (linkRefProcessor ? 1 : 0);
        result = 31 * result + (paragraphPreProcessor ? 1 : 0);
        result = 31 * result + (nodePostProcessor ? 1 : 0);
        result = 31 * result + (documentPostProcessor ? 1 : 0);
        result = 31 * result + (nodeRenderer ? 1 : 0);
        result = 31 * result + (phasedNodeRenderer ? 1 : 0);
        result = 31 * result + (linkResolver ? 1 : 0);
        result = 31 * result + (attributeProvider ? 1 : 0);
        result = 31 * result + (customBlockNode ? 1 : 0);
        result = 31 * result + (customNode ? 1 : 0);
        result = 31 * result + (customNodeRepository ? 1 : 0);
        result = 31 * result + (customProperties ? 1 : 0);
        result = 31 * result + (jiraRenderer ? 1 : 0);
        return result;
    }
}
