// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.md.nav.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.HyperlinkLabel;
import com.vladsch.boxed.json.BoxedJsArray;
import com.vladsch.boxed.json.BoxedJsNumber;
import com.vladsch.boxed.json.BoxedJsObject;
import com.vladsch.boxed.json.BoxedJsString;
import com.vladsch.boxed.json.BoxedJson;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.util.translator.yandex.YandexClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnterYandexKeyDialog extends DialogWrapper {
    private JPanel contentPane;
    private JTextField myYandexKey;
    private HyperlinkLabel myYandexLink;
    private ValidationInfo myLastValidationInfo = null;
    private String myLastCheckedKey = null;

    public EnterYandexKeyDialog(JComponent parent, String yandexKey) {
        super(parent, false);
        init();
        setTitle(MdBundle.message("settings.translator.enter-key.title"));
        myYandexLink.setHyperlinkText("Get the Yandex.Translate API key");
        myYandexLink.addHyperlinkListener(e -> {
            if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                BrowserUtil.browse("https://tech.yandex.com/translate/");
            }
        });

        myYandexKey.setText(yandexKey);
        setModal(true);
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "MarkdownNavigator.YandexKeyDialog";
    }

    protected class MyOkAction extends OkAction {
        protected MyOkAction() {
            super();
            putValue(Action.NAME, MdBundle.message("settings.translator.yandex-key.ok"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (doValidate(true) == null) {
                getOKAction().setEnabled(true);
            }
            super.doAction(e);
        }
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        return new Action[] { new MyOkAction(), getCancelAction() };
    }

    @Nullable
    public static String showDialog(JComponent parent, final String yandexKey) {
        EnterYandexKeyDialog dialog = new EnterYandexKeyDialog(parent, yandexKey);
        if (dialog.showAndGet()) {
            final String yandexKeyText = dialog.myYandexKey.getText();
            return yandexKeyText;
        }
        return null;
    }

    protected ValidationInfo doValidate(boolean loadLicense) {
        String keyText = myYandexKey.getText();
        if (!keyText.trim().isEmpty()) {
            if (!keyText.equals(myLastCheckedKey)) {
                myLastCheckedKey = keyText;
                YandexClient client = new YandexClient(keyText);
                String translated = client.translate("испытание", "ru", "en");

                if (translated == null) {
                    //int code;
                    //String lang;
                    //List<String> text;
                    String lastError = client.getLastError();
                    if (lastError != null) {
                        // java.io.IOException: Server returned HTTP response code: 403 for URL: https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20150324T022104Z.0818b74e36297c45&lang=ru-en&text=%D0%B8%D1%81%D0%BF%D1%8B%D1%82%D0%B0%D0%BD%D0%B8%D0%B5
                        Pattern pattern = Pattern.compile("HTTP response code: (\\d{3}) for");
                        Matcher matcher = pattern.matcher(lastError);
                        if (matcher.find()) {
                            int code = Integer.parseInt(matcher.group(1));
                            switch (code) {
                                case 200:
                                    return myLastValidationInfo = super.doValidate();
                                case 401:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.401"));
                                case 402:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.402"));
                                case 403:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.401"));
                                case 404:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.404"));
                                case 413:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.413"));
                                case 422:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.422"));
                                case 501:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.501"));
                                default:
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.invalid-code", lastError));
                            }
                        }
                    } else {
                        return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.invalid-response"));
                    }
                } else {
                    BoxedJsObject response = BoxedJson.boxedFrom(translated);
                    BoxedJsNumber responseCode = response.getJsNumber("code");
                    BoxedJsString responseLang = response.getJsString("lang");
                    BoxedJsArray responseText = response.getJsArray("text");
                    if (responseCode.isValid() && responseLang.isValid() && responseText.isValid()) {
                        switch (responseCode.intValue()) {
                            case 200:
                                if (!responseText.getString(0).equals("test")) {
                                    return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.invalid-translation", responseText.getString(0)));
                                }
                                break;
                            case 401:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.401"));
                            case 402:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.402"));
                            case 404:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.404"));
                            case 413:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.413"));
                            case 422:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.422"));
                            case 501:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.501"));
                            default:
                                return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.invalid-code", responseCode.toString()));
                        }
                    } else {
                        return myLastValidationInfo = new ValidationInfo(MdBundle.message("settings.translator.error.invalid-response", responseText));
                    }
                    //return new ValidationInfo(MdBundle.message("settings.translator.error.invalid-translation"));
                }
            } else if (myLastValidationInfo != null) {
                return myLastValidationInfo;
            }
        }
        myLastValidationInfo = null;
        return super.doValidate();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return doValidate(false);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myYandexKey;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
