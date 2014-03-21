package net.parostroj.timetable.gui.dialogs;

import java.awt.event.*;

import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TrainDiagram;
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
    private boolean changed;

    /** Creates new form TextItemsDialog */
    public TextItemsDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // type values ...
        typeComboBox.addItem("bbcode");
        typeComboBox.addItem("html");
        // create model and set it
        itemsModel = new WrapperListModel<TextItem>(false);
        itemList.setModel(itemsModel);
        textArea.setTabsEmulated(true);
    }

    @Override
    public void setVisible(boolean b) {
        if (b)
            updateButtons();
        super.setVisible(b);
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.fillList();
        this.setVisible(true);
    }

    private void fillList() {
        for (TextItem item : diagram.getTextItems()) {
            itemsModel.addWrapper(new Wrapper<TextItem>(item));
        }
    }

    private void updateButtons() {
        boolean selected = itemList.getSelectedValue() != null;
        downButton.setEnabled(selected);
        upButton.setEnabled(selected);
        deleteButton.setEnabled(selected);
        textArea.setEnabled(selected);
        // create button
        createButton.setEnabled(!"".equals(nameTextField.getText().trim()));
    }

    private void writeChangedValueBack() {
        if (selectedItem != null && changed) {
            selectedItem.setText(textArea.getText());
            changed = false;
        }
    }

    private void initComponents() {
        javax.swing.JPanel textPanel = new javax.swing.JPanel();
        org.fife.ui.rtextarea.RTextScrollPane scrollPane = new org.fife.ui.rtextarea.RTextScrollPane();
        textArea = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();
        javax.swing.JScrollPane listScrollPane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList();
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        javax.swing.JPanel handlePanel = new javax.swing.JPanel();
        nameTextField = new javax.swing.JTextField();
        typeComboBox = new javax.swing.JComboBox();
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 0);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 0);
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 0);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 0);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeActionPerformed();
            }
        });

        textPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 0));
        textPanel.setLayout(new java.awt.BorderLayout(5, 0));

        scrollPane.setLineNumbersEnabled(false);

        textArea.setColumns(60);
        textArea.setRows(25);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        textArea.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                changed = true;
            }
        });
        scrollPane.setViewportView(textArea);

        textPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        itemList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemList.setPrototypeCellValue("mmmmmmmmmmmm");
        itemList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                itemListValueChanged(evt);
            }
        });
        listScrollPane.setViewportView(itemList);

        textPanel.add(listScrollPane, java.awt.BorderLayout.LINE_END);

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

        getContentPane().add(controlPanel, java.awt.BorderLayout.LINE_END);

        pack();
    }

    private void closeActionPerformed() {
        writeChangedValueBack();
        this.setVisible(false);
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TextItem item = new TextItem(IdGenerator.getInstance().getId(), diagram);
        item.setName(nameTextField.getText().trim());
        item.setType((String)typeComboBox.getSelectedItem());
        item.setText("");
        diagram.addTextItem(item);
        Wrapper<TextItem> wrapper = new Wrapper<TextItem>(item);
        itemsModel.addWrapper(wrapper);
        nameTextField.setText("");
        itemList.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void itemListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            writeChangedValueBack();
            Wrapper<?> wrapper = (Wrapper<?>) itemList.getSelectedValue();
            if (wrapper != null) {
                selectedItem = (TextItem) wrapper.getElement();
                textArea.setText(selectedItem.getText());
                textArea.setSyntaxEditingStyle(selectedItem.getType().equals("bbcode") ?
                        SyntaxConstants.SYNTAX_STYLE_BBCODE :
                        SyntaxConstants.SYNTAX_STYLE_HTML);
                textArea.setCaretPosition(0);
            } else {
                textArea.setText("");
                selectedItem = null;
            }
            this.changed = false;
        }
        this.updateButtons();
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Wrapper<?> wrapper = (Wrapper<?>) itemList.getSelectedValue();
        if (wrapper != null) {
            itemsModel.removeObject((TextItem) wrapper.getElement());
            diagram.removeTextItem((TextItem) wrapper.getElement());
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = itemList.getSelectedIndex();
        if (index != -1 && index != 0) {
            Wrapper<TextItem> wrapper = itemsModel.removeIndex(index);
            diagram.moveTextItem(index, index - 1);
            index--;
            itemsModel.addWrapper(wrapper, index);
            itemList.setSelectedIndex(index);
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int index = itemList.getSelectedIndex();
        if (index != -1 && index != (itemsModel.getSize() - 1)) {
            Wrapper<TextItem> wrapper = itemsModel.removeIndex(index);
            diagram.moveTextItem(index, index + 1);
            index++;
            itemsModel.addWrapper(wrapper, index);
            itemList.setSelectedIndex(index);
        }
    }

    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList itemList;
    private javax.swing.JTextField nameTextField;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea textArea;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JButton upButton;
}
