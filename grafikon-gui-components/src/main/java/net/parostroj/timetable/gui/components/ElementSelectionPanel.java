/*
 * ElementSelectionPanel.java
 *
 * Created on 21.9.2009, 10:44:19
 */
package net.parostroj.timetable.gui.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Panel for elements selection.
 *
 * @author jub
 */
public class ElementSelectionPanel<T> extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final String LIST_PROTOTYPE_VALUE = "mmmmmmmmmmmmmmmmmmmmmm";
    private static final int LIST_DEFAULT_ROW_COUNT = 15;

    private final WrapperListModel<T> leftListModel;
    private final WrapperListModel<T> rightListModel;

    public ElementSelectionPanel() {
        this(true, true);
    }

    public ElementSelectionPanel(boolean leftSorted, boolean rightSorted) {
        leftListModel = new WrapperListModel<>(leftSorted);
        rightListModel = new WrapperListModel<>(rightSorted);
        initComponents();
    }

    public void setSorted(boolean leftSorted, boolean rightSorted) {
        leftListModel.setSorted(leftSorted);
        rightListModel.setSorted(rightSorted);
    }

    public WrapperListModel<T> getNotSelected() {
        return leftListModel;
    }

    public WrapperListModel<T> getSelected() {
        return rightListModel;
    }

    public void setListForSelection(Collection<Wrapper<T>> notSelected) {
        this.setListsForSelection(notSelected, null);
    }

    public void setListsForSelection(Collection<Wrapper<T>> notSelected, Collection<Wrapper<T>> selected) {
        leftListModel.setListOfWrappers(new ArrayList<>(notSelected));
        rightListModel.setListOfWrappers(selected == null ? new ArrayList<>() : new ArrayList<>(selected));
    }

    public void addSelected(Collection<Wrapper<T>> selectedList) {
        for (Wrapper<T> wrapper : selectedList) {
            addSelected(wrapper);
        }
    }

    public void addSelected(Wrapper<T> wrapper) {
        leftListModel.removeWrapper(wrapper);
        rightListModel.addWrapper(wrapper);
    }

    public List<Wrapper<T>> getSelectedList() {
        return new ArrayList<>(rightListModel.getListOfWrappers());
    }

    public List<Wrapper<T>> getNotSelectedList() {
        return new ArrayList<>(leftListModel.getListOfWrappers());
    }

    public void clear() {
        leftListModel.clear();
        rightListModel.clear();
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        leftList = new javax.swing.JList<>();
        moveRightButton = GuiComponentUtils.createButton(GuiIcon.DARROW_RIGHT, 2);
        moveLeftButton = GuiComponentUtils.createButton(GuiIcon.DARROW_LEFT, 2);
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        rightList = new javax.swing.JList<>();

        scrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        leftList.setModel(leftListModel);
        leftList.setPrototypeCellValue(Wrapper.getPrototypeWrapper(LIST_PROTOTYPE_VALUE));
        scrollPane1.setViewportView(leftList);

        moveRightButton.addActionListener(evt -> moveRightButtonActionPerformed(evt));

        moveLeftButton.addActionListener(evt -> moveLeftButtonActionPerformed(evt));

        scrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        rightList.setModel(rightListModel);
        rightList.setPrototypeCellValue(Wrapper.getPrototypeWrapper(LIST_PROTOTYPE_VALUE));
        scrollPane2.setViewportView(rightList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(moveLeftButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(moveRightButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(moveRightButton)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(moveLeftButton))
                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );

        leftList.setVisibleRowCount(LIST_DEFAULT_ROW_COUNT);
        rightList.setVisibleRowCount(LIST_DEFAULT_ROW_COUNT);

        this.setLayout(layout);
    }

    private void moveLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get selected elements and move them
        int[] values = rightList.getSelectedIndices();
        List<Wrapper<T>> toBeRemoved = new LinkedList<>();
        for (int ind : values) {
            Wrapper<T> wrapper = rightListModel.getIndex(ind);
            toBeRemoved.add(wrapper);
            leftListModel.addWrapper(wrapper);
        }
        for (Wrapper<T> w : toBeRemoved) {
            rightListModel.removeWrapper(w);
        }
    }

    private void moveRightButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get selected elements and move them
        int[] values = leftList.getSelectedIndices();
        List<Wrapper<T>> toBeRemoved = new LinkedList<>();
        for (int ind : values) {
            Wrapper<T> wrapper = leftListModel.getIndex(ind);
            toBeRemoved.add(wrapper);
            rightListModel.addWrapper(wrapper);
        }
        for (Wrapper<T> w : toBeRemoved) {
            leftListModel.removeWrapper(w);
        }
    }

    private javax.swing.JList<Wrapper<T>> leftList;
    private javax.swing.JButton moveLeftButton;
    private javax.swing.JButton moveRightButton;
    private javax.swing.JList<Wrapper<T>> rightList;
}
