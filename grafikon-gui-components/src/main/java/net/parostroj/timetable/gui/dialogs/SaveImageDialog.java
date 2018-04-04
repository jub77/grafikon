package net.parostroj.timetable.gui.dialogs;

import java.awt.Dialog;
import java.awt.Dimension;
import java.io.File;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog for saving GT.
 *
 * @author jub
 */
public class SaveImageDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	public static enum Type {
        PNG("save.gt.png",new FileNameExtensionFilter("PNG", "png"),"png"), SVG("save.gt.svg",new FileNameExtensionFilter("SVG", "svg"),"svg");

        private String name;
        private String description;
        private FileNameExtensionFilter filter;
        private String extension;

        private Type(String name, FileNameExtensionFilter filter,String extension) {
            this.name = name;
            this.filter = filter;
            this.extension = extension;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            if (description == null)
                this.setDescription();
            return description;
        }

        private void setDescription() {
            description = ResourceLoader.getString(name);
        }

        public FileNameExtensionFilter getFilter() {
            return filter;
        }

        public String getExtension() {
            return extension;
        }

        @Override
        public String toString() {
            return this.getDescription();
        }
    }

    private File saveFile;
    private boolean save;
    private static final int DEFAULT_WIDTH = 2000;
    private static final int DEFAULT_HEIGHT = 400;
    private static final int MAX_WIDTH = 10000;
    private static final int MAX_HEIGHT = 10000;

    private static JFileChooser fileChooserInstance;

    private static synchronized JFileChooser getFileChooser() {
        if (fileChooserInstance == null)
            fileChooserInstance = new JFileChooser() {
                private static final long serialVersionUID = 1L;

				@Override
                public void approveSelection() {
                    if ((getDialogType() == JFileChooser.SAVE_DIALOG) && getSelectedFile().exists()) {
                        int result = JOptionPane.showConfirmDialog(this,
                                String.format(ResourceLoader.getString("savedialog.overwrite.text"), getSelectedFile()),
                                ResourceLoader.getString("savedialog.overwrite.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if(result != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    super.approveSelection();
                }
            };
        return fileChooserInstance;
    }

    /**
     * Creates new form SaveGTDialog.
     *
     * @param parent
     * @param modal
     */
    public SaveImageDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        init();
    }

    public SaveImageDialog(Dialog dialog, boolean modal) {
        super(dialog, modal);
        initComponents();
        init();
    }

    private void init() {
        xTextField.setValue(Integer.valueOf(DEFAULT_WIDTH));
        yTextField.setValue(Integer.valueOf(DEFAULT_HEIGHT));

        typeComboBox.setModel(new DefaultComboBoxModel<Type>(Type.values()));

        // preload file chooser
        getFileChooser();
    }

    public boolean isSave() {
        return save;
    }

    public File getSaveFile() {
        return saveFile;
    }

    public Dimension getSaveSize() {
        int x = DEFAULT_WIDTH;
        int y = DEFAULT_HEIGHT;
        if (xTextField.getValue() != null) {
            x = ((Number)xTextField.getValue()).intValue();
            if (x > MAX_WIDTH)
                x = MAX_WIDTH;
        }

        if (yTextField.getValue() != null) {
            y = ((Number)yTextField.getValue()).intValue();
            if (y > MAX_HEIGHT)
                y = MAX_HEIGHT;
        }
        return new Dimension(x, y);
    }

    public void setSaveSize(Dimension size) {
        xTextField.setValue(size.width);
        yTextField.setValue(size.height);
    }

    public Type getImageType() {
        return (Type)typeComboBox.getSelectedItem();
    }

    public void setSizeChangeEnabled(boolean enabled) {
        xTextField.setEnabled(enabled);
        yTextField.setEnabled(enabled);
    }

    public boolean isSizeChangeEnabled() {
        return xTextField.isEnabled() && yTextField.isEnabled();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        xTextField = new javax.swing.JFormattedTextField();
        yTextField = new javax.swing.JFormattedTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<Type>();

        setTitle(ResourceLoader.getString("save.gt.title")); // NOI18N
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(ResourceLoader.getString("save.gt.x")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        getContentPane().add(jLabel1, gridBagConstraints);

        jLabel2.setText(ResourceLoader.getString("save.gt.y")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        getContentPane().add(jLabel2, gridBagConstraints);

        saveButton.setText(ResourceLoader.getString("save.gt.save")); // NOI18N
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(saveButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(buttonsPanel, gridBagConstraints);

        xTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(xTextField, gridBagConstraints);

        yTextField.setColumns(20);
        yTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
        getContentPane().add(yTextField, gridBagConstraints);

        jLabel3.setText(ResourceLoader.getString("save.gt.type")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        getContentPane().add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 5);
        getContentPane().add(typeComboBox, gridBagConstraints);

        pack();
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // do nothing - close dialog
        this.save = false;
        this.setVisible(false);
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // set specific file name filter
        JFileChooser fileChooser = getFileChooser();
        fileChooser.setFileFilter(this.getImageType().getFilter());
        // show save dialog
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            saveFile = fileChooser.getSelectedFile();
            if (!saveFile.getName().toLowerCase().endsWith("." + this.getImageType().getExtension()))
                saveFile = new File(saveFile.getAbsolutePath()+"." + this.getImageType().getExtension());
            this.save = true;
        } else {
            this.save = false;
        }
        fileChooser.removeChoosableFileFilter(this.getImageType().getFilter());
        this.setVisible(false);
    }

    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox<Type> typeComboBox;
    private javax.swing.JFormattedTextField xTextField;
    private javax.swing.JFormattedTextField yTextField;
}
