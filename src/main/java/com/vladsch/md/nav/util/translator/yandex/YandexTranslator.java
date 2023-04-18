// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.util.translator.yandex;

import com.vladsch.boxed.json.BoxedJsArray;
import com.vladsch.boxed.json.BoxedJsNumber;
import com.vladsch.boxed.json.BoxedJsObject;
import com.vladsch.boxed.json.BoxedJsString;
import com.vladsch.boxed.json.BoxedJson;
import com.vladsch.md.nav.util.translator.TranslateUtil;
import com.vladsch.md.nav.util.translator.Translator;

import java.util.Collections;
import java.util.List;

public class YandexTranslator implements Translator {
    private final String myApiKey;

    public YandexTranslator(final String apiKey) {
        myApiKey = apiKey;
    }

    @Override
    public List<String> translate(String splitText, String fromLang, String toLang, boolean autoDetect) {
        return translate(Collections.singletonList(splitText), fromLang, toLang, autoDetect);
    }

    @Override
    public int getMaxTextLength() {
        return 10000;
    }

    @Override
    public List<String> translate(List<String> splitText, String fromLang, String toLang, boolean autoDetect) {
        if (!splitText.isEmpty()) {
            String translated;
            YandexClient yandexClient = new YandexClient(myApiKey);
            if (autoDetect) {
                String detect = yandexClient.detect(splitText.get(0), fromLang, toLang);
                BoxedJsObject response = BoxedJson.objectFrom(detect);
                BoxedJsNumber responseCode = response.getJsNumber("code");
                BoxedJsString responseLang = response.getJsString("lang");
                if (responseCode.isValid() && responseLang.isValid() && responseCode.intValue() == 200) {
                    String language = responseLang.getString();
                    translated = yandexClient.translate(splitText, language, toLang);
                } else {
                    // failed to detect language
                    translated = yandexClient.translate(splitText, fromLang, toLang);
                }
            } else {
                translated = yandexClient.translate(splitText, fromLang, toLang);
            }

            if (translated != null) {
                //int code;
                //String lang;
                //List<String> text;
                BoxedJsObject response = BoxedJson.boxedFrom(translated);
                BoxedJsNumber responseCode = response.getJsNumber("code");
                BoxedJsString responseLang = response.getJsString("lang");
                BoxedJsArray responseText = response.getJsArray("text");
                if (responseCode.isValid() && responseLang.isValid() && responseText.isValid()) {
                    if (responseCode.intValue() == 200) {
                        return TranslateUtil.asStringList(responseText);
                    } else {
                        System.out.println("Received response code " + responseCode.intValue());
                    }
                }
            }
        }
        return Collections.emptyList();
    }
}
