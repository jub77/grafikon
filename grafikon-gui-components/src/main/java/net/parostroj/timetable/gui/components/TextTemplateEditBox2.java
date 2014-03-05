/*
 * TextTemplateEditBox2.java
 *
 * Created on 14.4.2011, 10:22:31
 */
package net.parostroj.timetable.gui.components;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TextTemplate.Language;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * Edit box for text template (text area).
 *
 * @author jub
 */
public class TextTemplateEditBox2 extends javax.swing.JPanel {

    private static final Map<Language, String> HIGHLIGHT;

    static {
        Map<Language, String> h = new EnumMap<Language, String>(Language.class);
        h.put(Language.GROOVY, SyntaxConstants.SYNTAX_STYLE_JSP);
        h.put(Language.MVEL, SyntaxConstants.SYNTAX_STYLE_HTML);
        h.put(Language.PLAIN, SyntaxConstants.SYNTAX_STYLE_HTML);
        HIGHLIGHT = Collections.unmodifiableMap(h);
    }

    /** Creates new form TextTemplateEditBox2 */
    public TextTemplateEditBox2() {
        initComponents();

        for (Language language : Language.values()) {
            languageComboBox.addItem(language);
        }

        Border border = new CompoundBorder(new EmptyBorder(5, 5, 0, 5), scrollPane.getBorder());
        scrollPane.setBorder(border);
        templateTextArea.setTabsEmulated(true);
        templateTextArea.setTabSize(4);
    }

    public void setTemplateLanguages(Collection<Language> languages) {
        languageComboBox.removeAllItems();
        for (Language language : languages) {
            languageComboBox.addItem(language);
        }
    }

    public void enableTemplateLanguageChange(boolean enabled) {
        languageComboBox.setEnabled(enabled);
    }

    public void setColumns(int columns) {
        templateTextArea.setColumns(columns);
    }

    public int getColumns() {
        return templateTextArea.getColumns();
    }

    public void setRows(int rows) {
        templateTextArea.setRows(rows);
    }

    public int getRows() {
        return templateTextArea.getRows();
    }

    public void setTemplateFont(Font font) {
        templateTextArea.setFont(font);
    }

    public Font getTemplateFont() {
        return templateTextArea.getFont();
    }

    public String getTemplateText() {
        return templateTextArea.getText();
    }

    public void setTemplateText(String text) {
        templateTextArea.setText(text);
        templateTextArea.setCaretPosition(0);
    }

    public Language getTemplateLanguage() {
        return (Language) languageComboBox.getSelectedItem();
    }

    public void setTemplateLanguage(Language language) {
        languageComboBox.setSelectedItem(language);
    }

    public TextTemplate getTemplate() throws GrafikonException {
        return TextTemplate.createTextTemplate(getTemplateText(), getTemplateLanguage());
    }

    public void setTemplate(TextTemplate template) {
        if (template != null) {
            setTemplateText(template.getTemplate());
            setTemplateLanguage(template.getLanguage());
        } else {
            setTemplateText("");
            setTemplateLanguage(Language.GROOVY);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        languageComboBox.setEnabled(enabled);
        scrollPane.setEnabled(enabled);
        templateTextArea.setEnabled(enabled);
        templateTextArea.setEditable(enabled);
        super.setEnabled(enabled);
    }

    private void initComponents() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        languageComboBox = new javax.swing.JComboBox();
        scrollPane = new org.fife.ui.rtextarea.RTextScrollPane();
        templateTextArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

        setLayout(new java.awt.BorderLayout());

        panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        languageComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                languageComboBoxItemStateChanged(evt);
            }
        });
        panel.add(languageComboBox);

        add(panel, java.awt.BorderLayout.PAGE_END);

        scrollPane.setLineNumbersEnabled(false);

        templateTextArea.setColumns(20);
        templateTextArea.setRows(5);
        scrollPane.setViewportView(templateTextArea);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    private void languageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            templateTextArea.setSyntaxEditingStyle(HIGHLIGHT.get(evt.getItem()));
        }
    }

    private javax.swing.JComboBox languageComboBox;
    private org.fife.ui.rtextarea.RTextScrollPane scrollPane;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea templateTextArea;
}
