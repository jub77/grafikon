/*
 * ElementSelectionPanel.java
 *
 * Created on 21.9.2009, 10:44:19
 */
package net.parostroj.timetable.gui.components;

import java.util.ArrayList;
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

    private final WrapperListModel<T> leftListModel;
    private final WrapperListModel<T> rightListModel;

    /** Creates new form ElementSelectionPanel */
    public ElementSelectionPanel() {
        leftListModel = new WrapperListModel<T>();
        rightListModel = new WrapperListModel<T>();
        initComponents();
    }

    public void setListForSelection(List<Wrapper<T>> list) {
        leftListModel.setListOfWrappers(list);
        rightListModel.setListOfWrappers(new ArrayList<Wrapper<T>>());
    }

    public void addSelected(List<Wrapper<T>> selectedList) {
        for (Wrapper<T> wrapper : selectedList) {
            addSelected(wrapper);
        }
    }

    public void addSelected(Wrapper<T> wrapper) {
        leftListModel.removeWrapper(wrapper);
        rightListModel.addWrapper(wrapper);
    }

    public List<Wrapper<T>> getSelectedList() {
        return rightListModel.getListOfWrappers();
    }

    public void clear() {
        leftListModel.clear();
        rightListModel.clear();
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        leftList = new javax.swing.JList();
        moveRightButton = GuiComponentUtils.createButton(GuiIcon.DARROW_RIGHT, 2);
        moveLeftButton = GuiComponentUtils.createButton(GuiIcon.DARROW_LEFT, 2);
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        rightList = new javax.swing.JList();

        scrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        leftList.setModel(leftListModel);
        leftList.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmm");
        scrollPane1.setViewportView(leftList);

        moveRightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveRightButtonActionPerformed(evt);
            }
        });

        moveLeftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLeftButtonActionPerformed(evt);
            }
        });

        scrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        rightList.setModel(rightListModel);
        rightList.setPrototypeCellValue("mmmmmmmmmmmmmmmmmmmm");
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
        this.setLayout(layout);
    }

    private void moveLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get selected elements and move them
        int[] values = rightList.getSelectedIndices();
        List<Wrapper<T>> toBeRemoved = new LinkedList<Wrapper<T>>();
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
        List<Wrapper<T>> toBeRemoved = new LinkedList<Wrapper<T>>();
        for (int ind : values) {
            Wrapper<T> wrapper = leftListModel.getIndex(ind);
            toBeRemoved.add(wrapper);
            rightListModel.addWrapper(wrapper);
        }
        for (Wrapper<T> w : toBeRemoved) {
            leftListModel.removeWrapper(w);
        }
    }

    private javax.swing.JList leftList;
    private javax.swing.JButton moveLeftButton;
    private javax.swing.JButton moveRightButton;
    private javax.swing.JList rightList;
}
