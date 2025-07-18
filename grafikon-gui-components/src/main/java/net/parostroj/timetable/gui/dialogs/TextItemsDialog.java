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
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for text items.
 *
 * @author jub
 */
public class TextItemsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private TrainDiagram diagram;
    private final WrapperListModel<TextItem> itemsModel;
    private TextItem selectedItem;

    /** Creates new form TextItemsDialog */
    public TextItemsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // create model and set it
        itemsModel = new WrapperListModel<>(false);
        itemsModel.setObjectListener(new WrapperListModel.ObjectListener<>() {
            @Override
            public void added(TextItem object, int index) {
                diagram.getTextItems().add(index, object);
            }

            @Override
            public void removed(TextItem object) {
                diagram.getTextItems().remove(object);
            }

            @Override
            public void moved(TextItem object, int fromIndex, int toIndex) {
                diagram.getTextItems().move(fromIndex, toIndex);
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
        createButton.setEnabled(!ObjectsUtil.isEmpty(nameTextField.getText()));
    }

    private void initComponents() {
        javax.swing.JPanel textPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane listScrollPane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList<>();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel handlePanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(6);
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
        itemList.addListSelectionListener(this::itemListValueChanged);
        listScrollPane.setViewportView(itemList);

        textPanel.add(listScrollPane, BorderLayout.CENTER);

        getContentPane().add(textPanel, java.awt.BorderLayout.CENTER);

        controlPanel.setLayout(new java.awt.BorderLayout());

        handlePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        handlePanel.setLayout(new java.awt.GridLayout(6, 0, 0, 3));

        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                updateButtons();
            }
        });
        handlePanel.add(nameTextField);

        createButton.addActionListener(this::createButtonActionPerformed);
        handlePanel.add(createButton);

        deleteButton.addActionListener(this::deleteButtonActionPerformed);
        handlePanel.add(deleteButton);

        editButton.addActionListener(this::editButtonActionPerformed);
        handlePanel.add(editButton);

        upButton.addActionListener(this::upButtonActionPerformed);
        handlePanel.add(upButton);

        downButton.addActionListener(this::downButtonActionPerformed);
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
        dialog.showDialog(diagram, new TextItemModel(selectedItem.getTemplate()));
        TextItemModel newItemModel = dialog.getResultModel();
        if (newItemModel != null) {
            selectedItem.setTemplate(newItemModel.template);
        }
    }

    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList<Wrapper<TextItem>> itemList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton upButton;
}
