// Copyright (c) 2017-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.flex.intentions

import com.intellij.codeInsight.intention.impl.config.IntentionManagerSettings
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.vladsch.md.nav.MdLanguage
import com.vladsch.md.nav.intentions.Intention
import icons.FlexmarkIcons
import icons.MdIcons
import javax.swing.Icon

abstract class FlexIntention protected constructor() : Intention() {
    override fun getText(): String = FlexIntentionsBundle.message(prefix)

    override fun getFamilyName(): String = FlexIntentionsBundle.message(prefix)

    override fun isAvailableIn(file: PsiFile): Boolean {
        return file.viewProvider.languages.contains(MdLanguage.INSTANCE)
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        if (isAvailableIn(file)) {
            addAllInFileAction(project)
            return findMatchingElement(file, editor) != null
        }
        return false
    }

    override fun startInWriteAction(): Boolean = true

    override val icon: Icon? by lazy {
        val intentionMetaData = IntentionManagerSettings.getInstance().metaData
        val familyName = familyName
        var icon: Icon? = null
        for (metaData in intentionMetaData) {
            if (metaData.action.familyName == familyName) {
                icon = FlexmarkIcons.IntentionActions.getCategoryIcon(metaData.myCategory)
                break
            }
        }
        icon ?: FlexmarkIcons.IntentionActions.getCategoryIcon(MdIcons.MARKDOWN_CATEGORY)
    }
}
