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

import java.util.function.Predicate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import net.parostroj.timetable.actions.RouteBuilder;
import net.parostroj.timetable.actions.TrainBuilder;
import net.parostroj.timetable.filters.ModelPredicates;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.actions.EditGroupsAction;
import net.parostroj.timetable.gui.commands.*;
import net.parostroj.timetable.gui.components.GroupSelect;
import net.parostroj.timetable.gui.components.GroupSelect.Type;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.tree2.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * View with list of trains.
 *
 * @author jub
 */
public class TrainListView extends javax.swing.JPanel implements TreeSelectionListener {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(TrainListView.class);

    private transient ApplicationModel model;
    private ButtonGroup groupsBG;
    private final transient ItemListener groupL;
    private transient GroupSelect groupSelect;
    private transient TrainTreeHandler trainTreeHandler;
    private TreeType treeType = TreeType.TYPES;
    private boolean selecting = false;

    public enum TreeType {
        FLAT, TYPES, GROUPS_AND_TYPES, GROUPS
    }

    private static class GroupMenuItem extends javax.swing.JRadioButtonMenuItem {

        private static final long serialVersionUID = 1L;

		private final transient GroupSelect groupSelect;

        public GroupMenuItem(String text, GroupSelect groupSelect) {
            super(text);
            this.groupSelect = groupSelect;
        }

        public GroupSelect getGroupSelect() {
            return groupSelect;
        }
    }

    private class MenuAdapter extends MouseAdapter implements ActionListener {

        private int x = -1;
        private int y = -1;

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
        moveToGroupMenuItem.addActionListener(e -> moveToGroup());
        changeRouteMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("trainlist.change.route"));
        changeRouteMenuItem.addActionListener(e -> changeRoute());
        deleteMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("button.delete"));
        deleteMenuItem.addActionListener(e -> deleteAction());
        editMenuItem = new javax.swing.JMenuItem(ResourceLoader.getString("button.edit"));
        editMenuItem.addActionListener(e -> editAction());
        treePopupMenu.add(moveToGroupMenuItem);
        treePopupMenu.add(changeRouteMenuItem);
        treePopupMenu.add(editMenuItem);
        treePopupMenu.add(deleteMenuItem);
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
        listTypesMenuItem.addActionListener(this::treeTypeActionPerformed);
        listMenu.add(listTypesMenuItem);

        listFlatMenuItem.setText(ResourceLoader.getString("trainlist.tree.flat")); // NOI18N
        listFlatMenuItem.addActionListener(this::treeTypeActionPerformed);
        listMenu.add(listFlatMenuItem);

        listGroupsMenuItem.setText(ResourceLoader.getString("trainlist.tree.groups")); // NOI18N
        listGroupsMenuItem.addActionListener(this::treeTypeActionPerformed);
        listMenu.add(listGroupsMenuItem);

        listGroupsFlatMenuItem.setText(ResourceLoader.getString("trainlist.tree.groups.flat")); // NOI18N
        listGroupsFlatMenuItem.addActionListener(this::treeTypeActionPerformed);
        listMenu.add(listGroupsFlatMenuItem);

        groupsMenu = new javax.swing.JMenu(ResourceLoader.getString("trainlist.groups")); // NOI18N
        treePopupMenu.add(groupsMenu);

        groupL = e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                groupSelect = ((GroupMenuItem) e.getItem()).getGroupSelect();
                // selection of group regenerates tree with a new filter group
                updateViewDiagramChanged();
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
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane);

        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        trainTree.setComponentPopupMenu(treePopupMenu);
        trainTree.setModel(null);
        scrollPane.setViewportView(trainTree);

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        buttonPanel.add(createButton);

        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        buttonPanel.add(editButton);

        copyButton = GuiComponentUtils.createButton(GuiIcon.COPY, 2);
        buttonPanel.add(copyButton);

        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        buttonPanel.add(deleteButton);

        menuButton = GuiComponentUtils.createButton(GuiIcon.CONFIGURE_T, 2);
        buttonPanel.add(menuButton);
        MenuAdapter menuAdapter = new MenuAdapter();
        menuButton.addActionListener(menuAdapter);
        menuButton.addMouseListener(menuAdapter);

        deleteButton.addActionListener(evt -> deleteAction());
        createButton.addActionListener(this::createButtonActionPerformed);
        copyButton.addActionListener(e -> {
            CopyTrainDialog dialog = new CopyTrainDialog((java.awt.Frame) TrainListView.this.getTopLevelAncestor(),
                    true, model.getSelectedTrain());
            dialog.setLocationRelativeTo(TrainListView.this);
            dialog.setVisible(true);
            dialog.dispose();
        });
        editButton.addActionListener(e -> editAction());
        trainTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && model.getSelectedTrain() != null) {
                    editAction();
                }
            }
        });
        trainTree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && model.getSelectedTrain() != null) {
                    editAction();
                }
            }
        });
    }

    private EditTrainDialog getEditTrainDialog() {
        if (editDialog == null) {
            editDialog = new EditTrainDialog(GuiComponentUtils.getWindow(this), true);
        }
        return editDialog;
    }

    private void updateGroupsMenu(boolean added, Group group) {
        if (added) {
            GroupMenuItem newItem = new GroupMenuItem(group.getName(), new GroupSelect(GroupSelect.Type.GROUP, group));
            // skip first two items <all>,<none>
            for (int i = 2; i < groupsMenu.getItemCount(); i++) {
                JMenuItem item = groupsMenu.getItem(i);
                if (newItem.getText().compareTo(item.getText()) < 0) {
                    addToGroupsMenu(newItem, i);
                    newItem = null;
                    break;
                }
            }
            if (newItem != null) {
                addToGroupsMenu(newItem, null);
            }
        } else {
            GroupMenuItem item = findByGroup(group);
            if (Objects.requireNonNull(item).isSelected()) {
                groupsMenu.getItem(0).setSelected(true);
            }
            removeFromGroupsMenu(item);
        }
    }

    private GroupMenuItem findByGroup(Group group) {
        for (MenuElement e : groupsMenu.getPopupMenu().getSubElements()) {
            if (e instanceof GroupMenuItem) {
                GroupMenuItem item = (GroupMenuItem) e;
                if (group.equals(item.getGroupSelect().getGroup())) {
                    return item;
                }
            }
        }
        return null;
    }

    private void addToGroupsMenu(JMenuItem item, Integer position) {
        if (position == null) {
            groupsMenu.add(item);
        } else {
            groupsMenu.insert(item, position);
        }
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

    public void setModel(final ApplicationModel model) {
        this.model = model;
        javax.swing.JMenuItem groupsMenuItem = new javax.swing.JMenuItem();
        groupsMenuItem.setAction(new EditGroupsAction(model));
        groupsMenuItem.setText(ResourceLoader.getString("menu.groups"));
        treePopupMenu.add(groupsMenuItem);
        this.model.getMediator().addColleague(new ApplicationGTEventColleague() {
            @Override
            public void processApplicationEvent(ApplicationModelEvent event) {
                switch (event.getType()) {
                    case SET_DIAGRAM_CHANGED:
                        buildGroupsMenu();
                        updateViewDiagramChanged();
                        break;
                    case SELECTED_TRAIN_CHANGED:
                        if (!selecting) {
                            selectTrain((Train) event.getObject());
                        }
                        break;
                    case EDIT_SELECTED_TRAIN:
                        if (!selecting && model.getSelectedTrain() != null) {
                            editAction();
                        }
                        break;
                    default:
                        // do nothing
                        break;
                }
            }

            @Override
            public void processTrainDiagramEvent(Event event) {
                if (event.getType().isList()) {
                    if (event.getObject() instanceof Group) {
                        if (event.getType() == Event.Type.ADDED) {
                            updateGroupsMenu(true, (Group) event.getObject());
                        } else if (event.getType() == Event.Type.REMOVED) {
                            updateGroupsMenu(false, (Group) event.getObject());
                        }
                    } else if (event.getObject() instanceof Train) {
                        if (event.getType() == Event.Type.ADDED) {
                            addAndSelectTrain((Train) event.getObject());
                        } else if (event.getType() == Event.Type.REMOVED) {
                            deleteAndDeselectTrain((Train) event.getObject());
                        }
                    } else if (event.getObject() instanceof TrainType) {
                        updateViewDiagramChanged();
                    }
                }
            }

            @Override
            public void processTrainTypeEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE && event.getAttributeChange().checkName(TrainType.ATTR_DESC)) {
                    updateViewDiagramChanged();
                }
            }

            @Override
            public void processTrainEvent(Event event) {
                if (event.getType() == Event.Type.ATTRIBUTE) {
                    if (event.getAttributeChange().checkName(Train.ATTR_NAME)) {
                        refreshTrain((Train) event.getSource());
                    } else if (event.getAttributeChange().checkName(Train.ATTR_TYPE, Train.ATTR_GROUP)) {
                        modifyTrain((Train) event.getSource());
                    }
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
            menuButton.setEnabled(true);
        } else {
            trainTree.setModel(null);
            trainTreeHandler = null;
            createButton.setEnabled(false);
            menuButton.setEnabled(false);
        }
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        copyButton.setEnabled(false);
        moveToGroupMenuItem.setEnabled(false);
        changeRouteMenuItem.setEnabled(false);
        deleteMenuItem.setEnabled(false);
        editMenuItem.setEnabled(false);
        if (trainTreeHandler != null) {
            this.selectTrain(model.getSelectedTrain());
        }
    }

    private Predicate<Train> createFilter() {
        if (groupSelect.getType() == Type.ALL) {
            return value -> true;
        } else {
            return ModelPredicates.inGroup(groupSelect.getGroup());
        }
    }

    private void addAndSelectTrain(Train train) {
        TrainTreeNode addedNode = trainTreeHandler.addTrain(train);
        this.selectNode(addedNode);
    }

    private void deleteAndDeselectTrain(Train train) {
        trainTreeHandler.removeTrain(train);
        this.selectNode(null);
    }

    private void modifyTrain(Train train) {
        boolean selected = this.isTrainSelected(train);
        trainTreeHandler.removeTrain(train);
        TrainTreeNode node = trainTreeHandler.addTrain(train);
        if (selected) {
            addSelectedNode(node);
        }
    }

    private void refreshTrain(Train train) {
        TrainTreeNode node = trainTreeHandler.getTrain(train);
        trainTreeHandler.getTreeModel().nodeChanged(node);
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

    private TreePath addSelectedNode(TrainTreeNode node) {
        TreePath p = this.getPath(node);
        trainTree.addSelectionPath(p);
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
                train.getAttributes().setRemove(Train.ATTR_GROUP, group);
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
                if (route == null) {
                    throw new IllegalArgumentException("No route available.");
                }
                TrainBuilder builder = new TrainBuilder();
                model.setSelectedTrain(null);
                this.deleteTrain(oldTrain);
                Train newTrain = builder.createTrain(oldTrain, route);
                model.getDiagram().getTrains().add(newTrain);
                model.setSelectedTrain(newTrain);
            } catch (Exception e) {
                log.warn("Error changing route of the train.", e);
                GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.title") + ": " + e.getMessage(), this);
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
                if (model.getSelectedTrain() != null) {
                    model.setSelectedTrain(null);
                }
            } else {
                Train train = (Train) item;
                if (model.getSelectedTrain() != train) {
                    model.setSelectedTrain(train);
                }
            }
        }
        boolean selectionEmpty = trainTree.isSelectionEmpty() || (trainTree.getSelectionCount() == 1
                && Objects.requireNonNull(trainTree.getSelectionPath()).getParentPath() == null);
        deleteButton.setEnabled(!selectionEmpty);
        copyButton.setEnabled(model.getSelectedTrain() != null);
        editButton.setEnabled(model.getSelectedTrain() != null);
        moveToGroupMenuItem.setEnabled(!selectionEmpty);
        changeRouteMenuItem.setEnabled(model.getSelectedTrain() != null);
        deleteMenuItem.setEnabled(!selectionEmpty);
        editMenuItem.setEnabled(model.getSelectedTrain() != null);
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
                    break;
            }
            // update list
            this.updateViewDiagramChanged();
        }
    }

    private void deleteAction() {
        Set<Train> selectedTrains = this.getSelectedTrains();

        model.setSelectedTrain(null); // no train selected

        for (Train deletedTrain : selectedTrains) {
            this.deleteTrain(deletedTrain);
        }
    }

    private void editAction() {
        getEditTrainDialog().setLocationRelativeTo(GuiComponentUtils.getWindow(TrainListView.this));
        getEditTrainDialog().showDialog(model.getSelectedTrain());
    }

    private Set<Train> getSelectedTrains() {
        TreePath[] paths = trainTree.getSelectionPaths();
        Set<Train> selected = new HashSet<>();
        if (paths != null) {
            for (TreePath path : paths) {
                TrainTreeNode node = (TrainTreeNode) path.getLastPathComponent();
                Collection<Train> trains = trainTreeHandler.getTrains(node);
                selected.addAll(trains);
            }
        }
        return selected;
    }

    private boolean isTrainSelected(Train train) {
        TrainTreeNode node = trainTreeHandler.getTrain(train);
        return trainTree.isPathSelected(this.getPath(node));
    }

    private void deleteTrain(Train deletedTrain) {
        DeleteTrainCommand deleteCommand = new DeleteTrainCommand(deletedTrain);
        try {
            model.applyCommand(deleteCommand);
        } catch (GrafikonException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // call create new train dialog
        Frame f = (Frame) this.getTopLevelAncestor();

        CreateTrainDialog createDialog = new CreateTrainDialog(GuiComponentUtils.getWindow(this), model.getDiagram());
        createDialog.updateView(groupSelect.getGroup());

        createDialog.setLocationRelativeTo(f);
        createDialog.setVisible(true);
        createDialog.dispose();

        Command command = createDialog.getCreateTrainCommand();
        try {
            if (command != null) {
                model.applyCommand(command);
            }
        } catch (GrafikonException e) {
            log.warn("Error executing create train command.", e);
            GuiComponentUtils.showError(ResourceLoader.getString("create.train.createtrainerror"), this.getParent());
        }
    }

    private void treeTypeActionPerformed(java.awt.event.ActionEvent evt) {
        if (evt.getSource() == listFlatMenuItem) {
            treeType = TrainListView.TreeType.FLAT;
        } else if (evt.getSource() == listTypesMenuItem) {
            treeType = TrainListView.TreeType.TYPES;
        } else if (evt.getSource() == listGroupsMenuItem) {
            treeType = TrainListView.TreeType.GROUPS_AND_TYPES;
        } else {
            treeType = TrainListView.TreeType.GROUPS;
        }
        // update list
        this.updateViewDiagramChanged();
    }

    private EditTrainDialog editDialog;
    private final javax.swing.JButton createButton;
    private final javax.swing.JButton deleteButton;
    private final javax.swing.JButton editButton;
    private final javax.swing.JButton copyButton;
    private final javax.swing.JRadioButtonMenuItem listFlatMenuItem;
    private final javax.swing.JRadioButtonMenuItem listGroupsMenuItem;
    private final javax.swing.JRadioButtonMenuItem listGroupsFlatMenuItem;
    private final javax.swing.JTree trainTree;
    private final javax.swing.JPopupMenu treePopupMenu;
    private final javax.swing.JRadioButtonMenuItem listTypesMenuItem;
    private final javax.swing.JMenu groupsMenu;
    private final javax.swing.JButton menuButton;
    private final javax.swing.JMenuItem moveToGroupMenuItem;
    private final javax.swing.JMenuItem changeRouteMenuItem;
    private final javax.swing.JMenuItem deleteMenuItem;
    private final javax.swing.JMenuItem editMenuItem;
}
