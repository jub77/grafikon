package net.parostroj.timetable.gui.dialogs;

import java.io.File;
import javax.swing.JFileChooser;
import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog for template selection.
 *
 * @author jub
 */
public class TemplateSelectDialog extends javax.swing.JDialog {

    private File template;
    private JFileChooser chooser;
    private boolean okPressed;

    /** Creates new form TemplateSelectDialog */
    public TemplateSelectDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public boolean selectTemplate(JFileChooser chooser, File file) {
        this.chooser = chooser;
        this.template = file;
        if (template != null)
            templateTextField.setText(template.getPath());
        this.okPressed = false;
        // set position
        this.setLocationRelativeTo(getParent());
        // show dialog
        this.setVisible(true);
        // forget chooser
        this.chooser = null;
        return okPressed;
    }

    public File getTemplate() {
        return template;
    }

    private void initComponents() {
        javax.swing.JPanel selectPanel = new javax.swing.JPanel();
        templateTextField = new javax.swing.JTextField();
        javax.swing.JButton selectButton = new javax.swing.JButton();
        javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("templateselectdialog.title")); // NOI18N

        templateTextField.setColumns(35);
        templateTextField.setEditable(false);
        selectPanel.add(templateTextField);

        selectButton.setText(ResourceLoader.getString("button.select")); // NOI18N
        selectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectButtonActionPerformed(evt);
            }
        });
        selectPanel.add(selectButton);

        getContentPane().add(selectPanel, java.awt.BorderLayout.CENTER);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(cancelButton);

        getContentPane().add(buttonsPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        okPressed = true;
        setVisible(false);
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int returnValue = chooser.showOpenDialog(getParent());
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            this.template = chooser.getSelectedFile();
            // update text string
            templateTextField.setText(this.template.getPath());
        }
    }

    private javax.swing.JTextField templateTextField;
}
