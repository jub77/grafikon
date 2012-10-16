/*
 * TrainListView.java
 *
 * Created on 24. srpen 2007, 12:35
 */
package net.parostroj.timetable.gui.views;

import java.awt.Frame;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.ApplicationModelEvent;
import net.parostroj.timetable.gui.ApplicationModelEventType;
import net.parostroj.timetable.gui.ApplicationModelListener;
import net.parostroj.timetable.gui.dialogs.CreateTrainDialog;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.ResourceLoader;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.border.EmptyBorder;

/**
 * View with list of trains.
 *
 * @author jub
 */
public class TrainListView extends javax.swing.JPanel implements ApplicationModelListener, TreeSelectionListener {

    private ApplicationModel model;

    public static enum TreeType {
        FLAT, TYPES
    }

    private TreeType treeType = TreeType.TYPES;
    private boolean selecting = false;

    /**
     * Creates new form TrainListView.
     */
    public TrainListView() {
        setLayout(new BorderLayout(0, 0));

        treePopupMenu = new javax.swing.JPopupMenu();
        typesMenuItem = new javax.swing.JMenuItem();
        flatMenuItem = new javax.swing.JMenuItem();

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

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 0, 0));
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
        menuButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treePopupMenu.show(menuButton, 3, 3);
            }
        });
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

    public void setModel(ApplicationModel model) {
        this.model = model;
        this.model.addListener(this);
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
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        switch (event.getType()) {
        case SET_DIAGRAM_CHANGED:
            this.updateViewDiagramChanged();
            break;
        case NEW_TRAIN:
            this.addAndSelectTrain((Train) event.getObject());
            break;
        case DELETE_TRAIN:
            this.deleteAndDeselectTrain((Train) event.getObject());
            break;
        case SELECTED_TRAIN_CHANGED:
            if (!selecting) {
                this.selectTrain((Train) event.getObject());
            }
            break;
        case MODIFIED_TRAIN_NAME_TYPE:
            this.modifyAndSelectTrain((Train) event.getObject());
            break;
        case TRAIN_TYPES_CHANGED:
            this.updateViewDiagramChanged();
            break;
        default:
            // do nothing
            break;
        }
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
    private final javax.swing.JMenuItem flatMenuItem;
    private final javax.swing.JScrollPane scrollPane;
    private final javax.swing.JTree trainTree;
    private final javax.swing.JPopupMenu treePopupMenu;
    private final javax.swing.JMenuItem typesMenuItem;
    private final javax.swing.JMenu groupsMenu;
    private final javax.swing.JButton menuButton;
}
