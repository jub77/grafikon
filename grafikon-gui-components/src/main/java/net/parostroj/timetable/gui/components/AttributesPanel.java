/*
 * AttributesPanel.java
 *
 * Created on 30.3.2011, 10:22:55
 */
package net.parostroj.timetable.gui.components;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Attributes;

/**
 * Panel with table with attributes.
 *
 * @author jub
 */
public class AttributesPanel extends javax.swing.JPanel {

    private AttributesTableModel attributesTableModel;
    private String category;

    /** Creates new form AttributesPanel */
    public AttributesPanel() {
        initComponents();
        attributesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removeButton.setEnabled(attributesTable.getSelectedRow() != -1);
                }
            }
        });
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        attributesTable = new javax.swing.JTable();
        buttonsPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        nameTextField = new javax.swing.JTextField();
        removeButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        attributesTable.setModel(new net.parostroj.timetable.gui.components.AttributesTableModel(category));
        attributesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(attributesTable);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        addButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(addButton);

        nameTextField.setColumns(20);
        nameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                nameTextFieldCaretUpdate(evt);
            }
        });
        buttonsPanel.add(nameTextField);

        removeButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(removeButton);

        add(buttonsPanel, java.awt.BorderLayout.PAGE_END);
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.attributesTableModel != null) {
            this.attributesTableModel.getAttributes().set(nameTextField.getText(), "", category);
        }
        nameTextField.setText("");
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int row = attributesTable.getSelectedRow();
        if (row != -1) {
            String name = attributesTableModel.getUserNames().get(row);
            attributesTableModel.getAttributes().remove(name, category);
        }
    }

    private void nameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {
        addButton.setEnabled(!"".equals(nameTextField.getText()));
    }

    public void startEditing(Attributes attributes) {
        attributesTableModel = new AttributesTableModel(category);
        attributesTableModel.startEditing(attributes);
        attributesTable.setModel(attributesTableModel);
        this.updateColumns();
        nameTextField.setText("");
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    public Attributes stopEditing() {
        Attributes attributes = attributesTableModel.stopEditing();
        attributesTableModel = null;
        attributesTable.setModel(new DefaultTableModel());
        return attributes;
    }

    private void updateColumns() {
        TableColumn nameColumn = attributesTable.getColumnModel().getColumn(0);
        nameColumn.setMaxWidth(200);
        nameColumn.setPreferredWidth(100);
    }

    public Attributes getAttributes() {
        return attributesTableModel.getAttributes();
    }

    private javax.swing.JButton addButton;
    private javax.swing.JTable attributesTable;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane scrollPane;
}
