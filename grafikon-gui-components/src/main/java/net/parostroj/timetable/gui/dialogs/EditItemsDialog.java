package net.parostroj.timetable.gui.dialogs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing list of items.
 *
 * @author jub
 */
abstract public class EditItemsDialog<T, E> extends javax.swing.JDialog {

    protected E element;
    private WrapperListModel<T> listModel;

    private final boolean move;
    private final boolean edit;
    private boolean newByName;

    public EditItemsDialog(java.awt.Window parent, boolean modal) {
        this(parent, modal, true, false, true);
    }

    public EditItemsDialog(java.awt.Window parent, boolean modal, boolean move, boolean edit, boolean newByName) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        this.move = move;
        this.edit = edit;
        this.newByName = newByName;
        initComponents();
    }

    public void showDialog(E element) {
        this.element = element;
        this.updateValues();
        this.setVisible(true);
    }

    abstract protected Collection<T> getList();

    abstract protected void add(T item, int index);

    abstract protected void remove(T item);

    abstract protected void move(T item, int oldIndex, int newIndex);

    abstract protected boolean deleteAllowed(T item);

    abstract protected T createNew(String name);

    protected void refresh(T item) {
        listModel.refreshObject(item);
    }

    protected void refreshAll() {
        listModel.refreshAll();
    }

    public void updateValues() {
        // update list of available classes ...
        listModel = new WrapperListModel<T>(this.createWrapperList(getList()), null, !move);
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

    public void setMultipleSelection(boolean multiple) {
        itemList.setSelectionMode(multiple ? javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                : javax.swing.ListSelectionModel.SINGLE_SELECTION);
    }

    public void updateEnabled() {
        boolean enabled = !itemList.isSelectionEmpty();
        boolean multiple = enabled && itemList.getSelectedIndices().length > 1;
        if (upButton != null) upButton.setEnabled(enabled && !multiple);
        if (downButton != null) downButton.setEnabled(enabled && !multiple);
        deleteButton.setEnabled(enabled);
        if (editButton != null) editButton.setEnabled(enabled && !multiple);
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList<Wrapper<T>>();
        if (newByName) {
            nameTextField = new javax.swing.JTextField();
            nameTextField.setColumns(6);
            nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
                @Override
                protected void change() {
                    String text = nameTextField.getText();
                    newButton.setEnabled(!ObjectsUtil.isEmpty(text));
                }
            });
        }
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        newButton.setEnabled(!newByName);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        if (move) {
            upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 2);
            downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 2);
            upButton.addActionListener(evt -> upButtonActionPerformed(evt));
            downButton.addActionListener(evt -> downButtonActionPerformed(evt));
        }
        if (edit) {
            editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
            editButton.addActionListener( evt -> {
                edit(itemList.getSelectedValue().getElement());
            });
            itemList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && !itemList.isSelectionEmpty()
                            && itemList.getSelectedIndices().length == 1) {
                        edit(itemList.getSelectedValue().getElement());
                    }
                }
            });
        }

        itemList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                itemListValueChanged(evt);
            }
        });
        scrollPane.setViewportView(itemList);

        newButton.addActionListener(evt -> newButtonActionPerformed(evt));
        deleteButton.addActionListener(evt -> deleteButtonActionPerformed(evt));

        javax.swing.JPanel panel = new javax.swing.JPanel();
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        panel.setLayout(layout);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        ParallelGroup horizontal = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
            .addComponent(newButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        if (move) {
            horizontal.addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        if (edit) {
            horizontal.addComponent(editButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        if (newByName) {
            horizontal.addComponent(nameTextField);
        }
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(horizontal)
                .addContainerGap())
        );
        SequentialGroup vertical = layout.createSequentialGroup();
        if (newByName) {
            vertical.addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);
        }
        vertical.addComponent(newButton)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(deleteButton);
        if (move) {
            vertical.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton);
        }
        if (edit) {
            vertical.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton);
        }
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, vertical))
                .addContainerGap())
        );

        pack();
    }

    protected void edit(T item) {
        throw new IllegalStateException("Edit action not implemented");
    }

    protected Wrapper<T> createWrapper(T item) {
        return Wrapper.getWrapper(item);
    }

    private List<Wrapper<T>> createWrapperList(Iterable<? extends T> items) {
        List<Wrapper<T>> result = new ArrayList<>();
        for (T item : items) {
            result.add(this.createWrapper(item));
        }
        return result;
    }

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {
        T item = null;
        if (newByName) {
            String name = ObjectsUtil.checkAndTrim(nameTextField.getText());
            if (name != null) {
                // create new item
                item = this.createNew(name);
                nameTextField.setText("");
            }
        } else {
            item = this.createNew(null);
        }
        if (item != null) {
            listModel.addWrapper(createWrapper(item));
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
    private javax.swing.JButton editButton;
}
