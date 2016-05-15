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

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.utils.ObjectsUtil;

import javax.swing.JComboBox;

/**
 * Panel with table with attributes.
 *
 * @author jub
 */
public class AttributesPanel extends javax.swing.JPanel {

    private AttributesTableModel attributesTableModel;
    private String category;

    private static enum Type {
        STRING("String", ""), BOOLEAN("Boolean", false),
        INTEGER("Integer", Integer.valueOf(0)), DOUBLE("Double", Double.valueOf(0));

        public String text;
        public Object value;

        private Type(String text, Object value) {
            this.text = text;
            this.value = value;
        }

        @Override
        public String toString() {
            return text;
        }
    }

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
        attributesTable = new javax.swing.JTable() {

            private Class<?> editingClass;

            @Override
            public javax.swing.table.TableCellRenderer getCellRenderer(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    Class<?> rowClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultRenderer(rowClass);
                } else {
                    return super.getCellRenderer(row, column);
                }
            }

            @Override
            public javax.swing.table.TableCellEditor getCellEditor(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 1) {
                    editingClass = getModel().getValueAt(row, modelColumn).getClass();
                    return getDefaultEditor(editingClass);
                } else {
                    return super.getCellEditor(row, column);
                }
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return editingClass != null ? editingClass : super.getColumnClass(column);
            }
        };
        buttonsPanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        removeButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);

        setLayout(new java.awt.BorderLayout());

        attributesTable.setModel(new net.parostroj.timetable.gui.components.AttributesTableModel(category));
        attributesTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(attributesTable);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        nameTextField.setColumns(20);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                String txt = ObjectsUtil.checkAndTrim(nameTextField.getText());
                addButton.setEnabled(txt != null);
            }
        });
        buttonsPanel.add(nameTextField);

        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        typeComboBox = new JComboBox<Type>();
        for (Type t : Type.values()) {
            typeComboBox.addItem(t);
        }
        buttonsPanel.add(typeComboBox);
        addButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);

        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);

        add(buttonsPanel, java.awt.BorderLayout.PAGE_END);
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.attributesTableModel != null) {
            Type type = (Type) typeComboBox.getSelectedItem();
            this.attributesTableModel.getAttributes().set(category, nameTextField.getText(), type.value);
        }
        nameTextField.setText("");
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int row = attributesTable.getSelectedRow();
        if (row != -1) {
            String name = attributesTableModel.getUserNames().get(row);
            attributesTableModel.getAttributes().remove(category, name);
        }
    }

    public void startEditing(Attributes attributes, String category) {
        this.setCategory(category);
        this.startEditing(attributes);
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
    private JComboBox<Type> typeComboBox;
}
