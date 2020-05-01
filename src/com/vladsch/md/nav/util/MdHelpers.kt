// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util

import com.intellij.psi.PsiElement

fun <K : PsiElement> Collection<K>.toPsiSet(): PsiSet<K> = PsiSet.toPsiSet(this)
fun <K : PsiElement> Collection<K>.toLinkedPsiSet(): PsiSet<K> = PsiSet.toLinkedPsiSet(this)
fun <K : PsiElement, V> Map<K, V>.toPsiMap(): PsiMap<K, V> = PsiMap.toPsiMap(this)
fun <K : PsiElement, V> Map<K, V>.toLinkedPsiMap(): PsiMap<K, V> = PsiMap.toLinkedPsiMap(this)

fun <K: PsiElement, V> Collection<Map.Entry<K, V>>.toPsiMap(): PsiMap<K, V> = PsiMap.toPsiMap(this)
fun <K: PsiElement, V> Collection<Map.Entry<K, V>>.toLinkedPsiMap(): PsiMap<K, V> = PsiMap.toLinkedPsiMap(this)

fun <K: PsiElement, V> List<Pair<K, V>>.toPsiMap(): PsiMap<K, V> = PsiMap.toPsiMap(this.toMap())
fun <K: PsiElement, V> List<Pair<K, V>>.toLinkedPsiMap(): PsiMap<K, V> = PsiMap.toLinkedPsiMap(this.toMap())

