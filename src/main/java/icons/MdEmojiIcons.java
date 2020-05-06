// Copyright (c) 2015-2020 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package icons;

import com.intellij.openapi.util.IconLoader;
import com.vladsch.flexmark.ext.emoji.internal.EmojiReference;
import com.vladsch.flexmark.ext.emoji.internal.EmojiShortcuts;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.HashMap;

public class MdEmojiIcons {
    static Icon load(String path) {
        return IconLoader.getIcon(path, MdEmojiIcons.class);
    }

    static HashMap<String, Icon> emojiMap = new HashMap<>();
    static HashMap<Icon, String> emojiIconNameMap = new HashMap<>();
    //public static final Icon MultiMarkdown = load("/icons/emojis/8ball.png"); // 16x16

    public static HashMap<Icon, String> getIconNamesMap() {
        return emojiIconNameMap;
    }

    @Nullable
    public static Icon getEmojiIcon(@Nullable String emojiShortcut) {
        if (emojiShortcut == null) return null;
        Icon icon = emojiMap.get(emojiShortcut);
        if (icon != null) return icon;

        EmojiReference.Emoji shortcut = EmojiShortcuts.getEmojiFromShortcut(emojiShortcut);
        if (shortcut == null) return null;

        String iconFilename = shortcut.emojiCheatSheetFile;
        if (iconFilename == null) {
            iconFilename = shortcut.githubFile;
            if (iconFilename == null) {
                return null;
            }
            iconFilename = shortcut.shortcut + ".png";
        }

        icon = load("/icons/emojis/" + iconFilename);
        emojiMap.put(emojiShortcut, icon);
        emojiIconNameMap.put(icon, "Emoji: " + emojiShortcut);
        return icon;
    }
}
