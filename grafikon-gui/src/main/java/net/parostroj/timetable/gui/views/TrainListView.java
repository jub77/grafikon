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

import net.parostroj.timetable.actions.RouteBuilder;
import net.parostroj.timetable.actions.TrainBuilder;
import net.parostroj.timetable.filters.EmptyFilter;
import net.parostroj.timetable.filters.Filter;
import net.parostroj.timetable.filters.GroupFilter;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.commands.CommandException;
import net.parostroj.timetable.gui.commands.DeleteTrainCommand;
import net.parostroj.timetable.gui.components.GroupSelect;
import net.parostroj.timetable.gui.components.GroupSelect.Type;
import net.parostroj.timetable.gui.dialogs.CreateRouteDialog;
import net.parostroj.timetable.gui.dialogs.CreateTrainDialog;
import net.parostroj.timetable.gui.dialogs.GroupChooserDialog;
import net.parostroj.timetable.gui.views.tree2.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.model.events.TrainEvent;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View with list of trains.
 *
 * @author jub
 */
public class TrainListView extends javax.swing.JPanel implements TreeSelectionListener {

    private static final Logger LOG = LoggerFactory.getLogger(TrainListView.class);

    private ApplicationModel model;
    private ButtonGroup groupsBG;
    private final ItemListener groupL;
    private GroupSelect groupSelect;
    private final MenuAdapter menuAdapter;
    private TrainTreeHandler trainTreeHandler;
    private TreeType treeType = TreeType.TYPES;
    private boolean selecting = false;

    public static enum TreeType {
        FLAT, TYPES, GROUPS_AND_TYPES, GROUPS
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

    /**
     * Creates new form TrainListView.
     */
    public TrainListView() {
        setLayout(new BorderLayout(0, 0));

        treePopupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenu listMenu = new javax.swing.JMenu(ResourceLoader.getString("trainlist.tree"));
        treePopupMenu.add(listMenu);
        moveToGroupMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("trainlist.move.to.group"));
        moveToGroupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                moveToGroup();
            }
        });
        changeRouteMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("trainlist.change.route"));
        changeRouteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeRoute();
            }
        });
        treePopupMenu.add(moveToGroupMenuItem);
        treePopupMenu.add(changeRouteMenuItem);
        listTypesMenuItem = new javax.swing.JRadioButtonMenuItem();
        listFlatMenuItem = new javax.swing.JRadioButtonMenuItem();
        listGroupsMenuItem = new javax.swing.JRadioButtonMenuItem();
        listGroupsFlatMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.ButtonGroup typeButtonGroup = new ButtonGroup();
        typeButtonGroup.add(listTypesMenuItem);
        typeButtonGroup.add(listFlatMenuItem);
        typeButtonGroup.add(listGroupsMenuItem);
        typeButtonGroup.add(listGroupsFlatMenuItem);
        listTypesMenuItem.setSelected(true);

        listTypesMenuItem.setText(ResourceLoader.getString("trainlist.tree.types")); // NOI18N
        listTypesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeTypeActionPerformed(evt);
            }
        });
        listMenu.add(listTypesMenuItem);

        listFlatMenuItem.setText(ResourceLoader.getString("trainlist.tree.flat")); // NOI18N
        listFlatMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeTypeActionPerformed(evt);
            }
        });
        listMenu.add(listFlatMenuItem);

        listGroupsMenuItem.setText(ResourceLoader.getString("trainlist.tree.groups")); // NOI18N
        listGroupsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeTypeActionPerformed(evt);
            }
        });
        listMenu.add(listGroupsMenuItem);

        listGroupsFlatMenuItem.setText(ResourceLoader.getString("trainlist.tree.groups.flat")); // NOI18N
        listGroupsFlatMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeTypeActionPerformed(evt);
            }
        });
        listMenu.add(listGroupsFlatMenuItem);

        groupsMenu = new javax.swing.JMenu(ResourceLoader.getString("menu.groups")); // NOI18N
        treePopupMenu.add(groupsMenu);

        groupL = new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    groupSelect = ((GroupMenuItem) e.getItem()).getGroupSelect();
                    // selection of group regenerates tree with a new filter group
                    updateViewDiagramChanged();
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
        trainTree.setRootVisible(false);
        trainTree.setShowsRootHandles(true);
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
        this.updateViewDiagramChanged();
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
                        buildGroupsMenu();
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
                    modifyAndSelectTrain(event.getSource());
                }
            }
        });
        this.buildGroupsMenu();
        this.updateViewDiagramChanged();
    }

    private void updateViewDiagramChanged() {
        if (model.getDiagram() != null) {
            TrainTreeHandlerFactory factory = TrainTreeHandlerFactory.getInstance();
            switch (treeType) {
                case FLAT:
                    trainTreeHandler = factory.getFlatHandler(createFilter(), model.getDiagram());
                    break;
                case GROUPS_AND_TYPES:
                    trainTreeHandler = factory.getGroupsAndTypesHandler(model.getDiagram());
                    break;
                case TYPES:
                    trainTreeHandler = factory.getTypesHandler(createFilter(), model.getDiagram());
                    break;
                case GROUPS:
                    trainTreeHandler = factory.getGroupsHandler(model.getDiagram());
                    break;
            }
            trainTreeHandler.addTrains(model.getDiagram().getTrains());
            trainTree.setModel(trainTreeHandler.getTreeModel());
            createButton.setEnabled(true);
            deleteButton.setEnabled(false);
            moveToGroupMenuItem.setEnabled(false);
            changeRouteMenuItem.setEnabled(false);
            menuButton.setEnabled(true);
        } else {
            trainTree.setModel(null);
            trainTreeHandler = null;
            createButton.setEnabled(false);
            deleteButton.setEnabled(false);
            moveToGroupMenuItem.setEnabled(false);
            changeRouteMenuItem.setEnabled(false);
            menuButton.setEnabled(false);
        }
        if (trainTreeHandler != null)
            this.selectTrain(model.getSelectedTrain());
    }

    private Filter<Train> createFilter() {
        if (groupSelect.getType() == Type.ALL)
            return new EmptyFilter<Train>();
        else
            return new GroupFilter<Train, Train>(groupSelect.getGroup());
    }

    private void addAndSelectTrain(Train train) {
        TrainTreeNode addedNode = trainTreeHandler.addTrain(train);
        this.selectNode(addedNode);
    }

    private void deleteAndDeselectTrain(Train train) {
        trainTreeHandler.removeTrain(train);
        this.selectNode(null);
    }

    private void modifyAndSelectTrain(Train train) {
        trainTreeHandler.removeTrain(train);
        this.addAndSelectTrain(train);
    }

    private TreePath selectTrain(Train train) {
        TrainTreeNode node = trainTreeHandler.getTrain(train);
        return this.selectNode(node);
    }

    private TreePath getPath(TrainTreeNode node) {
        TreePath p = null;
        if (node != null) {
            p = new TreePath(node.getPath());
        }
        return p;
    }

    private TreePath selectNode(TrainTreeNode node) {
        TreePath p = this.getPath(node);
        trainTree.setSelectionPath(p);
        trainTree.scrollPathToVisible(p);
        return p;
    }

    private void moveToGroup() {
        GroupChooserDialog dialog = new GroupChooserDialog();
        dialog.setLocationRelativeTo(menuButton);
        dialog.showDialog(model.getDiagram(), groupSelect.getGroup());
        if (dialog.isSelected()) {
            Set<Train> selectedTrains = this.getSelectedTrains();
            Group group = dialog.getSelected();
            for (Train train : selectedTrains) {
                if (group == null)
                    train.removeAttribute(Train.ATTR_GROUP);
                else
                    train.setAttribute(Train.ATTR_GROUP, group);
            }
        }
    }

    private void changeRoute() {
        CreateRouteDialog dialog = new CreateRouteDialog();
        dialog.setLocationRelativeTo(menuButton);
        Train oldTrain = model.getSelectedTrain();
        List<Node> result = dialog.showDialog(model.getDiagram(), Arrays.asList(oldTrain.getStartNode(), oldTrain.getEndNode()));
        if (result != null) {
            try {
                RouteBuilder rb = new RouteBuilder();
                Route route = rb.createRoute(null, model.getDiagram().getNet(), result);
                if (route == null)
                    throw new IllegalArgumentException("No route available.");
                TrainBuilder builder = new TrainBuilder();
                Train newTrain = builder.createTrain(oldTrain, route);
                model.setSelectedTrain(null);
                this.deleteTrain(oldTrain, oldTrain.getTrainDiagram());
                model.getDiagram().addTrain(newTrain);
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.NEW_TRAIN, model, newTrain));
                model.setSelectedTrain(newTrain);
            } catch (Exception e) {
                LOG.warn("Error changing route of the train.", e);
                ActionUtils.showError(ResourceLoader.getString("dialog.error.title") + ": " + e.getMessage(), this);
            }
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        selecting = true;
        if (e.isAddedPath()) {
            Object selected = e.getPath().getLastPathComponent();
            TrainTreeNode node = (TrainTreeNode) selected;
            Object item = node.getUserObject();
            if (!(item instanceof Train) || trainTree.getSelectionCount() > 1) {
                if (model.getSelectedTrain() != null)
                    model.setSelectedTrain(null);
            } else {
                Train train = (Train) item;
                if (model.getSelectedTrain() != train)
                    model.setSelectedTrain(train);
            }
        }
        boolean selectionEmpty = trainTree.isSelectionEmpty();
        deleteButton.setEnabled(!selectionEmpty);
        moveToGroupMenuItem.setEnabled(!selectionEmpty);
        changeRouteMenuItem.setEnabled(model.getSelectedTrain() != null);
        selecting = false;
    }

    public TreeType getTreeType() {
        return treeType;
    }

    public void setTreeType(TreeType treeType) {
        if (treeType != this.treeType) {
            this.treeType = treeType;
            switch (treeType) {
                case FLAT:
                    listFlatMenuItem.setSelected(true);
                    break;
                case TYPES:
                    listTypesMenuItem.setSelected(true);
                    break;
                case GROUPS_AND_TYPES:
                    listGroupsMenuItem.setSelected(true);
                    break;
                case GROUPS:
                    listGroupsFlatMenuItem.setSelected(true);
            }
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
                Collection<Train> trains = trainTreeHandler.getTrains(node);
                selected.addAll(trains);
            }
        }
        return selected;
    }

    private void deleteTrain(Train deletedTrain, TrainDiagram diagram) {
        DeleteTrainCommand deleteCommand = new DeleteTrainCommand(deletedTrain);
        try {
            model.applyCommand(deleteCommand);
        } catch (CommandException e) {
            LOG.error(e.getMessage(), e);
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
        if (evt.getSource() == listFlatMenuItem)
            treeType = TrainListView.TreeType.FLAT;
        else if (evt.getSource() == listTypesMenuItem)
            treeType = TrainListView.TreeType.TYPES;
        else if (evt.getSource() == listGroupsMenuItem)
            treeType = TrainListView.TreeType.GROUPS_AND_TYPES;
        else
            treeType = TrainListView.TreeType.GROUPS;
        // update list
        this.updateViewDiagramChanged();
    }// GEN-LAST:event_treeTypeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private final javax.swing.JButton createButton;
    private final javax.swing.JButton deleteButton;
    private final javax.swing.JRadioButtonMenuItem listFlatMenuItem;
    private final javax.swing.JRadioButtonMenuItem listGroupsMenuItem;
    private final javax.swing.JRadioButtonMenuItem listGroupsFlatMenuItem;
    private final javax.swing.JScrollPane scrollPane;
    private final javax.swing.JTree trainTree;
    private final javax.swing.JPopupMenu treePopupMenu;
    private final javax.swing.JRadioButtonMenuItem listTypesMenuItem;
    private final javax.swing.JMenu groupsMenu;
    private final javax.swing.JButton menuButton;
    private final javax.swing.JMenuItem moveToGroupMenuItem;
    private final javax.swing.JMenuItem changeRouteMenuItem;
}
