package net.parostroj.timetable.gui.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.gui.wrappers.WrapperListModel.ObjectListener;
import net.parostroj.timetable.utils.ObjectsUtil;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

/**
 * Dialog for editing list of items.
 *
 * @author jub
 */
public abstract class EditItemsDialog<T, E> extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	protected E element;
    protected WrapperListModel<T> listModel;

    private final boolean move;
    private final boolean edit;
    private final boolean newByName;
    private final boolean copy;
    private final boolean multiple;

    protected EditItemsDialog(java.awt.Window parent, boolean modal,
            boolean move,
            boolean edit,
            boolean newByName,
            boolean copy,
            boolean multiple) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        this.move = move;
        this.edit = edit;
        this.newByName = newByName;
        this.copy = copy;
        this.multiple = multiple;
        initComponents();
    }

    public void showDialog(E element) {
        this.element = element;
        this.updateValues();
        this.setVisible(true);
    }

    protected abstract Collection<T> getList();

    protected abstract void add(T item, int index);

    protected abstract void remove(T item);

    protected abstract void move(T item, int oldIndex, int newIndex);

    protected abstract boolean deleteAllowed(T item);

    protected abstract T createNew(String name);

    protected void refresh(T item) {
        listModel.refreshObject(item);
    }

    protected void refreshAll() {
        Wrapper<T> selectedValue = itemList.getSelectedValue();
        listModel.refreshAll();
        itemList.setSelectedValue(selectedValue, true);
    }

    public void updateValues() {
        // update list of available classes ...
        listModel = new WrapperListModel<>(this.createWrapperList(getList()), null, !move);
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
            }
        });
        itemList.setModel(listModel);
        this.updateEnabled();
        this.itemList.requestFocusInWindow();
    }

    public void updateEnabled() {
        boolean enabled = !itemList.isSelectionEmpty();
        int selectedItemsCount = itemList.getSelectedIndices().length;
        boolean isMultiple = enabled && selectedItemsCount > 1;
        if (upButton != null)
            upButton.setEnabled(enabled && !isMultiple);
        if (downButton != null)
            downButton.setEnabled(enabled && !isMultiple);
        deleteButton.setEnabled(enabled);
        if (editButton != null)
            editButton.setEnabled(enabled && !isMultiple);
        this.selectionChanged(selectedItemsCount);
        this.updateCopyButton();
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        itemList = new javax.swing.JList<>();
        itemList.setVisibleRowCount(12);
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(6);
        nameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                updateNewButton();
                updateCopyButton();
            }
        });
        newButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        copyButton = GuiComponentUtils.createButton(GuiIcon.COPY, 2);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        upButton = GuiComponentUtils.createButton(GuiIcon.GO_UP, 2);
        downButton = GuiComponentUtils.createButton(GuiIcon.GO_DOWN, 2);
        upButton.addActionListener(this::upButtonActionPerformed);
        downButton.addActionListener(this::downButtonActionPerformed);
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        editButton.addActionListener(evt -> edit(itemList.getSelectedValue().getElement()));
        itemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (edit && e.getClickCount() == 2 && !itemList.isSelectionEmpty()
                        && itemList.getSelectedIndices().length == 1) {
                    edit(itemList.getSelectedValue().getElement());
                }
            }
        });
        itemList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (edit && e.getKeyCode() == KeyEvent.VK_ENTER && itemList.getSelectedIndices().length == 1) {
                    edit(itemList.getSelectedValue().getElement());
                }
            }
        });

        itemList.setSelectionMode(multiple ? javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                : javax.swing.ListSelectionModel.SINGLE_SELECTION);
        itemList.addListSelectionListener(this::itemListValueChanged);
        scrollPane.setViewportView(itemList);

        newButton.addActionListener(this::newButtonActionPerformed);
        copyButton.addActionListener(this::copyButtonActionPerformed);
        deleteButton.addActionListener(this::deleteButtonActionPerformed);

        Component box = Box.createRigidArea(new Dimension(0, 0));

        javax.swing.JPanel panel = new javax.swing.JPanel();
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(panel);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(scrollPane)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(nameTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(newButton)
                        .addComponent(copyButton)
                        .addComponent(deleteButton)
                        .addComponent(upButton)
                        .addComponent(downButton)
                        .addComponent(editButton)
                        .addComponent(box))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(newButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(copyButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(deleteButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(upButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(downButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(editButton)
                            .addComponent(box, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
        );
        layout.linkSize(SwingConstants.HORIZONTAL, nameTextField, newButton, copyButton, deleteButton, upButton, downButton, editButton);
        panel.setLayout(layout);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(panel, java.awt.BorderLayout.CENTER);

        // update visual components
        upButton.setVisible(move);
        downButton.setVisible(move);
        editButton.setVisible(edit);
        nameTextField.setVisible(newByName);
        copyButton.setVisible(copy);
        this.updateNewButton();

        pack();
    }

    protected void edit(T item) {
        throw new IllegalStateException("Edit action not implemented");
    }

    protected T copy(String name, T item) {
        throw new IllegalStateException("Copy action not implemented");
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
        createNewImpl(this::createNew);
    }

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {
        T itemToCopy = listModel.getElementAt(itemList.getSelectedIndex()).getElement();
        createNewImpl(name -> this.copy(name, itemToCopy));
    }

    private void createNewImpl(Function<String, T> createFunction) {
        T item = null;
        if (newByName) {
            String name = ObjectsUtil.checkAndTrim(nameTextField.getText());
            if (name != null) {
                item = createFunction.apply(name);
                nameTextField.setText("");
            }
        } else {
            item = createFunction.apply(null);
        }
        if (item != null) {
            Wrapper<T> newWrapper = createWrapper(item);
            listModel.addWrapper(newWrapper);
            itemList.setSelectedValue(newWrapper, true);
        }
        itemList.requestFocus();
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int[] indices = itemList.getSelectedIndices();
        if (indices.length > 0) {
            for (int index : indices) {
                if (!this.deleteAllowed(listModel.getIndex(index).getElement())) {
                    GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.delete.in.use"), this);
                    return;
                }
            }
            int cnt = 0;
            for (int index : indices) {
                listModel.removeIndex(index - (cnt++));
            }
            int index = indices[0];
            if (index > listModel.getSize() - 1) {
                itemList.setSelectedIndex(listModel.getSize() - 1);
            } else {
                itemList.setSelectedIndex(index);
            }
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

    private void updateNewButton() {
        if (newByName) {
            String text = nameTextField.getText();
            newButton.setEnabled(!ObjectsUtil.isEmpty(text));
        } else {
            newButton.setEnabled(true);
        }
    }

    private void updateCopyButton() {
        boolean selectedOne = itemList.getSelectedIndices().length == 1;
        if (newByName) {
            String text = nameTextField.getText();
            copyButton.setEnabled(selectedOne && !ObjectsUtil.isEmpty(text));
        } else {
            copyButton.setEnabled(selectedOne);
        }
    }

    protected Collection<T> getSelectedItems() {
        List<T> result = new ArrayList<>();
        for (int index : itemList.getSelectedIndices()) {
            result.add(listModel.getElementAt(index).getElement());
        }
        return result;
    }

    protected int getSelectionItemsCount() {
        return itemList.getSelectedIndices().length;
    }

    protected void selectionChanged(int selectedItemsCount) {
    }

    private javax.swing.JButton deleteButton;
    private javax.swing.JButton downButton;
    private javax.swing.JList<Wrapper<T>> itemList;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton newButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JButton upButton;
    private javax.swing.JButton editButton;

    public static class Builder<E extends EditItemsDialog<X, Y>, X, Y> {

        private Constructor<E> constructor;
        private boolean copy;
        private boolean edit;
        private boolean move;
        private boolean newByName;
        private boolean multiple;

        private Builder(Constructor<E> constructor) {
            this.constructor = constructor;
        }

        public Builder<E,X,Y> setCopy(boolean copy) {
            this.copy = copy;
            return this;
        }

        public Builder<E,X,Y> setEdit(boolean edit) {
            this.edit = edit;
            return this;
        }

        public Builder<E,X,Y> setMove(boolean move) {
            this.move = move;
            return this;
        }

        public Builder<E,X,Y> setNewByName(boolean newByName) {
            this.newByName = newByName;
            return this;
        }

        public Builder<E,X,Y> setMultiple(boolean multiple) {
            this.multiple = multiple;
            return this;
        }

        public E build(java.awt.Window window, boolean modal) {
            try {
                return constructor.newInstance(window, modal, move, edit, newByName, copy, multiple);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Problem creating instance: " + constructor);
            }
        }
    }

    public static <E extends EditItemsDialog<X, Y>,X,Y> Builder<E, X, Y> newBuilder(Class<E> clazz) {
        try {
            Constructor<E> c = clazz.getConstructor(java.awt.Window.class, Boolean.TYPE,
                    Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE, Boolean.TYPE);
            return new Builder<>(c);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("Missing constructor for class: " + clazz.getName());
        }
    }
}
