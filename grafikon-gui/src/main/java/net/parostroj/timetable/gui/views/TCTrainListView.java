/*
 * ECTrainListView.java
 *
 * Created on 12. září 2007, 16:07
 */
package net.parostroj.timetable.gui.views;

import java.awt.Component;
import java.awt.Color;
import java.util.*;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.components.TrainSelector;
import net.parostroj.timetable.gui.dialogs.TrainsFilterDialog;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
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
                m.addElement(new Wrapper<Train>(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, train.getTrainDiagram())));
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
        if (interval != null) {
            // select in left list
            allTrainsList.setSelectedValue(new Wrapper<Train>(interval.getTrain(), null), true);
            // select all intervals in right list
            DefaultListModel dlm = (DefaultListModel) ecTrainsList.getModel();
            boolean selection = false;
            for (int i = 0; i < dlm.getSize(); i++) {
                TrainsCycleItemWrapper w = (TrainsCycleItemWrapper) dlm.getElementAt(i);
                if (w.getItem().getTrain() == interval.getTrain()) {
                    if (!selection) {
                        selection = true;
                        ecTrainsList.clearSelection();
                    }
                    ecTrainsList.addSelectionInterval(i, i);
                }
            }
        }
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
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        allTrainsList = new javax.swing.JList();
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        ecTrainsList = new javax.swing.JList();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        javax.swing.JScrollPane errorsScrollPane = new javax.swing.JScrollPane();
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
        selectionButton = new javax.swing.JButton();

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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("gt_texts"); // NOI18N
        selectionButton.setText(bundle.getString("ec.list.selection")); // NOI18N
        selectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(selectionButton, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(upButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(removeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailsTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(coverageScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(changeButton))
            .addComponent(errorsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(upButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(downButton)
                        .addGap(18, 18, 18)
                        .addComponent(selectionButton))
                    .addComponent(scrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(detailsTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(fromComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(toComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeButton)
                    .addComponent(coverageScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(errorsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        boolean warning = model.getProgramSettings().isWarningAutoECCorrection();
        StringBuilder trainsStr = null;
        for (Object objectSelected : selectedValues) {
            Wrapper<?> selected = (Wrapper<?>) objectSelected;
            if (selected != null) {
                Train t = (Train) selected.getElement();
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
        if (warning && trainsStr != null) {
            ActionUtils.showWarning(
                    String.format(ResourceLoader.getString("dialog.warning.trains.recalculated"), trainsStr),
                    ActionUtils.getTopLevelComponent(this));
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
            TimeInterval from = (TimeInterval) ((Wrapper<?>)fromComboBox.getSelectedItem()).getElement();
            TimeInterval to = (TimeInterval) ((Wrapper<?>)toComboBox.getSelectedItem()).getElement();
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
                    // recalculate if needed (engine class depedency)
                    if (train.checkNeedSpeedRecalculate()) {
                        train.recalculate();
                        model.fireEvent(new ApplicationModelEvent(ApplicationModelEventType.MODIFIED_TRAIN, model, train));
                        if (model.getProgramSettings().isWarningAutoECCorrection()) {
                            ActionUtils.showWarning(
                                    String.format(ResourceLoader.getString("dialog.warning.trains.recalculated"), train.getName()),
                                    ActionUtils.getTopLevelComponent(this));
                        }
                    }
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
                Wrapper<TimeInterval> w = new Wrapper<TimeInterval>(interval);
                fromComboBox.addItem(w);
                toComboBox.addItem(w);
            }
        }
        fromComboBox.setSelectedItem(new Wrapper<TimeInterval>(from));
        toComboBox.setSelectedItem(new Wrapper<TimeInterval>(to));
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
            this.setFilter(selected.getActionCommand(), (Component) evt.getSource());
    }//GEN-LAST:event_filterChangedActionPerformed

    private void overlappingCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overlappingCheckBoxMenuItemActionPerformed
        // enable/disable overlapping (refresh list of all trains)
        overlappingEnabled = overlappingCheckBoxMenuItem.isSelected();
        this.updateListAllTrains();
    }//GEN-LAST:event_overlappingCheckBoxMenuItemActionPerformed

    private void selectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectionButtonActionPerformed
        filterMenu.show(selectionButton, 3, 3);
    }//GEN-LAST:event_selectionButtonActionPerformed

    private void setFilter(String type, Component component) {
        if ("P".equals(type)) {
            filter = TrainFilter.getTrainFilter(TrainFilter.PredefinedType.PASSENGER);
        } else if ("F".equals(type)) {
            filter = TrainFilter.getTrainFilter(TrainFilter.PredefinedType.FREIGHT);
        } else if ("C".equals(type)) {
            // custom filter
            TrainsFilterDialog dialog = new TrainsFilterDialog((java.awt.Frame)ActionUtils.getTopLevelComponent(component), true);
            dialog.setTrainTypes(model.getDiagram(), selectedTypes);
            dialog.setLocationRelativeTo((java.awt.Frame)ActionUtils.getTopLevelComponent(component));
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
    private javax.swing.JPopupMenu filterMenu;
    private javax.swing.ButtonGroup filterbuttonGroup;
    private javax.swing.JRadioButtonMenuItem freightRadioButtonMenuItem;
    private javax.swing.JComboBox fromComboBox;
    private javax.swing.JTextArea infoTextArea;
    private javax.swing.JCheckBoxMenuItem overlappingCheckBoxMenuItem;
    private javax.swing.JRadioButtonMenuItem passengerRadioButtonMenuItem;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton selectionButton;
    private javax.swing.JComboBox toComboBox;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
}
