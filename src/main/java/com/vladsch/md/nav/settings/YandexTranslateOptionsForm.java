// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.HyperlinkLabel;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.boxed.json.BoxedJsObject;
import com.vladsch.boxed.json.BoxedJson;
import com.vladsch.md.nav.util.translator.yandex.YandexClient;
import com.vladsch.plugin.util.ui.FormParams;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettableForm;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.json.JsonValue;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YandexTranslateOptionsForm extends FormParams<MdDocumentSettings> implements SettableForm<MdDocumentSettings> {
    private static final Logger LOG = Logger.getInstance("com.vladsch.md.nav.settings.editor");
    private static final String[] EMPTY_STRINGS = new String[0];

    JPanel myMainPanel;
    JComboBox<String> myYandexFromLanguage;
    JComboBox<String> myYandexToLanguage;
    private JLabel myYandexFromLanguageLabel;
    private JButton myLoadLanguages;
    JBCheckBox myTranslateAutoDetect;
    private JLabel myYandexToLanguageLabel;
    private HyperlinkLabel myYandexLink;
    private JButton myGetYandexKeyButton;
    String myYandexKey;
    private String myFromLang = "";
    private String myToLang = "";

    private boolean myEnabled = true;

    private final SettingsComponents<MdDocumentSettings> components;

    public YandexTranslateOptionsForm(@NotNull MdDocumentSettings settings) {
        super(settings);

        components = new SettingsComponents<MdDocumentSettings>() {
            @Override
            protected Settable<MdDocumentSettings>[] createComponents(@NotNull MdDocumentSettings i) {
                //noinspection unchecked
                return new Settable[] {
                        notrace("TranslateAutoDetect", component(myTranslateAutoDetect, i::getTranslateAutoDetect, i::setTranslateAutoDetect)),
                        notrace("YandexKey", component(() -> myYandexKey.trim(), text -> myYandexKey = text.trim(), i::getYandexKey, i::setYandexKey)),
                        notrace("YandexFromLanguage", componentString(YandexFromLanguages.ADAPTER, myYandexFromLanguage, i::getYandexFromLanguage, i::setYandexFromLanguage)),
                        notrace("YandexToLanguage", componentString(YandexToLanguages.ADAPTER, myYandexToLanguage, i::getYandexToLanguage, i::setYandexToLanguage)),
                };
            }
        };

        // reload languages
        myLoadLanguages.addActionListener(e -> {
            myToLang = (String) myYandexToLanguage.getSelectedItem();
            myFromLang = (String) myYandexFromLanguage.getSelectedItem();

            loadYandexLanguages(myFromLang, myYandexKey);
            List<String> languages = getYandexLanguages(myFromLang);
            YandexToLanguages.updateValues(languages, false, myYandexToLanguage);
            myYandexToLanguage.setSelectedItem(myToLang);
        });

        myGetYandexKeyButton.addActionListener(e -> {
            String yandexKey = EnterYandexKeyDialog.showDialog(myMainPanel, myYandexKey);
            if (yandexKey != null) {
                myYandexKey = yandexKey;
                updateOptionalSettings();
            }
        });
        myYandexLink.setHyperlinkText("Powered by Yandex.Translate");
        myYandexLink.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                BrowserUtil.browse("http://translate.yandex.com/");
            }
        });

        updateOptionalSettings();
    }

    static final HashMap<String, List<String>> ourLanguageMap = new HashMap<>();
    @SuppressWarnings("unchecked")
    static final List<String> ourEmptyLanguages = Collections.EMPTY_LIST;

    static final String[] ourYandexDefaults = new String[] {
            "af", "am", "ar", "az", "ba", "be", "bg", "bn", "bs", "ca", "ceb", "cs", "cy", "da", "de", "el", "en", "eo",
            "es", "et", "eu", "fa", "fi", "fr", "ga", "gd", "gl", "gu", "he", "hi", "hr", "ht", "hu", "hy", "id", "is",
            "it", "ja", "jv", "ka", "kk", "km", "kn", "ko", "ky", "la", "lb", "lo", "lt", "lv", "mg", "mhr", "mi", "mk",
            "ml", "mn", "mr", "mrj", "ms", "ms", "mt", "my", "ne", "nl", "no", "pa", "pap", "pl", "pt", "ro", "ru", "si",
            "sk", "sl", "sq", "sr", "su", "sv", "sw", "ta", "te", "tg", "th", "tl", "tr", "tt", "udm", "uk", "ur", "uz",
            "vi", "xh", "yi", "zh"
    };

    static void initializeLanguages() {
        ourLanguageMap.clear();
        List<String> list = Arrays.asList(ourYandexDefaults);
        for (String lang : ourYandexDefaults) {
            ourLanguageMap.put(lang, list);
        }
    }

    static void loadYandexLanguages(String forLang, String yandexKey) {
        try {
            String responseText = new YandexClient(yandexKey).getLanguages(forLang);
            final BoxedJsObject response = BoxedJson.boxedFrom(responseText);
            final BoxedJsObject responseObject = response.getJsonObject("langs");
            if (responseObject.isValid()) {
                ArrayList<String> from = new ArrayList<>();
                for (Map.Entry<String, JsonValue> entry : responseObject.entrySet()) {
                    from.add(entry.getKey());
                }
                ourLanguageMap.put(forLang, from);
            } else {
                // FIX: show error for invalid response
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @NotNull
    static List<String> getYandexLanguages(String forLang) {
        if (ourLanguageMap.isEmpty()) {
            initializeLanguages();
        }

        if (ourLanguageMap.containsKey(forLang)) {
            return ourLanguageMap.get(forLang);
        }
        return ourEmptyLanguages;
    }

    private void createUIComponents() {
        myYandexKey = "";
        YandexFromLanguages.updateValues(getYandexLanguages("en"), false, myYandexFromLanguage);
        YandexToLanguages.updateValues(getYandexLanguages("en"), false, myYandexToLanguage);
        myYandexFromLanguage = YandexFromLanguages.ADAPTER.createComboBox();
        myYandexToLanguage = YandexToLanguages.ADAPTER.createComboBox();
    }

    public boolean isEnabled() {
        return myEnabled;
    }

    public void setEnabled(boolean enabled) {
        myEnabled = enabled;

        updateOptionalSettings();
    }

    public void updateOptionalSettings() {
        final boolean canTranslate = !myYandexKey.isEmpty() && myEnabled;
        myLoadLanguages.setEnabled(canTranslate);
        myTranslateAutoDetect.setEnabled(canTranslate);
        myYandexFromLanguage.setEnabled(canTranslate);
        myYandexToLanguage.setEnabled(canTranslate);
        myYandexFromLanguageLabel.setEnabled(canTranslate);
        myYandexToLanguageLabel.setEnabled(canTranslate);
        myGetYandexKeyButton.setEnabled(myEnabled);
    }

    @Override
    public void reset(@NotNull MdDocumentSettings settings) {
        components.reset(settings);
    }

    @Override
    public void apply(@NotNull MdDocumentSettings settings) {
        components.apply(settings);
    }

    @Override
    public boolean isModified(@NotNull MdDocumentSettings settings) {
        return components.isModified(settings);
    }
}
