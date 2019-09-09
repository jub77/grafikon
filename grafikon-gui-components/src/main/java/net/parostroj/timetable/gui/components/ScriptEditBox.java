package net.parostroj.timetable.gui.components;

import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Script;
import net.parostroj.timetable.model.Script.Language;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

/**
 * Editing component for scripts.
 *
 * @author jub
 */
public class ScriptEditBox extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

	private static final String SEARCH_ACTION = "search";
    private static final Map<Language, String> HIGHLIGHT;

    static {
        Map<Language, String> h = new EnumMap<>(Language.class);
        h.put(Language.GROOVY, SyntaxConstants.SYNTAX_STYLE_GROOVY);
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

        languageChange = true;
    }

    public void addComponentToEditBox(Component component) {
        panel.add(component);
    }

    public void removeComponentFromEditBox(Component component) {
        panel.remove(component);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        languageComboBox.setEnabled(enabled && languageChange);
        scriptTextArea.setEnabled(enabled);
        scriptTextArea.setEditable(enabled);
        scriptTextArea.setHighlightCurrentLine(enabled);
        scrollPane.setEnabled(enabled);
        scrollPane.getVerticalScrollBar().setEnabled(enabled);
        scrollPane.getHorizontalScrollBar().setEnabled(enabled);
    }

    public void setLanguageChange(boolean languageChange) {
        this.languageChange = languageChange;
    }

    public boolean isLanguageChange() {
        return languageChange;
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
        panel = new javax.swing.JPanel();
        languageComboBox = new javax.swing.JComboBox<>();
        scrollPane = new org.fife.ui.rtextarea.RTextScrollPane();
        scriptTextArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

        setLayout(new java.awt.BorderLayout());

        panel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        languageComboBox.addItemListener(new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                languageComboBoxItemStateChanged(evt);
            }
        });
        panel.add(languageComboBox);

        add(panel, java.awt.BorderLayout.PAGE_END);

        scrollPane.setLineNumbersEnabled(false);
        scrollPane.setViewportView(scriptTextArea);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        scriptTextArea.getInputMap().put(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK), SEARCH_ACTION);
        scriptTextArea.getActionMap().put(SEARCH_ACTION, new AbstractAction() {
            private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
                SearchDialog dialog = getSearchDialog();
                if (!dialog.isVisible()) {
                    dialog.setVisible(true);
                }
                if (!dialog.isFocused()) {
                    dialog.requestFocus();
                }
            }
        });
    }

    protected SearchDialog getSearchDialog() {
        if (searchDialog == null) {
            searchDialog = new SearchDialog(GuiComponentUtils.getWindow(ScriptEditBox.this), false);
            Point location = scrollPane.getLocationOnScreen();
            location.translate(10, 10);
            searchDialog.setLocation(location);
            searchDialog.setSearchFunction(data -> {
                SearchContext context = new SearchContext();
                context.setSearchFor(data.getText());
                SearchEngine.find(scriptTextArea, context);
            });
        }
        return searchDialog;
    }

    public void closeSearchDialog() {
        if (searchDialog != null && searchDialog.isVisible()) {
            searchDialog.setVisible(false);
        }
    }

    private void languageComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            scriptTextArea.setSyntaxEditingStyle(HIGHLIGHT.get(evt.getItem()));
        }
    }

    private javax.swing.JComboBox<Language> languageComboBox;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea scriptTextArea;
    private org.fife.ui.rtextarea.RTextScrollPane scrollPane;
    private javax.swing.JPanel panel;

    private SearchDialog searchDialog;

    private boolean languageChange;
}
