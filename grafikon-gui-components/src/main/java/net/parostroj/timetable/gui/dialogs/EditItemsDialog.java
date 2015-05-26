package net.parostroj.timetable.gui.dialogs;

import java.util.Collection;

import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing list of items.
 *
 * @author jub
 */
abstract public class EditItemsDialog<T> extends javax.swing.JDialog {

    protected TrainDiagram diagram;
    private WrapperListModel<T> listModel;

    private final boolean move;
    private final boolean edit;

    public EditItemsDialog(java.awt.Window parent, boolean modal) {
        this(parent, modal, true, false);
    }

    public EditItemsDialog(java.awt.Window parent, boolean modal, boolean move, boolean edit) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        this.move = move;
        this.edit = edit;
        initComponents();
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.updateValues();
        this.setVisible(true);
    }

    abstract protected Collection<T> getList();

    abstract protected void add(T item, int index);

    abstract protected void remove(T item);

    abstract protected void move(T item, int oldIndex, int newIndex);

    abstract protected boolean deleteAllowed(T item);

    abstract protected T createNew(String name);

    public void updateValues() {
        // update list of available classes ...
        listModel = new WrapperListModel<T>(Wrapper.getWrapperList(getList()), null, !move);
        listModel.setObjectListener(new ObjectListener<T>() {
            @Override
            public void added(T object, int index) {
                add(object, index);
            }

            @Override
            public void removed(T object) {
                remove(object);
            }

            @Override
            public void moved(T object, int fromIndex, int toIndex) {
                move(object, fromIndex, toIndex);
            }});
        itemList.setModel(listModel);
        this.updateEnabled();
    }

    public void updateEnabled() {
        boolean enabled = !itemList.isSelectionEmpty();
        if (upButton != null) upButton.setEnabled(enabled);
        if (downButton != null) downButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList<Wrapper<T>>();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(6);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                String text = nameTextField.getText();
                newButton.setEnabled(text != null && !"".equals(text.trim()));
            }
        });
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(false);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        if (move) {
            upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 2);
            downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 2);
            upButton.addActionListener(evt -> upButtonActionPerformed(evt));
            downButton.addActionListener(evt -> downButtonActionPerformed(evt));
        }

        itemList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                itemListValueChanged(evt);
            }
        });
        scrollPane.setViewportView(itemList);

        newButton.addActionListener(evt -> newButtonActionPerformed(evt));
        deleteButton.addActionListener(evt -> deleteButtonActionPerformed(evt));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        ParallelGroup horizontal = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addComponent(newButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        if (move) {
            horizontal.addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        horizontal.addComponent(nameTextField);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(horizontal)
                .addContainerGap())
        );
        SequentialGroup vertical = layout.createSequentialGroup()
            .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(newButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(deleteButton);
        if (move) {
            vertical.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton);
        }
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, vertical))
                .addContainerGap())
        );

        pack();
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String name = ObjectsUtil.checkAndTrim(nameTextField.getText());
        if (name != null) {
            // create new item
            T item = this.createNew(name);
            listModel.addWrapper(Wrapper.getWrapper(item));
            nameTextField.setText("");
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (!itemList.isSelectionEmpty()) {
            int selected = itemList.getSelectedIndex();
            if (!this.deleteAllowed(listModel.getIndex(selected).getElement())) {
                GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.delete.in.use"), this);
                return;
            }
            listModel.removeIndex(selected);
            if (selected >= listModel.getSize()) {
                selected--;
            }
            itemList.setSelectedIndex(selected);
        }
    }

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // move selected item class up
        if (!itemList.isSelectionEmpty()) {
            int selected = itemList.getSelectedIndex();
            selected -= 1;
            if (selected < 0)
                return;
            listModel.moveIndexUp(selected + 1);
            itemList.setSelectedIndex(selected);
        }
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // move selected item class down
        if (!itemList.isSelectionEmpty()) {
            int selected = itemList.getSelectedIndex();
            selected += 1;
            if (selected >= listModel.getSize())
                return;
            listModel.moveIndexDown(selected - 1);
            itemList.setSelectedIndex(selected);
        }
    }

    private void itemListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting())
            this.updateEnabled();
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList<Wrapper<T>> itemList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton upButton;
}
