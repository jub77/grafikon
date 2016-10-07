/*
 * AttributesPanel.java
 *
 * Created on 30.3.2011, 10:22:55
 */
package net.parostroj.timetable.gui.components;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.event.WeakPropertyChangeListener;

import net.parostroj.timetable.gui.pm.ModelAttributesPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.utils.ObjectsUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import java.awt.FlowLayout;
import java.util.function.Function;

/**
 * Panel with table with attributes.
 *
 * @author jub
 */
public class AttributesPanel extends javax.swing.JPanel implements ModelSubscriber, View<ModelAttributesPM> {

    private AttributesTableModel attributesTableModel;
    private String category;
    private Function<String, String> nameTranslation;

    private Link link;
    private ModelProvider localModelProvider;
    private WeakPropertyChangeListener listener;
    private WeakPropertyChangeListener finishedListener;

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
        this.link = new Link(this);
        this.localModelProvider = new ModelProvider();
        attributesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    removeButton.setEnabled(attributesTable.getSelectedRow() != -1);
                }
            }
        });
        listener = event -> {
            ModelAttributesPM pm = getPresentationModel();
            Attributes lAttributes = pm.getAttributes();
            String lCategory = pm.getCategory();
            nameTranslation = pm.getNameTranslation();
            startEditing(lAttributes, lCategory);
        };
        finishedListener = event -> {
            stopEditing();
        };
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
                if (modelColumn == 2) {
                    Object value = getModel().getValueAt(row, modelColumn);
                    Class<?> rowClass = value != null ? value.getClass() : String.class;
                    if (Number.class.isAssignableFrom(rowClass)) {
                        rowClass = String.class;
                    }
                    TableCellRenderer renderer = getDefaultRenderer(rowClass);
                    if (rowClass == Boolean.class) {
                        ((JCheckBox) renderer).setHorizontalAlignment(JCheckBox.LEFT);
                    }
                    return renderer;
                } else {
                    return super.getCellRenderer(row, column);
                }
            }

            @Override
            public javax.swing.table.TableCellEditor getCellEditor(int row, int column) {
                editingClass = null;
                int modelColumn = convertColumnIndexToModel(column);
                if (modelColumn == 2) {
                    editingClass = getModel().getValueAt(row, modelColumn).getClass();
                    Class<?> rowClass = editingClass;
                    if (Number.class.isAssignableFrom(rowClass)) {
                        rowClass = String.class;
                    }
                    TableCellEditor editor = getDefaultEditor(rowClass);
                    if (editingClass == Boolean.class) {
                        ((JCheckBox) ((DefaultCellEditor) editor).getComponent()).setHorizontalAlignment(JCheckBox.LEFT);
                    }
                    return editor;
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

        FlowLayout fl_buttonsPanel = new FlowLayout(FlowLayout.LEFT);
        fl_buttonsPanel.setVgap(0);
        fl_buttonsPanel.setHgap(0);
        buttonsPanel.setLayout(fl_buttonsPanel);

        nameTextField.setColumns(20);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                String txt = ObjectsUtil.checkAndTrim(nameTextField.getText());
                addButton.setEnabled(txt != null);
            }
        });
        buttonsPanel.add(nameTextField);
        buttonsPanel.add(Box.createHorizontalStrut(5));

        removeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        typeComboBox = new JComboBox<>();
        for (Type t : Type.values()) {
            typeComboBox.addItem(t);
        }
        buttonsPanel.add(typeComboBox);
        buttonsPanel.add(Box.createHorizontalStrut(5));
        addButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);

        addButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(addButton);
        buttonsPanel.add(Box.createHorizontalStrut(5));
        buttonsPanel.add(removeButton);

        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

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
        attributesTableModel.setNameTranslation(nameTranslation);
        attributesTableModel.startEditing(attributes);
        attributesTable.setModel(attributesTableModel);
        this.updateColumns();
        nameTextField.setText("");
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
    }

    public Attributes stopEditing() {
        if (attributesTableModel != null) {
            TableCellEditor editor = attributesTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
            Attributes attributes = attributesTableModel.stopEditing();
            attributesTableModel = null;
            attributesTable.setModel(new DefaultTableModel());
            return attributes;
        } else {
            return null;
        }
    }

    private void updateColumns() {
        TableColumnModel columnModel = attributesTable.getColumnModel();
        int viewIndex = attributesTable.convertColumnIndexToView(1);
        if (showType && viewIndex == -1) {
            columnModel.addColumn(new TableColumn(1));
        } else if (!showType && viewIndex != -1) {
            TableColumn typeColumn = columnModel.getColumn(viewIndex);
            attributesTable.removeColumn(typeColumn);
        }
        TableColumn nameColumn = columnModel.getColumn(attributesTable.convertColumnIndexToView(0));
        nameColumn.setMaxWidth(showType ? 200 : 300);
        nameColumn.setPreferredWidth(showType ? 100 : 200);
        if (showType) {
            TableColumn typeColumn = columnModel.getColumn(attributesTable.convertColumnIndexToView(1));
            typeColumn.setMaxWidth(100);
            typeColumn.setPreferredWidth(100);
        }
    }

    public Attributes getAttributes() {
        return attributesTableModel != null ? attributesTableModel.getAttributes() : null;
    }

    public void setEnabledAddRemove(boolean enabled) {
        buttonsPanel.setVisible(enabled);
    }

    public void setShowType(boolean showType) {
        this.showType = showType;
        this.updateColumns();
    }

    private javax.swing.JButton addButton;
    private javax.swing.JTable attributesTable;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane scrollPane;
    private JComboBox<Type> typeComboBox;
    private boolean showType = true;

    @Override
    public ModelAttributesPM getPresentationModel() {
        return localModelProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(ModelAttributesPM pModel) {
        ModelAttributesPM oldModel = getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener("attributes", listener);
            oldModel.removePropertyChangeListener("finished", finishedListener);
        }
        localModelProvider.setPresentationModel(pModel);
        if (pModel != null) {
            pModel.addPropertyChangeListener("attributes", listener);
            pModel.addPropertyChangeListener("finished", finishedListener);
        }
    }

    @Override
    public IModelProvider getModelProvider() {
        return this.link.getModelProvider();
    }

    @Override
    public void setModelProvider(IModelProvider provider) {
        this.link.setModelProvider(provider);
    }

    @Override
    public Path getPath() {
        return this.link.getPath();
    }

    @Override
    public void setPath(Path path) {
        this.link.setPath(path);
    }
}
