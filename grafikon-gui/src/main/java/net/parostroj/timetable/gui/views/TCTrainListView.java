/*
 * ECTrainListView.java
 *
 * Created on 12. září 2007, 16:07
 */
package net.parostroj.timetable.gui.views;

import net.parostroj.timetable.gui.helpers.TrainWrapper;
import java.awt.Color;
import java.util.*;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.TrainSelector;
import net.parostroj.timetable.gui.dialogs.TrainsFilterDialog;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.Tuple;

/**
 * View with list of train on one side and list of train of the cycle
 * on the other.
 *
 * @author jub
 */
public class TCTrainListView extends javax.swing.JPanel implements ApplicationModelListener, TrainSelector {

    private ApplicationModel model;
    private TCDelegate delegate;
    private TrainSort sort;
    private TrainFilter filter;
    private boolean overlappingEnabled;

    /** Creates new form ECTrainListView */
    public TCTrainListView() {
        initComponents();
        coverageScrollPane.getVerticalScrollBar().setUnitIncrement(10);
    }

    public void setModel(ApplicationModel model, TCDelegate delegate) {
        model.addListener(this);
        this.model = model;
        this.delegate = delegate;
        this.overlappingCheckBoxMenuItem.setEnabled(delegate.isOverlappingEnabled());
        overlappingEnabled = false;
        overlappingCheckBoxMenuItem.setSelected(false);

        this.updateListAllTrains();
    }

    @Override
    public void modelChanged(ApplicationModelEvent event) {
        TCDelegate.Action action = delegate.transformEventType(event.getType());
        switch (event.getType()) {
            case SET_DIAGRAM_CHANGED:
                if (model.getDiagram() != null) {
                    this.sort = new TrainSort(
                            new TrainComparator(
                            TrainComparator.Type.ASC,
                            model.getDiagram().getTrainsData().getTrainSortPattern()));
                }
                this.updateListAllTrains();
                break;
            case NEW_TRAIN:
                this.updateListAllTrains();
                break;
            case DELETE_TRAIN:
                this.updateListCycle();
                this.updateListAllTrains();
                break;
            default:
                // do nothing
                break;
        }
        if (action != null) {
            switch (action) {
                case SELECTED_CHANGED:
                    addButton.setEnabled(delegate.getSelectedCycle(model) != null && !allTrainsList.isSelectionEmpty());
                    this.updateListCycle();
                    this.updateErrors();
                    break;
                case DELETE_CYCLE:
                    this.updateListAllTrains();
                    break;
                default:
                // nothing
            }
        }
    }

    private void updateListAllTrains() {
        // left list with available trains
        if (model.getDiagram() == null) {
            allTrainsList.setModel(new DefaultListModel());
        } else {
            // get all trains (sort)
            List<Train> getTrains = new ArrayList<Train>();
            for (Train train : model.getDiagram().getTrains()) {
                if (overlappingEnabled || !train.isCovered(delegate.getType())) {
                    if (filter == null || filter.filter(train))
                        getTrains.add(train);
                }
            }
            // sort them
            getTrains = sort.sort(getTrains);

            DefaultListModel m = new DefaultListModel();
            for (Train train : getTrains) {
                m.addElement(new TrainWrapper(train, TrainWrapper.Type.NAME_AND_END_NODES_WITH_TIME));
            }
            allTrainsList.setModel(m);
        }
    }

    private void updateListCycle() {
        // right list with assign trains
        if (delegate.getSelectedCycle(model) == null) {
            ecTrainsList.setModel(new DefaultListModel());
        } else {
            DefaultListModel m = new DefaultListModel();
            for (TrainsCycleItem item : delegate.getSelectedCycle(model)) {
                m.addElement(new TrainsCycleItemWrapper(item));
            }
            ecTrainsList.setModel(m);
        }
    }

    private void updateErrors() {
        TrainsCycle selectedCycle = delegate.getSelectedCycle(model);
        if (selectedCycle != null) {
            infoTextArea.setText(delegate.getTrainCycleErrors(selectedCycle, model.getDiagram()));
        } else {
            infoTextArea.setText("");
        }
    }
    private TimeInterval lastSelected;

    @Override
    public void selectTrainInterval(TimeInterval interval) {
        if (interval != null)
            allTrainsList.setSelectedValue(new TrainWrapper(interval.getTrain(), TrainWrapper.Type.NAME_AND_END_NODES_WITH_TIME), true);
        if (interval == null)
            lastSelected = null;
        else
            lastSelected = interval;
    }

    @Override
    public TimeInterval getSelectedTrainInterval() {
        return lastSelected;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filterMenu = new javax.swing.JPopupMenu();
        allRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        passengerRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        freightRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        customRadioButtonMenuItem = new javax.swing.JRadioButtonMenuItem();
        javax.swing.JSeparator separator = new javax.swing.JSeparator();
        overlappingCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        filterbuttonGroup = new javax.swing.ButtonGroup();
        scrollPane1 = new javax.swing.JScrollPane();
        allTrainsList = new javax.swing.JList();
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        ecTrainsList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        errorsScrollPane = new javax.swing.JScrollPane();
        infoTextArea = new javax.swing.JTextArea();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        changeButton = new javax.swing.JButton();
        fromComboBox = new javax.swing.JComboBox();
        toComboBox = new javax.swing.JComboBox();
        detailsTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        coverageScrollPane = new javax.swing.JScrollPane();
        coverageTextPane = new net.parostroj.timetable.gui.views.ColorTextPane();

        filterbuttonGroup.add(allRadioButtonMenuItem);
        allRadioButtonMenuItem.setSelected(true);
        allRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.all")); // NOI18N
        allRadioButtonMenuItem.setActionCommand("A");
        allRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(allRadioButtonMenuItem);

        filterbuttonGroup.add(passengerRadioButtonMenuItem);
        passengerRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.passenger")); // NOI18N
        passengerRadioButtonMenuItem.setActionCommand("P");
        passengerRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(passengerRadioButtonMenuItem);

        filterbuttonGroup.add(freightRadioButtonMenuItem);
        freightRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.freight")); // NOI18N
        freightRadioButtonMenuItem.setActionCommand("F");
        freightRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(freightRadioButtonMenuItem);

        filterbuttonGroup.add(customRadioButtonMenuItem);
        customRadioButtonMenuItem.setText(ResourceLoader.getString("filter.trains.custom")); // NOI18N
        customRadioButtonMenuItem.setActionCommand("C");
        customRadioButtonMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterChangedActionPerformed(evt);
            }
        });
        filterMenu.add(customRadioButtonMenuItem);
        filterMenu.add(separator);

        overlappingCheckBoxMenuItem.setText(ResourceLoader.getString("filter.trains.overlapping")); // NOI18N
        overlappingCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
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
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                allTrainsListValueChanged(evt);
            }
        });
        scrollPane1.setViewportView(allTrainsList);

        scrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        ecTrainsList.setPrototypeCellValue("mmmmmmmmmmmmm");
        ecTrainsList.setVisibleRowCount(5);
        ecTrainsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                ecTrainsListValueChanged(evt);
            }
        });
        scrollPane2.setViewportView(ecTrainsList);

        addButton.setText(ResourceLoader.getString("ec.trains.add")); // NOI18N
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText(ResourceLoader.getString("ec.trains.remove")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        infoTextArea.setColumns(20);
        infoTextArea.setEditable(false);
        infoTextArea.setFont(addButton.getFont());
        infoTextArea.setRows(3);
        errorsScrollPane.setViewportView(infoTextArea);

        upButton.setText(ResourceLoader.getString("ec.trains.up")); // NOI18N
        upButton.setEnabled(false);
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        downButton.setText(ResourceLoader.getString("ec.trains.down")); // NOI18N
        downButton.setEnabled(false);
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        changeButton.setText(ResourceLoader.getString("ec.details.change")); // NOI18N
        changeButton.setEnabled(false);
        changeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeButtonActionPerformed(evt);
            }
        });

        fromComboBox.setEnabled(false);
        fromComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmmm");

        toComboBox.setEnabled(false);
        toComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmmm");

        detailsTextField.setEnabled(false);

        jLabel1.setText(ResourceLoader.getString("ec.list.comment")); // NOI18N

        coverageScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        coverageScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        coverageTextPane.setEditable(false);
        coverageScrollPane.setViewportView(coverageTextPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(upButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 491, Short.MAX_VALUE))
            .addComponent(errorsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 583, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(coverageScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(changeButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addGap(52, 52, 52)
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton))
                    .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                    .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(detailsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeButton)
                    .addComponent(coverageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
    // change order
    TrainsCycleItemWrapper selected = (TrainsCycleItemWrapper) ecTrainsList.getSelectedValue();
    int selectedIndex = ecTrainsList.getSelectedIndex();
    if (selected != null) {
        TrainsCycleItem item = selected.getItem();
        int newIndex = selectedIndex + 1;
        if (newIndex < item.getCycle().getItems().size()) {
            // remove ...
            DefaultListModel m = (DefaultListModel) ecTrainsList.getModel();
            m.remove(selectedIndex);
            // move to new place
            m.add(newIndex, selected);
            item.getCycle().moveItem(selectedIndex, newIndex);
            this.updateErrors();
            ecTrainsList.setSelectedValue(selected, true);
            ecTrainsList.repaint();
        }
        delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, model, delegate.getSelectedCycle(model));
    }
}//GEN-LAST:event_downButtonActionPerformed

private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
    // change order
    TrainsCycleItemWrapper selected = (TrainsCycleItemWrapper) ecTrainsList.getSelectedValue();
    int selectedIndex = ecTrainsList.getSelectedIndex();
    if (selected != null) {
        TrainsCycleItem item = selected.getItem();
        int newIndex = selectedIndex - 1;
        if (newIndex >= 0) {
            // remove ...
            DefaultListModel m = (DefaultListModel) ecTrainsList.getModel();
            m.remove(selectedIndex);
            // move to new place
            m.add(newIndex, selected);
            item.getCycle().moveItem(selectedIndex, newIndex);
            this.updateErrors();
            ecTrainsList.setSelectedValue(selected, true);
            ecTrainsList.repaint();
        }
        delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, model, delegate.getSelectedCycle(model));
    }
}//GEN-LAST:event_upButtonActionPerformed

private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
    Object[] selectedValues = ecTrainsList.getSelectedValues();
    for (Object selectedObject : selectedValues) {
        TrainsCycleItemWrapper selected = (TrainsCycleItemWrapper) selectedObject;
        if (selected != null) {
            TrainsCycleItem item = selected.getItem();
            item.getCycle().removeItem(item);
            model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, item.getTrain()));

            delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, model, delegate.getSelectedCycle(model));
        }
    }
    if (selectedValues.length > 0) {
        this.updateListAllTrains();
        this.updateListCycle();
        this.updateErrors();
    }
}//GEN-LAST:event_removeButtonActionPerformed

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
    Object[] selectedValues = allTrainsList.getSelectedValues();
    for (Object objectSelected : selectedValues) {
        TrainWrapper selected = (TrainWrapper) objectSelected;
        if (selected != null) {
            Train t = selected.getElement();
            TrainsCycle cycle = delegate.getSelectedCycle(model);
            if (cycle != null) {
                TrainsCycleItem item = null;
                if (overlappingEnabled) {
                    item = new TrainsCycleItem(cycle, t, null, t.getFirstInterval(), t.getLastInterval());
                } else {
                    Tuple<TimeInterval> tuple = t.getFirstUncoveredPart(delegate.getType());
                    item = new TrainsCycleItem(cycle, t, null, tuple.first, tuple.second);
                }
                cycle.addItem(item);
                model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, t));

                delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, model, delegate.getSelectedCycle(model));
            }
        }
    }
    if (selectedValues.length > 0) {
        this.updateListAllTrains();
        this.updateListCycle();
        this.updateErrors();
    }
}//GEN-LAST:event_addButtonActionPerformed

private void changeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeButtonActionPerformed
    if (ecTrainsList.getSelectedIndex() != -1) {
        TrainsCycleItem item = ((TrainsCycleItemWrapper) ecTrainsList.getSelectedValue()).getItem();
        Train train = item.getTrain();
        // check if the comment changed ...
        String newComment = "".equals(detailsTextField.getText().trim()) ? null : detailsTextField.getText();
        if ((newComment == null && item.getComment() != null) || (newComment != null && !newComment.equals(item.getComment())))
            item.setComment(newComment);
        TimeInterval from = ((TimeIntervalWrapper)fromComboBox.getSelectedItem()).getInterval();
        TimeInterval to = ((TimeIntervalWrapper)toComboBox.getSelectedItem()).getInterval();
        // new trains cycle item
        boolean oldCovered = train.isCovered(delegate.getType());
        if (from != item.getFromInterval() || to != item.getToInterval()) {
            TrainsCycleItem newItem = new TrainsCycleItem(item.getCycle(), train, item.getComment(), from, to);
            if (train.testAddCycle(newItem, item, overlappingEnabled)) {
                TrainsCycle cycle = item.getCycle();
                cycle.replaceItem(newItem, item);
                ((TrainsCycleItemWrapper)ecTrainsList.getSelectedValue()).setItem(newItem);
                this.updateSelectedTrainsCycleItem(newItem);
                this.updateErrors();
                if (!overlappingEnabled && oldCovered != train.isCovered(delegate.getType()))
                    this.updateListAllTrains();
                ecTrainsList.repaint();
            } else {
                this.updateSelectedTrainsCycleItem(item);
            }
        }
        delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, model, delegate.getSelectedCycle(model));
    }
}//GEN-LAST:event_changeButtonActionPerformed

private void ecTrainsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_ecTrainsListValueChanged
    if (!evt.getValueIsAdjusting()) {
        boolean selectedOne = ecTrainsList.getSelectedIndex() != -1 && ecTrainsList.getMaxSelectionIndex() == ecTrainsList.getMinSelectionIndex();
        boolean selected = ecTrainsList.getSelectedIndex() != -1;
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        TrainsCycleItem item = selectedOne ? ((TrainsCycleItemWrapper) ecTrainsList.getSelectedValue()).getItem() : null;
        this.updateSelectedTrainsCycleItem(item);
        detailsTextField.setEnabled(selectedOne);
        changeButton.setEnabled(selectedOne);
        fromComboBox.setEnabled(selectedOne);
        toComboBox.setEnabled(selectedOne);
        removeButton.setEnabled(selected);
        upButton.setEnabled(selectedOne);
        downButton.setEnabled(selectedOne);
    }
}//GEN-LAST:event_ecTrainsListValueChanged

    private void updateSelectedTrainsCycleItem(TrainsCycleItem item) {
        if (item == null) {
            detailsTextField.setText("");
            coverageTextPane.setText("");
        } else {
            detailsTextField.setText(item.getComment());
            List<Pair<TimeInterval, Boolean>> coverage = item.getTrain().getRouteCoverage(delegate.getType());
            coverageTextPane.setText("");
            for (Pair<TimeInterval, Boolean> pair : coverage) {
                this.appendSegment(coverageTextPane, pair);
            }
            this.updateFromTo(item.getTrain().getTimeIntervalList(), item.getFromInterval(), item.getToInterval());
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

    private void updateFromTo(List<TimeInterval> intervals, TimeInterval from, TimeInterval to) {
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        for (TimeInterval interval : intervals) {
            if (interval.isNodeOwner()) {
                TimeIntervalWrapper w = new TimeIntervalWrapper(interval);
                fromComboBox.addItem(w);
                toComboBox.addItem(w);
            }
        }
        fromComboBox.setSelectedItem(new TimeIntervalWrapper(from));
        toComboBox.setSelectedItem(new TimeIntervalWrapper(to));
    }

private void allTrainsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_allTrainsListValueChanged
    // until now nothing here
    if (!evt.getValueIsAdjusting()) {
        addButton.setEnabled(!allTrainsList.isSelectionEmpty() && delegate.getSelectedCycle(model) != null);
    }
}//GEN-LAST:event_allTrainsListValueChanged

private void filterChangedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterChangedActionPerformed
    ButtonModel selected = filterbuttonGroup.getSelection();
    if (selected != null)
        this.setFilter(selected.getActionCommand());
}//GEN-LAST:event_filterChangedActionPerformed

private void overlappingCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overlappingCheckBoxMenuItemActionPerformed
    // enable/disable overlapping (refresh list of all trains)
    overlappingEnabled = overlappingCheckBoxMenuItem.isSelected();
    this.updateListAllTrains();
}//GEN-LAST:event_overlappingCheckBoxMenuItemActionPerformed

    private void setFilter(String type) {
        if ("P".equals(type)) {
            filter = TrainFilter.getTrainFilter(TrainFilter.PredefinedType.PASSENGER);
        } else if ("F".equals(type)) {
            filter = TrainFilter.getTrainFilter(TrainFilter.PredefinedType.FREIGHT);
        } else if ("C".equals(type)) {
            // custom filter
            TrainsFilterDialog dialog = new TrainsFilterDialog((java.awt.Frame)this.getTopLevelAncestor(), true);
            dialog.setTrainTypes(model.getDiagram(), selectedTypes);
            dialog.setLocationRelativeTo(scrollPane1);
            dialog.setVisible(true);

            this.selectedTypes = dialog.getSelectedTypes();
            this.filter = TrainFilter.getTrainFilter(selectedTypes);
        } else {
            filter = null;
        }
        this.updateListAllTrains();
    }

    private Set<TrainType> selectedTypes = new HashSet<TrainType>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JRadioButtonMenuItem allRadioButtonMenuItem;
    private javax.swing.JList allTrainsList;
    private javax.swing.JButton changeButton;
    private javax.swing.JScrollPane coverageScrollPane;
    private net.parostroj.timetable.gui.views.ColorTextPane coverageTextPane;
    private javax.swing.JRadioButtonMenuItem customRadioButtonMenuItem;
    private javax.swing.JTextField detailsTextField;
    private javax.swing.JButton downButton;
    private javax.swing.JList ecTrainsList;
    private javax.swing.JScrollPane errorsScrollPane;
    private javax.swing.JPopupMenu filterMenu;
    private javax.swing.ButtonGroup filterbuttonGroup;
    private javax.swing.JRadioButtonMenuItem freightRadioButtonMenuItem;
    private javax.swing.JComboBox fromComboBox;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JCheckBoxMenuItem overlappingCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem passengerRadioButtonMenuItem;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane scrollPane1;
    private javax.swing.JComboBox toComboBox;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
}

class TimeIntervalWrapper {

    private final TimeInterval interval;

    TimeIntervalWrapper(TimeInterval interval) {
        this.interval = interval;
    }

    public TimeInterval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return interval.getOwner().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TimeIntervalWrapper other = (TimeIntervalWrapper) obj;
        if (this.interval != other.interval && (this.interval == null || !this.interval.equals(other.interval))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.interval != null ? this.interval.hashCode() : 0);
        return hash;
    }
}