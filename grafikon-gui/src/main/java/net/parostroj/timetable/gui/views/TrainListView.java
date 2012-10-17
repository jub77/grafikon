/*
 * TrainListView.java
 *
 * Created on 24. srpen 2007, 12:35
 */
package net.parostroj.timetable.gui.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.GroupSelect;
import net.parostroj.timetable.gui.dialogs.CreateTrainDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * View with list of trains.
 *
 * @author jub
 */
public class TrainListView extends javax.swing.JPanel implements TreeSelectionListener {

    private ApplicationModel model;
    private ButtonGroup groupsBG;
    private final ItemListener groupL;
    private GroupSelect groupSelect;
    private final MenuAdapter menuAdapter;

    public static enum TreeType {
        FLAT, TYPES
    }

    private static class GroupMenuItem extends javax.swing.JRadioButtonMenuItem {

        private final GroupSelect groupSelect;

        public GroupMenuItem(String text, GroupSelect groupSelect) {
            super(text);
            this.groupSelect = groupSelect;
        }

        public GroupSelect getGroupSelect() {
            return groupSelect;
        }
    }

    private class MenuAdapter extends MouseAdapter implements ActionListener {

        private int x = -1, y = -1;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (x == -1) {
                x = 3;
                y = 3;
            }
            treePopupMenu.show(menuButton, x, y);
            x = -1;
            y = -1;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            x = e.getX();
            y = e.getY();
        }
    }

    private TreeType treeType = TreeType.TYPES;
    private boolean selecting = false;

    /**
     * Creates new form TrainListView.
     */
    public TrainListView() {
        setLayout(new BorderLayout(0, 0));

        treePopupMenu = new javax.swing.JPopupMenu();
        typesMenuItem = new javax.swing.JRadioButtonMenuItem();
        flatMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.ButtonGroup typeButtonGroup = new ButtonGroup();
        typeButtonGroup.add(typesMenuItem);
        typeButtonGroup.add(flatMenuItem);
        typesMenuItem.setSelected(true);

        typesMenuItem.setText(ResourceLoader.getString("trainlist.tree.types")); // NOI18N
        typesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeTypeActionPerformed(evt);
            }
        });
        treePopupMenu.add(typesMenuItem);

        flatMenuItem.setText(ResourceLoader.getString("trainlist.tree.flat")); // NOI18N
        flatMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeTypeActionPerformed(evt);
            }
        });
        treePopupMenu.add(flatMenuItem);

        groupsMenu = new javax.swing.JMenu(ResourceLoader.getString("menu.groups")); // NOI18N
        treePopupMenu.add(groupsMenu);

        groupL = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    groupSelect = ((GroupMenuItem) e.getItem()).getGroupSelect();
                    // TODO handle selection of group ...
                }
            }
        };

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 0, 5));
        add(panel, BorderLayout.CENTER);

        trainTree = new javax.swing.JTree();
        trainTree.setModel(null);
        trainTree.addTreeSelectionListener(this);
        trainTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        panel.setLayout(new BorderLayout(0, 0));
        scrollPane = new javax.swing.JScrollPane();
        panel.add(scrollPane);

        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        trainTree.setComponentPopupMenu(treePopupMenu);
        trainTree.setModel(null);
        scrollPane.setViewportView(trainTree);

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        createButton = new javax.swing.JButton();
        buttonPanel.add(createButton);

        createButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        deleteButton = new javax.swing.JButton();
        buttonPanel.add(deleteButton);

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N

        menuButton = new javax.swing.JButton("v");
        buttonPanel.add(menuButton);
        menuAdapter = new MenuAdapter();
        menuButton.addActionListener(menuAdapter);
        menuButton.addMouseListener(menuAdapter);

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
    }

    private void updateGroupsMenu(boolean added, Group group) {
        if (added) {
            addToGroupsMenu(new GroupMenuItem(group.getName(), new GroupSelect(GroupSelect.Type.GROUP, group)), null);
        } else {
            GroupMenuItem item = findByGroup(group);
            if (item.isSelected())
                groupsMenu.getItem(0).setSelected(true);
            removeFromGroupsMenu(item);
        }
    }

    private GroupMenuItem findByGroup(Group group) {
        for (MenuElement e : groupsMenu.getPopupMenu().getSubElements()) {
            if (e instanceof GroupMenuItem) {
                GroupMenuItem item = (GroupMenuItem) e;
                if (group.equals(item.getGroupSelect().getGroup()))
                    return item;
            }
        }
        return null;
    }

    private void addToGroupsMenu(JMenuItem item, Integer position) {
        if (position == null)
            groupsMenu.add(item);
        else
            groupsMenu.insert(item, position);
        groupsBG.add(item);
        item.addItemListener(groupL);
    }

    private void removeFromGroupsMenu(JMenuItem item) {
        item.removeItemListener(groupL);
        groupsBG.remove(item);
        groupsMenu.remove(item);
    }

    private void buildGroupsMenu() {
        groupsMenu.removeAll();
        groupsBG = new ButtonGroup();

        // all and none
        addToGroupsMenu(new GroupMenuItem("<" + ResourceLoader.getString("groups.all") + ">", new GroupSelect(GroupSelect.Type.ALL, null)), null);
        addToGroupsMenu(new GroupMenuItem("<" + ResourceLoader.getString("groups.none") + ">", new GroupSelect(GroupSelect.Type.NONE, null)), null);

        // fill groups
        if (model != null && model.getDiagram() != null)
            for (Group group : model.getDiagram().getGroups()) {
                addToGroupsMenu(new GroupMenuItem(group.getName(), new GroupSelect(GroupSelect.Type.GROUP, group)), null);
            }

        // select first
        groupsMenu.getItem(0).setSelected(true);
    }

    public void setModel(ApplicationModel model) {
        this.model = model;
        this.model.getMediator().addColleague(new ApplicationGTEventColleague(true) {
            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SET_DIAGRAM_CHANGED:
                        updateViewDiagramChanged();
                        break;
                    case NEW_TRAIN:
                        addAndSelectTrain((Train) event.getObject());
                        break;
                    case DELETE_TRAIN:
                        deleteAndDeselectTrain((Train) event.getObject());
                        break;
                    case SELECTED_TRAIN_CHANGED:
                        if (!selecting) {
                            selectTrain((Train) event.getObject());
                        }
                        break;
                    case MODIFIED_TRAIN_NAME_TYPE:
                        modifyAndSelectTrain((Train) event.getObject());
                        break;
                    case TRAIN_TYPES_CHANGED:
                        updateViewDiagramChanged();
                        break;
                    default:
                        // do nothing
                        break;
                }
            }

            @Override
            public void processTrainDiagramEvent(TrainDiagramEvent event) {
                if (event.getType() == GTEventType.GROUP_ADDED) {
                    updateGroupsMenu(true, (Group) event.getObject());
                } else if (event.getType() == GTEventType.GROUP_REMOVED) {
                    updateGroupsMenu(false, (Group) event.getObject());
                }
            }

            @Override
            public void processTrainEvent(TrainEvent event) {
                if (event.getType() == GTEventType.ATTRIBUTE && event.getAttributeChange().getName().equals("group")) {
                    // TODO handle change of group of train
                }
            }
        });
        this.updateViewDiagramChanged();
    }

    private void updateViewDiagramChanged() {
        if (model.getDiagram() != null) {
            TrainTreeNodeRoot root = treeType == TreeType.TYPES ? new TrainTreeNodeRootImpl1(
                    ResourceLoader.getString("trainlist.trains"), model.getDiagram()) : new TrainTreeNodeRootImpl2(
                    ResourceLoader.getString("trainlist.trains"), model.getDiagram());
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            trainTree.setModel(treeModel);
            createButton.setEnabled(true);
            deleteButton.setEnabled(false);
            menuButton.setEnabled(true);
        } else {
            trainTree.setModel(null);
            createButton.setEnabled(false);
            deleteButton.setEnabled(false);
            menuButton.setEnabled(false);
        }
        buildGroupsMenu();
    }

    private void addAndSelectTrain(Train train) {
        TreePath p = ((TrainTreeNodeRoot) trainTree.getModel().getRoot()).addTrain(train);
        ((DefaultTreeModel) trainTree.getModel()).reload((TreeNode) p.getParentPath().getLastPathComponent());
        trainTree.setSelectionPath(p);
        trainTree.scrollPathToVisible(p);
    }

    private void deleteAndDeselectTrain(Train train) {
        TreePath p = ((TrainTreeNodeRoot) trainTree.getModel().getRoot()).removeTrain(train);
        ((DefaultTreeModel) trainTree.getModel()).reload((TreeNode) p.getParentPath().getLastPathComponent());
    }

    private void modifyAndSelectTrain(Train train) {
        TreePath p = ((TrainTreeNodeRoot) trainTree.getModel().getRoot()).removeTrain(train);
        ((DefaultTreeModel) trainTree.getModel()).reload((TreeNode) p.getParentPath().getLastPathComponent());
        p = ((TrainTreeNodeRoot) trainTree.getModel().getRoot()).addTrain(train);
        ((DefaultTreeModel) trainTree.getModel()).reload((TreeNode) p.getParentPath().getLastPathComponent());
        trainTree.setSelectionPath(p);
        trainTree.scrollPathToVisible(p);
    }

    private void selectTrain(Train train) {
        TreePath p = null;
        if (trainTree.getModel() != null)
            p = ((TrainTreeNodeRoot) trainTree.getModel().getRoot()).getTrainPath(train);
        trainTree.setSelectionPath(p);
        trainTree.scrollPathToVisible(p);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selecting = true;
        if (e.isAddedPath()) {
            Object selected = e.getPath().getLastPathComponent();
            if (!(selected instanceof TrainTreeNodeTrain) || trainTree.getSelectionCount() > 1) {
                if (model.getSelectedTrain() != null)
                    model.setSelectedTrain(null);
            } else {
                TrainTreeNodeTrain trainNode = (TrainTreeNodeTrain) selected;
                if (model.getSelectedTrain() != trainNode.getTrain())
                    model.setSelectedTrain(trainNode.getTrain());
            }
        }
        deleteButton.setEnabled(!trainTree.isSelectionEmpty());
        selecting = false;
    }

    public TreeType getTreeType() {
        return treeType;
    }

    public void setTreeType(TreeType treeType) {
        if (treeType != this.treeType) {
            this.treeType = treeType;
            // update list
            this.updateViewDiagramChanged();
        }
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_deleteButtonActionPerformed
        Set<Train> selectedTrains = this.getSelectedTrains();

        model.setSelectedTrain(null); // no train selected

        for (Train deletedTrain : selectedTrains) {
            this.deleteTrain(deletedTrain, model.getDiagram());
        }
    }// GEN-LAST:event_deleteButtonActionPerformed

    private Set<Train> getSelectedTrains() {
        TreePath[] paths = trainTree.getSelectionPaths();
        Set<Train> selected = new HashSet<Train>();
        if (paths != null) {
            for (TreePath path : paths) {
                TrainTreeNode node = (TrainTreeNode) path.getLastPathComponent();
                Set<Train> trains = node.getTrains(model.getDiagram());
                selected.addAll(trains);
            }
        }
        return selected;
    }

    private void deleteTrain(Train deletedTrain, TrainDiagram diagram) {
        // remove train from cycles
        for (String type : model.getDiagram().getCycleTypeNames()) {
            if (!deletedTrain.getCycles(type).isEmpty()) {
                this.removeTrainFromCycles(deletedTrain.getCycles(type));
            }
        }

        diagram.removeTrain(deletedTrain); // remove from list of trains
        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETE_TRAIN, model, deletedTrain));
    }

    private void removeTrainFromCycles(List<TrainsCycleItem> items) {
        for (TrainsCycleItem item : new LinkedList<TrainsCycleItem>(items)) {
            TrainsCycle cycle = item.getCycle();
            item.getCycle().removeItem(item);
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.DELETED_CYCLE, model, cycle));
        }
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_createButtonActionPerformed
        // call create new train dialog
        Frame f = (Frame) this.getTopLevelAncestor();

        CreateTrainDialog create = new CreateTrainDialog((Frame) this.getTopLevelAncestor(), model);
        create.updateView(groupSelect.getGroup());

        create.setLocationRelativeTo(f);
        create.setVisible(true);
    }// GEN-LAST:event_createButtonActionPerformed

    private void treeTypeActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_treeTypeActionPerformed
        if (evt.getSource() == flatMenuItem)
            treeType = TrainListView.TreeType.FLAT;
        else
            treeType = TrainListView.TreeType.TYPES;
        // update list
        this.updateViewDiagramChanged();
    }// GEN-LAST:event_treeTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton createButton;
    private final javax.swing.JButton deleteButton;
    private final javax.swing.JRadioButtonMenuItem flatMenuItem;
    private final javax.swing.JScrollPane scrollPane;
    private final javax.swing.JTree trainTree;
    private final javax.swing.JPopupMenu treePopupMenu;
    private final javax.swing.JRadioButtonMenuItem typesMenuItem;
    private final javax.swing.JMenu groupsMenu;
    private final javax.swing.JButton menuButton;
}
