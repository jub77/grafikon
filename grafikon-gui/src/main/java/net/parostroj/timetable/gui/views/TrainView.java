/*
 * TrainView.java
 *
 * Created on 31. srpen 2007, 12:50
 */
package net.parostroj.timetable.gui.views;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;
import java.util.*;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.IntervalSelectionMessage;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ResourceLoader;

import org.ini4j.Ini;

import com.google.common.collect.*;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

/**
 * View of train details.
 *
 * @author  jub
 */
public class TrainView extends javax.swing.JPanel implements ApplicationModelListener, StorableGuiData {

    private final class ToolTipHeaderWithPopupMenu extends ToolTipHeader {

        private ToolTipHeaderWithPopupMenu(TableColumnModel model) {
            super(model);
        }

        @Override
        public javax.swing.JPopupMenu getComponentPopupMenu() {
            this.setDraggedColumn(null);
            this.repaint();
            return ColumnSelectionDialog.createPopupMenu(trainTable, getCurrentColumns(), columns);
        }
    }

    private ApplicationModel model;
    private Train train;
    private TrainViewColumns columns;

    /**
     * Creates new form TrainView.
     */
    public TrainView() {
        initComponents();
        columns = new TrainViewColumns(trainTable);
    }

    public void editColumns() {
        Window window = GuiComponentUtils.getWindow(this);
        ColumnSelectionDialog dialog = new ColumnSelectionDialog(window, true);

        dialog.setLocationRelativeTo(window);
        dialog.selectColumns(trainTable, this::getCurrentColumns, columns);
        dialog.dispose();
    }

    private Set<TrainTableColumn> getCurrentColumns() {
        Set<TrainTableColumn> columns = new HashSet<>();
        Iterator<TableColumn> i = Iterators.forEnumeration(trainTable.getColumnModel().getColumns());
        while (i.hasNext()) {
            columns.add(TrainTableColumn.getColumn(i.next().getModelIndex()));
        }
        return columns;
    }

    public void resizeColumns() {
        // resize columns to original size
        TableColumnModel tcm = trainTable.getColumnModel();
        Enumeration<TableColumn> columns = tcm.getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            tc.setPreferredWidth(TrainTableColumn.getColumn(tc.getModelIndex()).getPrefWidth());
        }
    }

    public void sortColumns() {
        // sort columns to initial order
        TableColumnModel tcm = trainTable.getColumnModel();
        Enumeration<TableColumn> columns = tcm.getColumns();
        List<TableColumn> list = new LinkedList<>();
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            list.add(tc);
        }
        Collections.sort(list, (o1, o2) ->
                Integer.valueOf(Integer.valueOf(o1.getModelIndex()).compareTo(Integer.valueOf(o2.getModelIndex()))));
        for (TableColumn tc : list) {
            tcm.removeColumn(tc);
        }
        for (TableColumn tc : list) {
            tcm.addColumn(tc);
        }
    }

    public void setModel(final ApplicationModel model) {
        this.model = model;
        this.updateView(model.getSelectedTrain());
        this.model.addListener(this);
        ((TrainTableModel) trainTable.getModel()).setModel(model);
        model.getMediator().addColleague(message -> {
            IntervalSelectionMessage ism = (IntervalSelectionMessage) message;
            if (ism.getInterval() != null) {
                TimeInterval interval = ism.getInterval();
                Train train = interval.getTrain();
                if (train.getTimeIntervalBefore() == interval) {
                    interval = train.getFirstInterval();
                } else if (train.getTimeIntervalAfter() == interval) {
                    interval = train.getLastInterval();
                }
                if (train != TrainView.this.train) {
                    updateView(train);
                }
                int row = interval.getTrain().getTimeIntervalList().indexOf(interval);
                int column = TrainTableColumn.getIndex(trainTable.getColumnModel(),
                        interval.isNodeOwner() ? TrainTableColumn.STOP : TrainTableColumn.SPEED_LIMIT);
                trainTable.setRowSelectionInterval(row, row);
                if (column != -1) {
                    trainTable.setColumnSelectionInterval(column, column);
                }
                Rectangle rect = trainTable.getCellRect(row, 0, true);
                trainTable.scrollRectToVisible(rect);
                Component topLevelComponent = GuiComponentUtils.getTopLevelComponent(TrainView.this);
                if (topLevelComponent.hasFocus()) {
                    trainTable.requestFocus();
                }
            }
        }, IntervalSelectionMessage.class);
        model.getMediator().addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainEvent(Event event) {
                Train selectedTrain = model.getSelectedTrain();
                if (event.getSource() == selectedTrain) {
                    updateView(selectedTrain);
                }
            }
        }, Event.class);
    }


    @Override
    public void modelChanged(ApplicationModelEvent event) {
        if (event.getType() == ApplicationModelEventType.SELECTED_TRAIN_CHANGED || event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
            this.updateView(model.getSelectedTrain());
        }
    }

    private void updateView(Train train) {
        if (train == null) {
            trainTextField.setText(null);
            speedTextField.setText(null);
            techTimeTextField.setText(null);
            speedTextField.setEnabled(false);
        } else {
            // train type
            String name = train.getDefaultCompleteName();
            TextTemplate routeTemplate = train.getAttribute(Train.ATTR_ROUTE, TextTemplate.class);
            if (routeTemplate != null) {
                name = String.format("%s (%s)", name, routeTemplate.evaluate(TextTemplate.getBinding(train)));
            }
            name = this.addPreviousTrain(name, train);
            name = this.addNextTrain(name, train);
            trainTextField.setText(name);
            // scroll to the beginning - ensure that the start in visible
            trainTextField.setCaretPosition(0);
            Integer topSpeed = train.getTopSpeed();
            speedTextField.setText(topSpeed == null ? "" : Integer.toString(topSpeed));
            techTimeTextField.setText(this.createTechTimeString(train));
            techTimeTextField.setCaretPosition(0);
            speedTextField.setEnabled(true);
        }

        trainTable.removeEditor();
        ((TrainTableModel)trainTable.getModel()).setTrain(train);

        this.invalidate();
    }

    private String addPreviousTrain(String name, Train train) {
        if (train.getPreviousJoinedTrain() != null) {
            return String.format("[%s >] %s", train.getPreviousJoinedTrain().getDefaultName(), name);
        } else {
            return name;
        }
    }

    private String addNextTrain(String name, Train train) {
        if (train.getNextJoinedTrain() != null) {
            return String.format("%s [> %s]", name, train.getNextJoinedTrain().getDefaultName());
        } else {
            return name;
        }
    }

    private String createTechTimeString(Train train) {
        StringBuilder before = new StringBuilder(ResourceLoader.getString("create.train.time.before")).append(": ").append(train.getTimeBefore() / 60);
        StringBuilder after = new StringBuilder(ResourceLoader.getString("create.train.time.after")).append(": ").append(train.getTimeAfter() / 60);

        if (train.getTimeIntervalBefore() != null && train.getTimeIntervalBefore().isOverlapping()) {
            before.append(" [").append(this.getConflicts(train.getTimeIntervalBefore())).append("]");
        }
        if (train.getTimeIntervalAfter() != null && train.getTimeIntervalAfter().isOverlapping()) {
            after.append(" [").append(this.getConflicts(train.getTimeIntervalAfter())).append("]");
        }
        return before.append(", ").append(after).toString();
    }

    private String getConflicts(TimeInterval interval) {
        StringBuilder builder = new StringBuilder();
        for (TimeInterval overlap : interval.getOverlappingIntervals()) {
            if (builder.length() != 0) {
                builder.append(", ");
            }
            builder.append(overlap.getTrain().getDefaultName());
        }
        return builder.toString();
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        trainTextField = new javax.swing.JTextField();
        trainTableScrollPane = new javax.swing.JScrollPane();
        trainTable = new javax.swing.JTable();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        speedTextField = new javax.swing.JTextField();
        speedTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        techTimeTextField = new javax.swing.JTextField();
        techTimeTextField.setColumns(20);

        jLabel1.setText(ResourceLoader.getString("create.train.number")); // NOI18N

        trainTextField.setEditable(false);

        trainTable.setAutoCreateColumnsFromModel(false);
        trainTable.setModel(new TrainTableModel(model,train));
        trainTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        trainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ToolTipHeader header = new ToolTipHeaderWithPopupMenu(trainTable.getColumnModel());
        header.setToolTipText("text");
        trainTable.setTableHeader(header);
        trainTableScrollPane.setViewportView(trainTable);

        jLabel2.setText(ResourceLoader.getString("create.train.speed")); // NOI18N

        speedTextField.setColumns(4);
        speedTextField.setEditable(false);

        jLabel3.setText(ResourceLoader.getString("create.train.technological.time")); // NOI18N

        techTimeTextField.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(5)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(trainTableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(trainTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(jLabel2)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(jLabel3)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(techTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
                    .addGap(5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(5)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(trainTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(techTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(trainTableScrollPane, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGap(5))
        );
        this.setLayout(layout);
    }
    private javax.swing.JTextField speedTextField;
    private javax.swing.JTextField techTimeTextField;
    private javax.swing.JTable trainTable;
    private javax.swing.JScrollPane trainTableScrollPane;
    private javax.swing.JTextField trainTextField;

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        return columns.saveToPreferences(prefs);
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
        return columns.loadFromPreferences(prefs);
    }
}
