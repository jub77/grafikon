/*
 * TCTrainListView.java
 *
 * Created on 12. září 2007, 16:07
 */
package net.parostroj.timetable.gui.views;

import java.awt.Color;
import java.awt.Component;
import java.util.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.filters.TrainTypeFilter;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.components.TrainSelector;
import net.parostroj.timetable.gui.dialogs.TCItemChangeDialog;
import net.parostroj.timetable.gui.dialogs.TrainsFilterDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.TCDelegate.Action;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.*;

/**
 * View with list of train on one side and list of train of the cycle
 * on the other.
 *
 * @author jub
 */
public class TCTrainListView extends javax.swing.JPanel implements TCDelegate.Listener, TrainSelector {

    private static final int BUTTON_MARGIN = 1;

    private static enum TrainFilter {

        PASSENGER("P"), FREIGHT("F"), CUSTOM("C"), ALL("A");

        private String key;

        private TrainFilter(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public boolean isType(String key) {
            return this.key.equals(key);
        }
    }

    private TCDelegate delegate;
    private TrainSort sort;
    private TrainTypeFilter filter;
    private boolean overlappingEnabled;
    private final TCItemChangeDialog changeDialog;

    /** Creates new form ECTrainListView */
    public TCTrainListView() {
        initComponents();
        allTrainsList.setModel(new DefaultListModel());
        cTrainsList.setModel(new DefaultListModel());
        changeDialog = new TCItemChangeDialog();
    }

    public void setModel(TCDelegate delegate) {
        this.delegate = delegate;
        this.delegate.addListener(this);
        this.overlappingCheckBoxMenuItem.setEnabled(delegate.isOverlappingEnabled());
        overlappingEnabled = false;
        overlappingCheckBoxMenuItem.setSelected(false);

        this.updateListAllTrains();
    }

    @Override
    public void tcEvent(Action action, TrainsCycle cycle, Train train) {
        switch (action) {
            case REFRESH:
                if (delegate.getTrainDiagram() != null) {
                    this.sort = new TrainSort(
                            new TrainComparator(
                            TrainComparator.Type.ASC,
                            delegate.getTrainDiagram().getTrainsData().getTrainSortPattern()));
                }
                this.updateListAllTrains();
                break;
            case NEW_TRAIN:
                this.updateListAllTrains();
                break;
            case DELETED_TRAIN:
                this.updateListCycle();
                this.updateListAllTrains();
                break;
            case SELECTED_CHANGED:
                addButton.setEnabled(delegate.getSelectedCycle() != null && !allTrainsList.isSelectionEmpty());
                this.updateListCycle();
                this.updateErrors();
                break;
            case DELETED_CYCLE:
                this.updateListAllTrains();
                break;
            case REFRESH_TRAIN_NAME:
                this.updateTrainName(train);
                break;
            default:
                // nothing
        }
    }

    private void updateListAllTrains() {
        // left list with available trains
        if (delegate.getTrainDiagram() == null) {
            allTrainsList.setModel(new DefaultListModel());
        } else {
            // get all trains (sort)
            List<Train> trainsList = new ArrayList<Train>();
            for (Train train : delegate.getTrainDiagram().getTrains()) {
                if (overlappingEnabled || !train.isCovered(delegate.getType())) {
                    if (filter == null || filter.is(train))
                        trainsList.add(train);
                }
            }
            // sort them
            trainsList = sort.sort(trainsList);

            DefaultListModel m = new DefaultListModel();
            for (Train train : trainsList) {
                m.addElement(Wrapper.getWrapper(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, train.getTrainDiagram())));
            }
            allTrainsList.setModel(m);
        }
    }

    private void updateTrainName(Train train) {
        // all train list
        DefaultListModel model = (DefaultListModel) allTrainsList.getModel();
        int size = model.getSize();
        for (int i = 0; i < size; i ++) {
            Wrapper<?> w = (Wrapper<?>) model.getElementAt(i);
            if (w.getElement() == train) {
                model.setElementAt(w, i);
            }
        }
        // circulation list
        model = (DefaultListModel) cTrainsList.getModel();
        size = model.getSize();
        for (int i = 0; i < size; i++) {
            Wrapper<?> w = (Wrapper<?>) model.getElementAt(i);
            if (((TrainsCycleItem) w.getElement()).getTrain() == train) {
                model.setElementAt(w, i);
            }
        }
    }

    private void updateListCycle() {
        // right list with assign trains
        if (delegate.getSelectedCycle() == null) {
            ((DefaultListModel)cTrainsList.getModel()).clear();
        } else {
            DefaultListModel m = (DefaultListModel)cTrainsList.getModel();
            m.clear();
            for (TrainsCycleItem item : delegate.getSelectedCycle()) {
                m.addElement(Wrapper.getWrapper(item));
            }
            cTrainsList.setModel(m);
        }
    }

    private void updateErrors() {
        TrainsCycle selectedCycle = delegate.getSelectedCycle();
        if (selectedCycle != null) {
            infoTextArea.setText(delegate.getTrainCycleErrors(selectedCycle));
        } else {
            infoTextArea.setText("");
        }
    }

    private TimeInterval lastSelected;

    @Override
    public void selectTrainInterval(TimeInterval interval) {
        if (interval != null) {
            // select in left list
            allTrainsList.setSelectedValue(Wrapper.getWrapper(interval.getTrain(), null), true);
            // select all intervals in right list
            DefaultListModel dlm = (DefaultListModel) cTrainsList.getModel();
            boolean selection = false;
            for (int i = 0; i < dlm.getSize(); i++) {
                Wrapper<?> w = (Wrapper<?>) dlm.getElementAt(i);
                if (((TrainsCycleItem) w.getElement()).getTrain() == interval.getTrain()) {
                    if (!selection) {
                        selection = true;
                        cTrainsList.clearSelection();
                    }
                    cTrainsList.addSelectionInterval(i, i);
                }
            }
        }
        lastSelected = interval;
    }

    @Override
    public TimeInterval getSelectedTrainInterval() {
        return lastSelected;
    }

    private void initComponents() {
        filterMenu = new javax.swing.JPopupMenu();
        javax.swing.JRadioButtonMenuItem allRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JRadioButtonMenuItem passengerRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JRadioButtonMenuItem freightRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JRadioButtonMenuItem customRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JSeparator separator = new javax.swing.JSeparator();
        overlappingCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        filterbuttonGroup = new javax.swing.ButtonGroup();
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        allTrainsList = new javax.swing.JList();
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        cTrainsList = new javax.swing.JList();
        addButton = GuiComponentUtils.createButton(GuiIcon.DARROW_RIGHT, BUTTON_MARGIN);
        removeButton = GuiComponentUtils.createButton(GuiIcon.DARROW_LEFT, BUTTON_MARGIN);
        javax.swing.JScrollPane errorsScrollPane = new javax.swing.JScrollPane();
        infoTextArea = new javax.swing.JTextArea();
        changeButton = GuiComponentUtils.createButton(GuiIcon.EDIT, BUTTON_MARGIN);
        javax.swing.JScrollPane coverageScrollPane = new javax.swing.JScrollPane();
        coverageScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        coverageTextPane = new net.parostroj.timetable.gui.views.ColorTextPane();
        selectionButton = GuiComponentUtils.createButton(GuiIcon.CONFIGURE_T, BUTTON_MARGIN);

        filterbuttonGroup.add(allRadioButtonMenuItem);
        allRadioButtonMenuItem.setSelected(true);
        allRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.all")); // NOI18N
        allRadioButtonMenuItem.setActionCommand(TrainFilter.ALL.getKey());
        allRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(allRadioButtonMenuItem);

        filterbuttonGroup.add(passengerRadioButtonMenuItem);
        passengerRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.passenger")); // NOI18N
        passengerRadioButtonMenuItem.setActionCommand(TrainFilter.PASSENGER.getKey());
        passengerRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(passengerRadioButtonMenuItem);

        filterbuttonGroup.add(freightRadioButtonMenuItem);
        freightRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.freight")); // NOI18N
        freightRadioButtonMenuItem.setActionCommand(TrainFilter.FREIGHT.getKey());
        freightRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(freightRadioButtonMenuItem);

        filterbuttonGroup.add(customRadioButtonMenuItem);
        customRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.custom")); // NOI18N
        customRadioButtonMenuItem.setActionCommand(TrainFilter.CUSTOM.getKey());
        customRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(customRadioButtonMenuItem);
        filterMenu.add(separator);

        overlappingCheckBoxMenuItem.setText(ResourceLoader.getString("filter.trains.overlapping")); // NOI18N
        overlappingCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overlappingCheckBoxMenuItemActionPerformed(evt);
            }
        });
        filterMenu.add(overlappingCheckBoxMenuItem);

        scrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        allTrainsList.setComponentPopupMenu(filterMenu);
        allTrainsList.setPrototypeCellValue("mmmmmmmmmmmmm");
        allTrainsList.setVisibleRowCount(5);
        allTrainsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                allTrainsListValueChanged(evt);
            }
        });
        scrollPane1.setViewportView(allTrainsList);

        scrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        cTrainsList.setPrototypeCellValue("mmmmmmmmmmmmm");
        cTrainsList.setVisibleRowCount(5);
        cTrainsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ecTrainsListValueChanged(evt);
            }
        });
        scrollPane2.setViewportView(cTrainsList);

        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        infoTextArea.setColumns(20);
        infoTextArea.setEditable(false);
        infoTextArea.setFont(addButton.getFont());
        infoTextArea.setRows(3);
        errorsScrollPane.setViewportView(infoTextArea);

        changeButton.setEnabled(false);
        changeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeButtonActionPerformed(evt);
            }
        });

        coverageScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        coverageScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        coverageTextPane.setEditable(false);
        coverageScrollPane.setViewportView(coverageTextPane);

        selectionButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.TRAILING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(removeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(selectionButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addComponent(changeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                .addComponent(coverageScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addComponent(errorsScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(addButton)
                            .addGap(0)
                            .addComponent(removeButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(changeButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(selectionButton))
                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(coverageScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(errorsScrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        this.setLayout(layout);
    }

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Object[] selectedValues = cTrainsList.getSelectedValues();
        for (Object selectedObject : selectedValues) {
            Wrapper<?> selected = (Wrapper<?>) selectedObject;
            if (selected != null) {
                TrainsCycleItem item = (TrainsCycleItem) selected.getElement();
                item.getCycle().removeItem(item);
                delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, delegate.getSelectedCycle());
            }
        }
        if (selectedValues.length > 0) {
            this.updateListAllTrains();
            this.updateListCycle();
            this.updateErrors();
        }
    }

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Object[] selectedValues = allTrainsList.getSelectedValues();
        boolean warning = delegate.showCorrectionWarning();
        StringBuilder trainsStr = null;
        for (Object objectSelected : selectedValues) {
            Wrapper<?> selected = (Wrapper<?>) objectSelected;
            if (selected != null) {
                Train t = (Train) selected.getElement();
                TrainsCycle cycle = delegate.getSelectedCycle();
                if (cycle != null) {
                    TrainsCycleItem item = null;
                    if (overlappingEnabled) {
                        item = new TrainsCycleItem(cycle, t, null, t.getFirstInterval(), t.getLastInterval());
                    } else {
                        Tuple<TimeInterval> tuple = t.getFirstUncoveredPart(delegate.getType());
                        item = new TrainsCycleItem(cycle, t, null, tuple.first, tuple.second);
                    }
                    // add to correct place
                    cycle.addItem(item);
                    // recalculate if needed (engine class dependency)
                    if (t.checkNeedSpeedRecalculate()) {
                        t.recalculate();
                        if (warning) {
                            if (trainsStr == null)
                                trainsStr = new StringBuilder();
                            else
                                trainsStr.append(',');
                            trainsStr.append(t.getName());
                        }
                    }

                    delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, delegate.getSelectedCycle());
                }
            }
        }
        if (selectedValues.length > 0) {
            this.updateListAllTrains();
            this.updateListCycle();
            this.updateErrors();
        }
        if (warning && trainsStr != null) {
            ActionUtils.showWarning(
                    String.format(ResourceLoader.getString("dialog.warning.trains.recalculated"), trainsStr),
                    ActionUtils.getTopLevelComponent(this));
        }
    }

    private void changeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (cTrainsList.getSelectedIndex() != -1) {
            TrainsCycleItem item = (TrainsCycleItem) ((Wrapper<?>) cTrainsList.getSelectedValue()).getElement();
            Train train = item.getTrain();
            if (!changeDialog.showDialog(this, item))
                return;
            // check if the comment changed ...
            String commentText = Conversions.checkAndTrim(changeDialog.getComment());
            item.setComment(commentText);
            TimeInterval from = changeDialog.getFrom();
            TimeInterval to = changeDialog.getTo();
            // new trains cycle item
            boolean oldCovered = train.isCovered(delegate.getType());
            if (from != item.getFromInterval() || to != item.getToInterval()) {
                TrainsCycleItem newItem = new TrainsCycleItem(item.getCycle(), train, item.getComment(), from, to);
                if (train.testAddCycle(newItem, item, overlappingEnabled)) {
                    TrainsCycle cycle = item.getCycle();
                    cycle.replaceItem(newItem, item);
                    int index = cycle.getItems().indexOf(newItem);
                    cTrainsList.setSelectedIndex(index);
                    ((DefaultListModel) cTrainsList.getModel()).set(index, Wrapper.getWrapper(newItem));
                    this.updateSelectedTrainsCycleItem(newItem);
                    this.updateErrors();
                    if (!overlappingEnabled && oldCovered != train.isCovered(delegate.getType()))
                        this.updateListAllTrains();
                    // recalculate if needed (engine class depedency)
                    if (train.checkNeedSpeedRecalculate()) {
                        train.recalculate();
                        if (delegate.showCorrectionWarning()) {
                            ActionUtils.showWarning(
                                    String.format(ResourceLoader.getString("dialog.warning.trains.recalculated"), train.getName()),
                                    ActionUtils.getTopLevelComponent(this));
                        }
                    }
                } else {
                    this.updateSelectedTrainsCycleItem(item);
                }
            }
            cTrainsList.repaint();
            delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, delegate.getSelectedCycle());
        }
    }

    private void ecTrainsListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            boolean selectedOne = cTrainsList.getSelectedIndex() != -1 && cTrainsList.getMaxSelectionIndex() == cTrainsList.getMinSelectionIndex();
            boolean selected = cTrainsList.getSelectedIndex() != -1;
            TrainsCycleItem item = (TrainsCycleItem) (selectedOne ? ((Wrapper<?>) cTrainsList.getSelectedValue()).getElement() : null);
            this.updateSelectedTrainsCycleItem(item);
            changeButton.setEnabled(selectedOne);
            removeButton.setEnabled(selected);
        }
    }

    private void updateSelectedTrainsCycleItem(TrainsCycleItem item) {
        if (item == null) {
            coverageTextPane.setText("");
        } else {
            List<Pair<TimeInterval, Boolean>> coverage = item.getTrain().getRouteCoverage(delegate.getType());
            coverageTextPane.setText("");
            for (Pair<TimeInterval, Boolean> pair : coverage) {
                this.appendSegment(coverageTextPane, pair);
            }
        }

    }

    private void appendSegment(ColorTextPane pane, Pair<TimeInterval, Boolean> segment) {
        if (segment.first.isNodeOwner()) {
            if (!segment.second) {
                pane.append(Color.BLACK, segment.first.getOwnerAsNode().getName());
            } else {
                pane.append(Color.RED, segment.first.getOwnerAsNode().getName());
            }
        } else {
            if (!segment.second)
                pane.append(Color.BLACK, " x ");
            else
                pane.append(Color.RED, " - ");
        }
    }

    private void allTrainsListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        // until now nothing here
        if (!evt.getValueIsAdjusting()) {
            addButton.setEnabled(!allTrainsList.isSelectionEmpty() && delegate.getSelectedCycle() != null);
        }
    }

    private void filterChangedActionPerformed(java.awt.event.ActionEvent evt) {
        ButtonModel selected = filterbuttonGroup.getSelection();
        if (selected != null)
            this.setFilter(selected.getActionCommand(), (Component) evt.getSource());
    }

    private void overlappingCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // enable/disable overlapping (refresh list of all trains)
        overlappingEnabled = overlappingCheckBoxMenuItem.isSelected();
        this.updateListAllTrains();
    }

    private void selectionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        filterMenu.show(selectionButton, 3, 3);
    }

    private void setFilter(String type, Component component) {
        if (TrainFilter.PASSENGER.isType(type)) {
            filter = TrainTypeFilter.getTrainFilter(TrainTypeFilter.PredefinedType.PASSENGER);
        } else if (TrainFilter.FREIGHT.isType(type)) {
            filter = TrainTypeFilter.getTrainFilter(TrainTypeFilter.PredefinedType.FREIGHT);
        } else if (TrainFilter.CUSTOM.isType(type)) {
            // custom filter
            TrainsFilterDialog dialog = new TrainsFilterDialog((java.awt.Frame)ActionUtils.getTopLevelComponent(component), true);
            dialog.setTrainTypes(delegate.getTrainDiagram(), selectedTypes);
            dialog.setLocationRelativeTo(ActionUtils.getTopLevelComponent(component));
            dialog.setVisible(true);

            this.selectedTypes = dialog.getSelectedTypes();
            this.filter = TrainTypeFilter.getTrainFilter(selectedTypes);
        } else {
            filter = null;
        }
        this.updateListAllTrains();
    }

    private Set<TrainType> selectedTypes = new HashSet<TrainType>();

    private javax.swing.JButton addButton;
    private javax.swing.JList allTrainsList;
    private javax.swing.JButton changeButton;
    private net.parostroj.timetable.gui.views.ColorTextPane coverageTextPane;
    private javax.swing.JList cTrainsList;
    private javax.swing.JPopupMenu filterMenu;
    private javax.swing.ButtonGroup filterbuttonGroup;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JCheckBoxMenuItem overlappingCheckBoxMenuItem;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton selectionButton;
}
