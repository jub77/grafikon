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
import net.parostroj.timetable.model.TextTemplate;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * View of train details.
 *
 * @author  jub
 */
public class TrainView extends javax.swing.JPanel implements ApplicationModelListener, StorableGuiData {

    private static final Logger LOG = LoggerFactory.getLogger(TrainView.class.getName());
    private ApplicationModel model;
    private Train train;
    private EditTrainDialog editDialog;

    /**
     * Creates new form TrainView.
     */
    public TrainView() {
        initComponents();

    }

    private EditTrainDialog getEditTrainDialog() {
        if (editDialog == null) {
            editDialog = new EditTrainDialog((java.awt.Frame)this.getTopLevelAncestor(), true);
        }
        return editDialog;
    }

    public void editColumns() {
        ColumnsDialog dialog = new ColumnsDialog((Frame)this.getTopLevelAncestor(), true);
        dialog.setLocationRelativeTo(trainTableScrollPane);
        dialog.updateColumns(trainTable);
        dialog.setVisible(true);
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

    public void setModel(ApplicationModel model) {
        this.model = model;
        this.train = model.getSelectedTrain();
        this.updateView();
        this.model.addListener(this);
        ((TrainTableModel)trainTable.getModel()).setModel(model);
        getEditTrainDialog().setModel(model);
        model.getMediator().addColleague(new Colleague() {
            public void receiveMessage(Object message) {
                IntervalSelectionMessage ism = (IntervalSelectionMessage) message;
                if (ism.getInterval() != null) {
                    TimeInterval interval = ism.getInterval();
                    int row = interval.getTrain().getTimeIntervalList().indexOf(interval);
                    int column = TrainTableColumn.getIndex(trainTable.getColumnModel(), interval.isNodeOwner() ? TrainTableColumn.STOP : TrainTableColumn.SPEED);
                    trainTable.setRowSelectionInterval(row, row);
                    if (column != -1)
                        trainTable.setColumnSelectionInterval(column, column);
                    Rectangle rect = trainTable.getCellRect(row, 0, true);
                    trainTable.scrollRectToVisible(rect);
                    trainTable.requestFocus();
                }
            }
        }, IntervalSelectionMessage.class);
    }


    @Override
    public void modelChanged(ApplicationModelEvent event) {
        if (event.getType() == ApplicationModelEventType.SELECTED_TRAIN_CHANGED || event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
            this.train = model.getSelectedTrain();
            this.updateView();
        } else if ((event.getType() == ApplicationModelEventType.MODIFIED_TRAIN_NAME_TYPE || event.getType() == ApplicationModelEventType.MODIFIED_TRAIN) && event.getObject() == model.getSelectedTrain()) {
            this.updateView();
        }
    }

    private void updateView() {
        if (train == null) {
            trainTextField.setText(null);
            speedTextField.setText(null);
            techTimeTextField.setText(null);
            speedTextField.setEnabled(false);

            editButton.setEnabled(false);
            copyButton.setEnabled(false);
        } else {
            // train type
            String name = train.getCompleteName();
            TextTemplate routeTemplate = (TextTemplate) train.getAttribute("route");
            if (routeTemplate != null)
                name = String.format("%s (%s)", name, routeTemplate.evaluate(train));
            trainTextField.setText(name);
            speedTextField.setText(Integer.toString(train.getTopSpeed()));
            techTimeTextField.setText(this.createTechTimeString(train));
            speedTextField.setEnabled(true);

            editButton.setEnabled(true);
            copyButton.setEnabled(true);
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        trainTextField = new javax.swing.JTextField();
        trainTableScrollPane = new javax.swing.JScrollPane();
        trainTable = new javax.swing.JTable();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        speedTextField = new javax.swing.JTextField();
        editButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        techTimeTextField = new javax.swing.JTextField();

        jLabel1.setText(ResourceLoader.getString("create.train.number")); // NOI18N

        trainTextField.setEditable(false);

        trainTable.setAutoCreateColumnsFromModel(false);
        trainTable.setModel(new TrainTableModel(model,train));
        trainTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        trainTableScrollPane.setViewportView(trainTable);

        jLabel2.setText(ResourceLoader.getString("create.train.speed")); // NOI18N

        speedTextField.setColumns(5);
        speedTextField.setEditable(false);

        editButton.setText(ResourceLoader.getString("button.edit")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        copyButton.setText(ResourceLoader.getString("button.copy")); // NOI18N
        copyButton.setEnabled(false);
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        jLabel3.setText(ResourceLoader.getString("create.train.technological.time")); // NOI18N

        techTimeTextField.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(1)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(trainTableScrollPane, Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(Alignment.LEADING, false)
                                .addComponent(jLabel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(jLabel3)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(techTimeTextField, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(copyButton)
                                    .addPreferredGap(ComponentPlacement.RELATED)
                                    .addComponent(editButton))
                                .addComponent(trainTextField, GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))))
                    .addGap(5))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(5)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(trainTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(2)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(editButton)
                        .addComponent(copyButton)
                        .addComponent(speedTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(techTimeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(2)
                    .addComponent(trainTableScrollPane, GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addGap(5))
        );
        this.setLayout(layout);
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        getEditTrainDialog().getSelectedTrainData();
        getEditTrainDialog().setLocationRelativeTo(this);
        getEditTrainDialog().setVisible(true);
    }//GEN-LAST:event_editButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        CopyTrainDialog dialog = new CopyTrainDialog((java.awt.Frame)this.getTopLevelAncestor(), true, model, train);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }//GEN-LAST:event_copyButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyButton;
    private javax.swing.JButton editButton;
    private javax.swing.JTextField speedTextField;
    private javax.swing.JTextField techTimeTextField;
    private javax.swing.JTable trainTable;
    private javax.swing.JScrollPane trainTableScrollPane;
    private javax.swing.JTextField trainTextField;
    // End of variables declaration//GEN-END:variables

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
