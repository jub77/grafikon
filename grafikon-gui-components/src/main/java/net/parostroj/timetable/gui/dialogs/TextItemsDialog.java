package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.dialogs.TextItemDialog.TextItemModel;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * Dialog for text items.
 *
 * @author jub
 */
public class TextItemsDialog extends javax.swing.JDialog {

    private TrainDiagram diagram;
    private final WrapperListModel<TextItem> itemsModel;
    private TextItem selectedItem;

    /** Creates new form TextItemsDialog */
    public TextItemsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // type values ...
        for (TextItem.Type type : TextItem.Type.values()) {
            typeComboBox.addItem(type);
        }
        // create model and set it
        itemsModel = new WrapperListModel<TextItem>(false);
        itemsModel.setObjectListener(new WrapperListModel.ObjectListener<TextItem>() {
            @Override
            public void added(TextItem object, int index) {
                diagram.addTextItem(object, index);
            }

            @Override
            public void removed(TextItem object) {
                diagram.removeTextItem(object);
            }

            @Override
            public void moved(TextItem object, int fromIndex, int toIndex) {
                diagram.moveTextItem(fromIndex, toIndex);
            }
        });
        itemList.setModel(itemsModel);

        pack();
        setMinimumSize(getSize());
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateButtons();
        }
        super.setVisible(visible);
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        itemsModel.setListOfWrappers(Wrapper.getWrapperList(diagram.getTextItems()));
        this.setVisible(true);
    }

    private void updateButtons() {
        boolean selected = itemList.getSelectedValue() != null;
        downButton.setEnabled(selected);
        upButton.setEnabled(selected);
        deleteButton.setEnabled(selected);
        editButton.setEnabled(selected);
        // create button
        createButton.setEnabled(!"".equals(nameTextField.getText().trim()));
    }

    private void initComponents() {
        javax.swing.JPanel textPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane listScrollPane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList<Wrapper<TextItem>>();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel handlePanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox<TextItem.Type>();
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 0);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 0);
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 0);
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 0);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 0);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeActionPerformed();
            }
        });

        textPanel.setBorder(new EmptyBorder(5, 5, 5, 0));
        textPanel.setLayout(new java.awt.BorderLayout(5, 0));

        itemList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemList.setPrototypeCellValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmmmm"));
        itemList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                itemListValueChanged(evt);
            }
        });
        listScrollPane.setViewportView(itemList);

        textPanel.add(listScrollPane, BorderLayout.CENTER);

        getContentPane().add(textPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.BorderLayout());

        handlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        handlePanel.setLayout(new java.awt.GridLayout(7, 0, 0, 3));

        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                updateButtons();
            }
        });
        handlePanel.add(nameTextField);
        handlePanel.add(typeComboBox);

        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        handlePanel.add(createButton);

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        handlePanel.add(deleteButton);

        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        handlePanel.add(editButton);

        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        handlePanel.add(upButton);

        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });
        handlePanel.add(downButton);

        controlPanel.add(handlePanel, java.awt.BorderLayout.NORTH);

        getContentPane().add(controlPanel, BorderLayout.EAST);
    }

    private void closeActionPerformed() {
        this.setVisible(false);
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TextItem item = new TextItem(IdGenerator.getInstance().getId(), diagram);
        item.setName(nameTextField.getText().trim());
        item.setType((TextItem.Type) typeComboBox.getSelectedItem());
        item.setTemplate(null);
        Wrapper<TextItem> wrapper = Wrapper.getWrapper(item);
        itemsModel.addWrapper(wrapper);
        nameTextField.setText("");
        itemList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void itemListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            int index = itemList.getSelectedIndex();
            if (index != -1) {
                selectedItem = itemsModel.getIndex(index).getElement();
            } else {
                selectedItem = null;
            }
        }
        this.updateButtons();
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = itemList.getSelectedIndex();
        if (index != -1) {
            itemsModel.removeIndex(index);
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = itemList.getSelectedIndex();
        if (index != -1 && index != 0) {
            itemsModel.moveIndexUp(index);
            itemList.setSelectedIndex(index - 1);
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = itemList.getSelectedIndex();
        if (index != -1 && index != (itemsModel.getSize() - 1)) {
            itemsModel.moveIndexDown(index);
            itemList.setSelectedIndex(index + 1);
        }
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TextItemDialog dialog = new TextItemDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(new TextItemModel(selectedItem.getTemplate(), selectedItem.getAttributes().getBool(TextItem.ATTR_TRAIN_TIMETABLE_INFO)));
        TextItemModel newItemModel = dialog.getModel();
        if (newItemModel != null) {
            selectedItem.setTemplate(newItemModel.template);
            selectedItem.getAttributes().setBool(TextItemAttributes.ATTR_TRAIN_TIMETABLE_INFO, newItemModel.trainTimetableInfo);
        }
    }

    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList<Wrapper<TextItem>> itemList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<TextItem.Type> typeComboBox;
    private javax.swing.JButton upButton;
}
