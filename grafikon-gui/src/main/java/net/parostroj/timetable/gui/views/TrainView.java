/*
 * TrainView.java
 *
 * Created on 31. srpen 2007, 12:50
 */
package net.parostroj.timetable.gui.views;

import java.awt.Frame;
import java.awt.Rectangle;
import java.util.*;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.dialogs.*;
import net.parostroj.timetable.gui.utils.IntervalSelectionMessage;
import net.parostroj.timetable.mediator.Colleague;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.events.*;
import net.parostroj.timetable.utils.ResourceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

/**
 * View of train details.
 *
 * @author  jub
 */
public class TrainView extends javax.swing.JPanel implements ApplicationModelListener, StorableGuiData {

    private static final Logger LOG = LoggerFactory.getLogger(TrainView.class.getName());
    private ApplicationModel model;
    private Train train;

    /**
     * Creates new form TrainView.
     */
    public TrainView() {
        initComponents();
    }

    public void editColumns() {
        ColumnsDialog dialog = new ColumnsDialog((Frame)this.getTopLevelAncestor(), true);
        dialog.setLocationRelativeTo(trainTableScrollPane);
        dialog.updateColumns(trainTable);
        dialog.setVisible(true);
        dialog.dispose();
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
        List<TableColumn> list = new LinkedList<TableColumn>();
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            list.add(tc);
        }
        Collections.sort(list, new Comparator<TableColumn>() {

            @Override
            public int compare(TableColumn o1, TableColumn o2) {
                return Integer.valueOf(Integer.valueOf(o1.getModelIndex()).compareTo(Integer.valueOf(o2.getModelIndex())));
            }
        });
        for (TableColumn tc : list) {
            tcm.removeColumn(tc);
        }
        for (TableColumn tc : list) {
            tcm.addColumn(tc);
        }
    }

    public void setModel(final ApplicationModel model) {
        this.model = model;
        this.train = model.getSelectedTrain();
        this.updateView();
        this.model.addListener(this);
        ((TrainTableModel) trainTable.getModel()).setModel(model);
        model.getMediator().addColleague(new Colleague() {
            public void receiveMessage(Object message) {
                IntervalSelectionMessage ism = (IntervalSelectionMessage) message;
                if (ism.getInterval() != null) {
                    TimeInterval interval = ism.getInterval();
                    Train train = interval.getTrain();
                    if (train.getTimeIntervalBefore() == interval) {
                        interval = train.getFirstInterval();
                    } else if (train.getTimeIntervalAfter() == interval) {
                        interval = train.getLastInterval();
                    }
                    int row = interval.getTrain().getTimeIntervalList().indexOf(interval);
                    int column = TrainTableColumn.getIndex(trainTable.getColumnModel(), interval.isNodeOwner() ? TrainTableColumn.STOP : TrainTableColumn.SPEED_LIMIT);
                    trainTable.setRowSelectionInterval(row, row);
                    if (column != -1)
                        trainTable.setColumnSelectionInterval(column, column);
                    Rectangle rect = trainTable.getCellRect(row, 0, true);
                    trainTable.scrollRectToVisible(rect);
                    trainTable.requestFocus();
                }
            }
        }, IntervalSelectionMessage.class);
        model.getMediator().addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainEvent(TrainEvent event) {
                if (event.getSource() == model.getSelectedTrain()) {
                    updateView();
                }
            }
        }, GTEvent.class);
    }


    @Override
    public void modelChanged(ApplicationModelEvent event) {
        if (event.getType() == ApplicationModelEventType.SELECTED_TRAIN_CHANGED || event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
            this.train = model.getSelectedTrain();
            this.updateView();
        }
    }

    private void updateView() {
        if (train == null) {
            trainTextField.setText(null);
            speedTextField.setText(null);
            techTimeTextField.setText(null);
            speedTextField.setEnabled(false);
        } else {
            // train type
            String name = train.getCompleteName();
            TextTemplate routeTemplate = (TextTemplate) train.getAttribute(Train.ATTR_ROUTE);
            if (routeTemplate != null)
                name = String.format("%s (%s)", name, routeTemplate.evaluate(train));
            trainTextField.setText(name);
            speedTextField.setText(Integer.toString(train.getTopSpeed()));
            techTimeTextField.setText(this.createTechTimeString(train));
            speedTextField.setEnabled(true);
        }

        trainTable.removeEditor();
        ((TrainTableModel)trainTable.getModel()).setTrain(train);

        this.invalidate();
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
            if (builder.length() != 0)
                builder.append(", ");
            builder.append(overlap.getTrain().getName());
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
        ToolTipHeader header = new ToolTipHeader(trainTable.getColumnModel());
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
    public void saveToPreferences(AppPreferences prefs) {
        // get displayed columns and save theirs order
        prefs.remove("train.columns");
        TableColumnModel tcm = trainTable.getColumnModel();
        Enumeration<TableColumn> columns = tcm.getColumns();
        StringBuilder order = null;
        while (columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            if (order != null)
                order.append('|');
            else
                order = new StringBuilder();
            order.append(column.getModelIndex()).append(',').append(column.getPreferredWidth());
        }
        if (order != null) {
            prefs.setString("train.columns", order.toString());
        }
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        // set displayed columns (if the prefs are empty - show all)
        String cs = prefs.getString("train.columns", null);
        List<TableColumn> shownColumns = new LinkedList<TableColumn>();
        if (cs == null || "".equals(cs)) {
            // all columns
            for (TrainTableColumn c : TrainTableColumn.values()) {
                shownColumns.add(c.createTableColumn());
            }
        } else {
            // extract
            String[] splitted = cs.split("\\|");
            for (String cStr : splitted) {
                try {
                    String[] ss = cStr.split(",");
                    int cInt = Integer.parseInt(ss[0]);
                    TableColumn ac = TrainTableColumn.getColumn(cInt).createTableColumn();
                    if (ss.length > 1) {
                        int wInt = Integer.parseInt(ss[1]);
                        if (wInt != 0) {
                            ac.setPreferredWidth(wInt);
                        }
                    }
                    shownColumns.add(ac);
                } catch (NumberFormatException e) {
                    LOG.warn("Cannot load columns order for train view: {}", cStr);
                }
            }
        }
        // append columns to table
        TableColumnModel tcm = trainTable.getColumnModel();
        for (TableColumn column : shownColumns) {
            tcm.addColumn(column);
        }
    }
}
