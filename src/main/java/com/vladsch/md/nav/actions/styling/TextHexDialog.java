// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.vladsch.md.nav.actions.styling;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.flexmark.util.html.ui.HtmlHelpers;
import com.vladsch.md.nav.MdBundle;
import com.vladsch.md.nav.util.FastEncoder;
import com.vladsch.plugin.util.HelpersKt;
import com.vladsch.plugin.util.ui.WrapLayout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TextHexDialog extends DialogWrapper {
    JPanel contentPane;
    private JPanel myOptionsPanel;
    private JTextPane myOptionsTextPane;
    JTextArea myOptionsTextArea;
    private JButton myCleanAllButton;
    private JButton myRemoveAllButton;
    private JScrollPane myOptionsScrollPane;
    JButton myOkAndSetButton;
    HashMap<JBCheckBox, String> myCheckBoxOptions = new HashMap<JBCheckBox, String>();
    @Nullable String myText;
    @NotNull final private Color myInvisibleColor;
    @NotNull final private Color myInvalidColor;
    @NotNull final private Color myEscapeColor;
    @Nullable final private Color myEscapeBackColor;
    @NotNull final private Color myDeprecatedColor;

    static class UnicodeChar {
        final @Nullable Color background;
        final char character;
        final String cleanSubstitute;
        final boolean isVisible;
        final boolean isDeprecated;
        final int codePoint;
        final String description;
        final String abbreviation;
        final Character ctrlChar;
        final boolean isNormal;

        public UnicodeChar(@Nullable Color background, char character, String cleanSubstitute, boolean isVisible, boolean isDeprecated, String description, String abbreviation, Character ctrlChar) {
            this.background = background;
            this.character = character;
            this.cleanSubstitute = cleanSubstitute;
            this.isVisible = isVisible;
            this.isDeprecated = isDeprecated;
            this.codePoint = Character.codePointAt(new char[] { character }, 0);
            this.description = description;
            this.abbreviation = abbreviation;
            this.ctrlChar = ctrlChar;
            this.isNormal = character == ' ' || character == '\t' || character == '\n' || character == '\r';
        }
    }

    static HashMap<Character, UnicodeChar> CHARACTERS = new HashMap<Character, UnicodeChar>();

    static void addChar(UnicodeChar unicodeChar) {
        CHARACTERS.put(unicodeChar.character, unicodeChar);
    }

    static Color COMMON_ASCII = JBColor.ORANGE;
    static Color COMMON_UNICODE = JBColor.YELLOW;
    static Color DO_NOT_USE = JBColor.RED;
    static {
        addChar(new UnicodeChar(DO_NOT_USE, '\u0000', "", false, false, "Null character", "NUL", '@'));
        addChar(new UnicodeChar(null, '\u0001', "", false, false, "Start of Heading", "SOH", 'A'));
        addChar(new UnicodeChar(null, '\u0002', "", false, false, "Start of Text", "STX", 'B'));
        addChar(new UnicodeChar(DO_NOT_USE, '\u0003', "", false, false, "End-of-text character", "ETX", 'C'));
        addChar(new UnicodeChar(null, '\u0004', "", false, false, "End-of-transmission character", "EOT", 'D'));
        addChar(new UnicodeChar(null, '\u0005', "", false, false, "Enquiry character", "ENQ", 'E'));
        addChar(new UnicodeChar(null, '\u0006', "", false, false, "Acknowledge character", "ACK", 'F'));
        addChar(new UnicodeChar(COMMON_ASCII, '\b', "", false, false, "Bell character", "BEL", 'G'));
        addChar(new UnicodeChar(COMMON_ASCII, '\u0008', "", false, false, "Backspace", "BS", 'H'));
        addChar(new UnicodeChar(COMMON_ASCII, '\t', " ", true, false, "Horizontal tab", "HT", 'I'));
        addChar(new UnicodeChar(COMMON_ASCII, '\n', "\n", true, false, "Line feed", "LF", 'J'));
        addChar(new UnicodeChar(null, '\u000B', "", false, false, "Vertical tab", "VT", 'K'));
        addChar(new UnicodeChar(COMMON_ASCII, '\f', "", false, false, "Form feed", "FF", 'L'));
        addChar(new UnicodeChar(COMMON_ASCII, '\r', "\n", false, false, "Carriage return", "CR", 'M'));
        addChar(new UnicodeChar(null, '\u000E', "", false, false, "Shift Out", "SO", 'N'));
        addChar(new UnicodeChar(null, '\u000F', "", false, false, "Shift In", "SI", 'O'));
        addChar(new UnicodeChar(null, '\u0010', "", false, false, "Data Link Escape", "DLE", 'P'));
        addChar(new UnicodeChar(null, '\u0011', "", false, false, "Device Control 1", "DC1", 'Q'));
        addChar(new UnicodeChar(null, '\u0012', "", false, false, "Device Control 2", "DC2", 'R'));
        addChar(new UnicodeChar(null, '\u0013', "", false, false, "Device Control 3", "DC3", 'S'));
        addChar(new UnicodeChar(null, '\u0014', "", false, false, "Device Control 4", "DC4", 'T'));
        addChar(new UnicodeChar(null, '\u0015', "", false, false, "Negative-acknowledge character", "NAK", 'U'));
        addChar(new UnicodeChar(null, '\u0016', "", false, false, "Synchronous Idle", "SYN", 'V'));
        addChar(new UnicodeChar(null, '\u0017', "", false, false, "End of Transmission Block", "ETB", 'W'));
        addChar(new UnicodeChar(null, '\u0018', "", false, false, "Cancel character", "CAN", 'X'));
        addChar(new UnicodeChar(null, '\u0019', "", false, false, "End of Medium", "EM", 'Y'));
        addChar(new UnicodeChar(null, '\u001A', "", false, false, "Substitute character", "SUB", 'Z'));
        addChar(new UnicodeChar(COMMON_ASCII, '\u001B', "", false, false, "Escape character", "ESC", '['));
        addChar(new UnicodeChar(null, '\u001C', "", false, false, "File Separator", "FS", '\\'));
        addChar(new UnicodeChar(null, '\u001D', "", false, false, "Group Separator", "GS", ']'));
        addChar(new UnicodeChar(null, '\u001E', "", false, false, "Record Separator", "RS", '^'));
        addChar(new UnicodeChar(null, '\u001F', "", false, false, "Unit Separator", "US", '_'));
        addChar(new UnicodeChar(null, ' ', " ", true, false, "Space", "SP", null));
        addChar(new UnicodeChar(COMMON_ASCII, '\u007F', "", false, false, "Delete", "DEL", null));
        addChar(new UnicodeChar(null, '\u0080', "", false, false, "Padding Character", "PAD", null));
        addChar(new UnicodeChar(null, '\u0081', "", false, false, "High Octet Preset", "HOP", null));
        addChar(new UnicodeChar(null, '\u0082', "", false, false, "Break Permitted Here", "BPH", null));
        addChar(new UnicodeChar(null, '\u0083', "", false, false, "No Break Here", "NBH", null));
        addChar(new UnicodeChar(null, '\u0084', "", false, false, "Index", "IND", null));
        addChar(new UnicodeChar(null, '\u0085', "", false, false, "Next Line", "NEL", null));
        addChar(new UnicodeChar(null, '\u0086', "", false, false, "Start of Selected Area", "SSA", null));
        addChar(new UnicodeChar(null, '\u0087', "", false, false, "End of Selected Area", "ESA", null));
        addChar(new UnicodeChar(null, '\u0088', "", false, false, "Character Tabulation Set", "HTS", null));
        addChar(new UnicodeChar(null, '\u0089', "", false, false, "Character Tabulation with Justification", "HTJ", null));
        addChar(new UnicodeChar(null, '\u008A', "", false, false, "Line Tabulation Set", "VTS", null));
        addChar(new UnicodeChar(null, '\u008B', "", false, false, "Partial Line Forward", "PLD", null));
        addChar(new UnicodeChar(null, '\u008C', "", false, false, "Partial Line Backward", "PLU", null));
        addChar(new UnicodeChar(null, '\u008D', "", false, false, "Reverse Line Feed", "RI", null));
        addChar(new UnicodeChar(null, '\u008E', "", false, false, "Single-Shift Two", "SS2", null));
        addChar(new UnicodeChar(null, '\u008F', "", false, false, "Single-Shift Three", "SS3", null));
        addChar(new UnicodeChar(null, '\u0090', "", false, false, "Device Control String", "DCS", null));
        addChar(new UnicodeChar(null, '\u0091', "", false, false, "Private Use 1", "PU1", null));
        addChar(new UnicodeChar(null, '\u0092', "", false, false, "Private Use 2", "PU2", null));
        addChar(new UnicodeChar(null, '\u0093', "", false, false, "Set Transmit State", "STS", null));
        addChar(new UnicodeChar(null, '\u0094', "", false, false, "Cancel character", "CCH", null));
        addChar(new UnicodeChar(null, '\u0095', "", false, false, "Message Waiting", "MW", null));
        addChar(new UnicodeChar(null, '\u0096', "", false, false, "Start of Protected Area", "SPA", null));
        addChar(new UnicodeChar(null, '\u0097', "", false, false, "End of Protected Area", "EPA", null));
        addChar(new UnicodeChar(null, '\u0098', "", false, false, "Start of String", "SOS", null));
        addChar(new UnicodeChar(null, '\u0099', "", false, false, "Single Graphic Character Introducer", "SGCI", null));
        addChar(new UnicodeChar(null, '\u009A', "", false, false, "Single Character Intro Introducer", "SCI", null));
        addChar(new UnicodeChar(null, '\u009B', "", false, false, "Control Sequence Introducer", "CSI", null));
        addChar(new UnicodeChar(null, '\u009C', "", false, false, "String Terminator", "ST", null));
        addChar(new UnicodeChar(null, '\u009D', "", false, false, "Operating System Command", "OSC", null));
        addChar(new UnicodeChar(null, '\u009E', "", false, false, "Private Message", "PM", null));
        addChar(new UnicodeChar(null, '\u009F', "", false, false, "Application Program Command", "APC", null));
        addChar(new UnicodeChar(COMMON_UNICODE, '\u00A0', " ", false, false, "No-Break space character", "NB SP", null));
        addChar(new UnicodeChar(null, '\u2000', " ", false, false, "EN Quad", "NQ SP", null));
        addChar(new UnicodeChar(null, '\u2001', " ", false, false, "EM Quad", "MQ SP", null));
        addChar(new UnicodeChar(null, '\u2002', " ", false, false, "EN Space", "EN SP", null));
        addChar(new UnicodeChar(null, '\u2003', " ", false, false, "EM Space", "EM SP", null));
        addChar(new UnicodeChar(null, '\u2004', " ", false, false, "Three-per-EM SPACE", "3/M SP", null));
        addChar(new UnicodeChar(null, '\u2005', " ", false, false, "Four-per-EM SPACE", "4/M SP", null));
        addChar(new UnicodeChar(null, '\u2006', " ", false, false, "Six-per-EM SPACE", "6/M SP", null));
        addChar(new UnicodeChar(null, '\u2007', " ", false, false, "Figure Space", "F SP", null));
        addChar(new UnicodeChar(null, '\u2008', " ", false, false, "Punctuation Space", "P SP", null));
        addChar(new UnicodeChar(null, '\u2009', " ", false, false, "Thin Space", "TH SP", null));
        addChar(new UnicodeChar(null, '\u200A', " ", false, false, "Hair Space", "H SP", null));
        addChar(new UnicodeChar(null, '\u200B', "", false, false, "Zero Width Space", "ZW SP", null));
        addChar(new UnicodeChar(null, '\u200C', "", false, false, "Zero Width Non-JOINER", "ZW NJ", null));
        addChar(new UnicodeChar(null, '\u200D', "", false, false, "Zero Width Joiner", "ZW J", null));
        addChar(new UnicodeChar(null, '\u200E', "", false, false, "Left-to-RIGHT MARK", "LRM", null));
        addChar(new UnicodeChar(null, '\u200F', "", false, false, "Right-to-LEFT MARK", "RLM", null));
        addChar(new UnicodeChar(COMMON_UNICODE, '\u2028', "\n", false, false, "Line separator", "L SEP", null));
        addChar(new UnicodeChar(null, '\u2060', "", false, false, "Word Joiner", "WJ", null));
        addChar(new UnicodeChar(null, '\u2061', "", false, false, "Function Application", "f()", null));
        addChar(new UnicodeChar(null, '\u2062', "", false, false, "Invisible Times", "INV x", null));
        addChar(new UnicodeChar(null, '\u2063', "", false, false, "Invisible Separator", "INV ,", null));
        addChar(new UnicodeChar(null, '\u2064', "", false, false, "Invisible Plus", "INV +", null));
        addChar(new UnicodeChar(null, '\u2066', "", false, false, "Left-to-right Isolate", "LRI", null));
        addChar(new UnicodeChar(null, '\u2067', "", false, false, "Right-to-left Isolate", "RLI", null));
        addChar(new UnicodeChar(null, '\u2068', "", false, false, "First Strong Isolate", "FSI", null));
        addChar(new UnicodeChar(null, '\u2069', "", false, false, "Pop Directional Isolate", "PDI", null));
        addChar(new UnicodeChar(null, '\u206A', "", false, true, "Inhibit Symmetric Swapping", "I SS", null));
        addChar(new UnicodeChar(null, '\u206B', "", false, true, "Activate Symmetric Swapping", "A SS", null));
        addChar(new UnicodeChar(null, '\u206C', "", false, true, "Inhibit Arabic Form Shaping", "I AFS", null));
        addChar(new UnicodeChar(null, '\u206D', "", false, true, "Activate Arabic Form Shaping", "A AFS", null));
        addChar(new UnicodeChar(null, '\u206E', "", false, true, "Notational Digit Shapers", "NA DS", null));
        addChar(new UnicodeChar(null, '\u206F', "", false, true, "Nominal Digit Shapers", "NO DS", null));
        addChar(new UnicodeChar(null, '\uFEFF', "", false, false, "Zero width no-break space", "ZW NB SP", null));
    }
    @NotNull
    <T> T ifNull(@NotNull T lastResortValue, @NotNull T... values) {
        for (T aValue : values) {
            if (aValue != null) return aValue;
        }
        return lastResortValue;
    }

    @NotNull
    String cleanAllExcept(@NotNull String text, @Nullable String fixedSubstitute, char... exceptions) {
        Set<Character> except = new HashSet<Character>();
        for (char c : exceptions) except.add(c);

        StringBuilder out = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            UnicodeChar uc = CHARACTERS.get(c);
            if (!except.contains(c) && uc != null) {
                if (fixedSubstitute == null) {
                    if (!uc.cleanSubstitute.isEmpty()) {
                        if (c == '\r') {
                            // if followed by \n then just delete
                            if (i + 1 < text.length() && text.charAt(i + 1) != '\n') {
                                out.append(uc.cleanSubstitute);
                            }
                        } else {
                            out.append(uc.cleanSubstitute);
                        }
                    }
                } else if (!fixedSubstitute.isEmpty()) {
                    out.append(fixedSubstitute);
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    void appendChars(String s) {
        myOptionsTextArea.replaceRange(s, myOptionsTextArea.getSelectionStart(), myOptionsTextArea.getSelectionEnd());
    }

    public TextHexDialog(JComponent parent, final @NotNull String text) {
        super(parent, false);
        myText = null;

        //myOptionsTextArea.setVisible(false);
        myOptionsTextArea.setText(text);
        myRemoveAllButton.setVisible(false);
        myCleanAllButton.setVisible(false);
        myOkAndSetButton.setVisible(false);
        myOptionsTextPane.setFont(myOptionsTextArea.getFont());

        // configure colors
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        Color textColor = ifNull(myOptionsTextArea.getForeground(), scheme.getAttributes(HighlighterColors.TEXT).getForegroundColor());
        myInvisibleColor = ifNull(textColor, scheme.getAttributes(DefaultLanguageHighlighterColors.LINE_COMMENT).getForegroundColor(), scheme.getAttributes(DefaultLanguageHighlighterColors.BLOCK_COMMENT).getForegroundColor());
        myInvalidColor = ifNull(textColor, scheme.getAttributes(CodeInsightColors.ERRORS_ATTRIBUTES).getForegroundColor(), JBColor.RED);
        myEscapeColor = ifNull(textColor, scheme.getAttributes(DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE).getForegroundColor(), JBColor.BLUE);
        myEscapeBackColor = scheme.getAttributes(DefaultLanguageHighlighterColors.VALID_STRING_ESCAPE).getBackgroundColor();
        myDeprecatedColor = ifNull(textColor, scheme.getAttributes(CodeInsightColors.DEPRECATED_ATTRIBUTES).getForegroundColor(), JBColor.BLACK);

        // add special character buttons
        ArrayList<UnicodeChar> values = new ArrayList<UnicodeChar>(CHARACTERS.values());
        Collections.sort(values, (Comparator<Object>) (o, t1) -> ((UnicodeChar) o).codePoint - ((UnicodeChar) t1).codePoint);

        for (UnicodeChar unicodeChar : values) {
            final int finalI = Character.codePointAt(String.valueOf(unicodeChar.character), 0);
            JButton charButton = new JButton(unicodeChar.abbreviation);
            String tipText = unicodeChar.description;
            tipText += String.format(" '\\u%04X'", unicodeChar.codePoint);
            if (unicodeChar.ctrlChar != null) tipText += " ^" + unicodeChar.ctrlChar;

            if (!Character.isValidCodePoint(unicodeChar.codePoint) || unicodeChar.character == '\0') {
                if (unicodeChar.character != '\0') tipText = "Invalid: " + tipText;
                charButton.setForeground(myInvalidColor);
            } else {
                if (unicodeChar.isDeprecated) {
                    tipText = "Deprecated: " + tipText;
                    charButton.setForeground(myDeprecatedColor);
                    charButton.setFont(new Font(myRemoveAllButton.getFont().getFontName(), Font.BOLD | Font.ITALIC, myRemoveAllButton.getFont().getSize()));
                } else if (unicodeChar.ctrlChar != null) {
                    if (unicodeChar.isNormal || unicodeChar.isVisible) {
                        charButton.setForeground(myEscapeColor);
                    }
                    if (myEscapeBackColor != null) charButton.setBackground(myEscapeBackColor);
                }
            }

            if (unicodeChar.background != null) {
                charButton.setBackground(HtmlHelpers.mixedColor(myOptionsTextArea.getBackground(), unicodeChar.background));
            }

            charButton.setToolTipText(tipText);
            final String s = String.valueOf(Character.toChars(finalI));

            charButton.addActionListener(event -> appendChars(s));

            myOptionsPanel.add(charButton);
        }

        myOptionsTextArea.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull DocumentEvent event) {
                ApplicationManager.getApplication().invokeLater(() -> updateHexView());
            }
        });

        myOptionsPanel.setVisible(true);

        init();
        setTitle(MdBundle.message("debug.actions.show-text-hex.title"));
        setModal(true);

        updateHexView();
    }

    String rgbColor(@NotNull Color color) {
        return HelpersKt.toRgbString(color);
    }

    protected void updateHexView() {
        String optionsText = "&nbsp;";
        String myText = myOptionsTextArea.getText();
        if (myText.length() > 0) {
            StringBuilder out = new StringBuilder();
            Font font = myOptionsTextArea.getFont();
            out.append("<span style='font-family:").append(font.getFontName()).append(";").append("font-size:").append(font.getSize()).append("pt;'>");
            int i = 0;
            for (; i < myText.length(); ) {
                char c = myText.charAt(i);
                int codePoint = Character.codePointAt(myText, i);
                int codePointChars = Character.charCount(codePoint);

                byte[] bytes = null;

                if (codePointChars == 1) {
                    bytes = myText.substring(i, i + codePointChars).getBytes();
                }

                boolean hadColor = false;
                UnicodeChar unicodeChar = CHARACTERS.get(c);

                if (!Character.isValidCodePoint(codePoint) || c == '\0') {
                    hadColor = true;
                    out.append("<span style='color:").append(rgbColor(myInvalidColor)).append(";'>");
                } else if (unicodeChar != null) {
                    if (unicodeChar.isDeprecated) {
                        hadColor = true;
                        out.append("<span style='color:").append(rgbColor(myDeprecatedColor)).append("; font-style:italic; font-weight:bold;'>");
                    } else if (unicodeChar.ctrlChar != null) {
                        hadColor = true;

                        if (unicodeChar.isNormal || unicodeChar.isVisible) {
                            out.append("<span style='color:").append(rgbColor(myEscapeColor));
                        } else {
                            out.append("<span style='color:").append(rgbColor(myInvisibleColor));
                        }

                        if (myEscapeBackColor != null) out.append("; background-color:").append(rgbColor(myEscapeBackColor)).append(";'>");
                        else out.append(";'>");
                    } else if (!unicodeChar.isVisible) {
                        hadColor = true;
                        out.append("<span style='color:").append(rgbColor(myInvisibleColor)).append(";'>");
                    }
                }

                switch (c) {
                    case '\0':
                        out.append("\\0");
                        break;
                    case '\n':
                        out.append("\\n");
                        break;
                    case '\t':
                        out.append("\\t");
                        break;
                    case '\f':
                        out.append("\\f");
                        break;
                    case '\b':
                        out.append("\\b");
                        break;

                    default:
                        if (unicodeChar != null) {
                            out.append(unicodeChar.abbreviation);
                            if (unicodeChar.ctrlChar != null) {
                                out.append(" ^").append(unicodeChar.ctrlChar);
                            }
                        } else {
                            out.append(ifNull(c, FastEncoder.encode(c)));
                        }
                        break;
                }
                out.append(":");

                if (bytes != null) {
                    for (byte b : bytes) {
                        out.append(String.format("%1$02X", b));
                    }
                } else {
                    // code point
                    out.append(String.format("U+%1$X", codePoint));
                }

                if (hadColor) {
                    out.append("</span>");
                }

                if (c == '\n') {
                    out.append("<br />");
                } else {
                    out.append(" ");
                }

                //boolean bold = !myText.get(option).isSelected;
                //if (bold) out.append("<strong>");
                //out.append("<span style='color:").append(rgbColor(myText.get(option).color)).append(";'>");
                //out.append(option).append("</span>");
                //if (bold) out.append("</strong>");
                i += codePointChars;
            }
            out.append("</span>");
            if (i > 0) optionsText = out.toString();
        }
        myOptionsTextPane.setText(optionsText);
        myOptionsTextPane.validate();
        myOptionsScrollPane.validate();
        myOptionsScrollPane.getParent().validate();
        contentPane.validate();
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "MarkdownNavigator.ShowTextHexDialog";
    }

    private void createUIComponents() {
        myOptionsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT));
        myOptionsPanel.setVisible(false);
    }

    protected class MyOkAction extends OkAction {
        protected MyOkAction() {
            super();
            putValue(Action.NAME, MdBundle.message("debug.show-text-hex.ok-and-set.label"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            myOkAndSetButton.setEnabled(false);
            if (doValidate() == null) {
                getOKAction().setEnabled(true);
                myOkAndSetButton.setEnabled(true);
            }
            super.doAction(e);
        }
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        return new Action[] { new MyOkAction(), getCancelAction(), };
    }

    protected class MyAction extends OkAction {
        final private Runnable runnable;

        protected MyAction(String name, Runnable runnable) {
            super();
            putValue(Action.NAME, name);
            this.runnable = runnable;
        }

        @Override
        protected void doAction(ActionEvent e) {
            runnable.run();
        }
    }

    //MyAction myOkAndSetAction = new MyAction(MdBundle.message("debug.show-text-hex.ok-and-set.label"), new Runnable() {
    //    @Override
    //    public void run() {
    //        myText = myOptionsTextArea.getText();
    //        doOKAction();
    //    }
    //});

    @NotNull
    protected Action[] createLeftSideActions() {
        return new Action[] {
                new MyAction(MdBundle.message("debug.show-text-hex.remove-all.label"), () -> myOptionsTextArea.setText(cleanAllExcept(myOptionsTextArea.getText(), "", ' ', '\t', '\n'))),
                new MyAction(MdBundle.message("debug.show-text-hex.clean-all.label"), () -> myOptionsTextArea.setText(cleanAllExcept(myOptionsTextArea.getText(), null))),
        };
    }

    @Nullable
    public static String showDialog(JComponent parent, String text) {
        TextHexDialog dialog = new TextHexDialog(parent, text);
        if (dialog.showAndGet()) {
            return dialog.myOptionsTextArea.getText();
        }
        return null;
    }

    protected ValidationInfo doValidate(boolean doActionIfValid) {
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
        return myOptionsPanel;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return contentPane;
    }
}
