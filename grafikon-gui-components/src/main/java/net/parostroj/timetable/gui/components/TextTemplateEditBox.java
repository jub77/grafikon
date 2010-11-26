package net.parostroj.timetable.gui.components;

import java.util.Collection;

import javax.swing.text.BadLocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.GrafikonException;
import net.parostroj.timetable.model.Language;
import net.parostroj.timetable.model.TextTemplate;

/**
 * Edit box for editing text templates.
 *
 * @author jub
 */
public class TextTemplateEditBox extends javax.swing.JPanel {
    
    private static final Logger LOG = LoggerFactory.getLogger(TextTemplateEditBox.class);

    private Collection<Language> languages;

    /** Creates new form TextTemplateEditBox */
    public TextTemplateEditBox() {
        initComponents();
    }

    public void setTemplateColumns(int columns) {
        templateTextField.setColumns(columns);
    }

    public int getTemplateColumns() {
        return templateTextField.getColumns();
    }

    public void setLanguages(Collection<Language> languages) {
        this.languages = languages;
        languageComboBox.removeAllItems();
        for (Language l : languages) {
            languageComboBox.addItem(l);
        }
    }

    public Collection<? extends Language> getLanguages() {
        return this.languages;
    }

    @Override
    public void setEnabled(boolean enabled) {
        templateTextField.setEnabled(enabled);
        languageComboBox.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    public TextTemplate getTemplate() throws GrafikonException {
        String str = templateTextField.getText().trim();
        Language lang = (Language) languageComboBox.getSelectedItem();
        if (lang == null)
            throw new GrafikonException("No language selected.");
        return TextTemplate.createTextTemplate(str, lang);
    }

    public TextTemplate getTemplateEmpty() throws GrafikonException {
        String str = templateTextField.getText().trim();
        Language lang = (Language) languageComboBox.getSelectedItem();
        if (lang == null)
            throw new GrafikonException("No language selected.");
        if ("".equals(str))
            return null;
        else
            return TextTemplate.createTextTemplate(str, lang);
    }

    public void setTemplate(TextTemplate template) {
        if (template != null)
            this.setTemplate(template.getTemplate(), template.getLanguage());
        else
            this.setTemplate(null, null);
    }

    public void setTemplate(String template, Language language) {
        templateTextField.setText(template);
        templateTextField.setCaretPosition(0);
        if (language == null)
            language = languages.iterator().next();
        languageComboBox.setSelectedItem(language);
    }
    
    public void setCaretPosition(int position) {
        templateTextField.setCaretPosition(position);
    }
    
    public int getCaretPosition() {
        return templateTextField.getCaretPosition();
    }
    
    public void insertText(String text) {
        try {
            templateTextField.getDocument().insertString(templateTextField.getCaretPosition(), text, null);
        } catch (BadLocationException e) {
            LOG.warn("Error inserting text: {}", e.getMessage());
        }
    }
    
    public void requestFocusForTemplateField() {
        templateTextField.requestFocusInWindow();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        templateTextField = new javax.swing.JTextField();
        languageComboBox = new javax.swing.JComboBox();

        setLayout(new java.awt.BorderLayout());
        add(templateTextField, java.awt.BorderLayout.CENTER);

        languageComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                languageComboBoxActionPerformed(evt);
            }
        });
        add(languageComboBox, java.awt.BorderLayout.LINE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void languageComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_languageComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_languageComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JTextField templateTextField;
    // End of variables declaration//GEN-END:variables

}
