package net.parostroj.timetable.gui.components;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.Script.Language;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * Editing component for scripts.
 *
 * @author jub
 */
public class ScriptEditBox extends javax.swing.JPanel {

    private static final Map<Language, String> HIGHLIGHT;

    static {
        Map<Language, String> h = new EnumMap<Language, String>(Language.class);
        h.put(Language.GROOVY, SyntaxConstants.SYNTAX_STYLE_GROOVY);
        h.put(Language.JAVASCRIPT, SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        HIGHLIGHT = Collections.unmodifiableMap(h);
    }

    /** Creates new form ScriptEditBox */
    public ScriptEditBox() {
        initComponents();

        for (Language language : Language.values()) {
            languageComboBox.addItem(language);
        }

        Border border = new CompoundBorder(new EmptyBorder(5, 5, 0, 5), scrollPane.getBorder());
        scrollPane.setBorder(border);
        scriptTextArea.setTabsEmulated(true);
        scriptTextArea.setTabSize(4);
    }

    public void setColumns(int columns) {
        scriptTextArea.setColumns(columns);
    }

    public int getColumns() {
        return scriptTextArea.getColumns();
    }

    public void setRows(int rows) {
        scriptTextArea.setRows(rows);
    }

    public int getRows() {
        return scriptTextArea.getRows();
    }

    public void setScriptFont(Font font) {
        scriptTextArea.setFont(font);
    }

    public Font getScriptFont() {
        return scriptTextArea.getFont();
    }

    public String getScriptText() {
        return scriptTextArea.getText();
    }

    public void setScriptText(String text) {
        scriptTextArea.setText(text);
        scriptTextArea.setCaretPosition(0);
    }

    public Language getScriptLanguage() {
        return (Language) languageComboBox.getSelectedItem();
    }

    public void setScriptLanguage(Language language) {
        languageComboBox.setSelectedItem(language);
    }

    public Script getScript() throws GrafikonException {
        return Script.createScript(getScriptText(), getScriptLanguage());
    }

    public void setScript(Script script) {
        if (script != null) {
            setScriptText(script.getSourceCode());
            setScriptLanguage(script.getLanguage());
        } else {
            setScriptText("");
            setScriptLanguage(Language.GROOVY);
        }
    }

    private void initComponents() {
        javax.swing.JPanel panel = new javax.swing.JPanel();
        languageComboBox = new javax.swing.JComboBox();
        scrollPane = new org.fife.ui.rtextarea.RTextScrollPane();
        scriptTextArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

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
        scrollPane.setViewportView(scriptTextArea);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    private void languageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            scriptTextArea.setSyntaxEditingStyle(HIGHLIGHT.get(evt.getItem()));
        }
    }

    private javax.swing.JComboBox languageComboBox;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea scriptTextArea;
    private org.fife.ui.rtextarea.RTextScrollPane scrollPane;
}
