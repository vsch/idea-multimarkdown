// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.parser.cache.data.dependency;

/**
 * Versioned dependency
 *
 * NOTE: equals and hashCode for two versioned instances for the same underlying object should result in equivalence between the versioned instances
 *
 * If the underlying dependency is a PsiElement then original psi element should be used for version tracking and comparisons
 *
 * If the underlying dependency is a PsiFile then original original file should be used for version tracking and comparisons
 */
public interface DataDependency {
    /**
     * Get current version of this dependency or -1 if dependent is no longer valid
     * @return version of the dependent
     */
    long getVersion();
}
