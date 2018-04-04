/*
 * TrainsWithConflictsPanel.java
 *
 * Created on 22.12.2009, 19:19:29
 */
package net.parostroj.timetable.gui.components;

import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ListSelectionListener;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Train;

/**
 * Panel for showing trains with conflicts.
 *
 * @author jub
 */
public class TrainsWithConflictsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

	private final WrapperListModel<Train> listModel = new WrapperListModel<Train>();

    /** Creates new form TrainsWithConflictsPanel */
    public TrainsWithConflictsPanel() {
        initComponents();
        listModel.initializeSet();
    }

    private Train getSelectedTrain() {
        Wrapper<?> selectedValue = trainsList.getSelectedValue();
        return selectedValue != null ? (Train) selectedValue.getElement() : null;
    }

    private void addTrainToList(Train train) {
        // do nothing if the list already contains the train
        if (listModel.getSetOfObjects().contains(train))
            return;
        // add to list
        listModel.addWrapper(new Wrapper<Train>(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, train.getDiagram().getTrainsData().getTrainComparator())));
    }

    public void updateSelectedTrain(Train train) {
        Train selectedTrain = this.getSelectedTrain();
        if (train == selectedTrain)
            return;
        else if (train == null) {
            trainsList.getSelectionModel().clearSelection();
        } else {
            int index = listModel.getIndexOfObject(train);
            if (index >= 0) {
                trainsList.setSelectedIndex(index);
                trainsList.scrollRectToVisible(trainsList.getCellBounds(index, index));
            } else
                trainsList.getSelectionModel().clearSelection();
        }
    }

    public void updateAllTrains(Iterable<Train> trains) {
        listModel.clear();
        if (trains != null) {
            for (Train train : trains) {
                if (train.isConflicting()) {
                    this.addTrainToList(train);
                }
            }
        }
    }

    public void updateTrain(Train train) {
        if (train.isConflicting()) {
            this.addTrainToList(train);
            for (Train t : train.getConflictingTrains()) {
                this.addTrainToList(t);
            }
        }
        this.checkTrains();
    }

    public void refreshTrain(Train train) {
        listModel.refreshObject(train);
    }

    public void removeTrain(Train train) {
        listModel.removeObject(train);
        this.checkTrains();
    }

    private void checkTrains() {
        Set<Train> removed = null;
        for (Train train : listModel.getSetOfObjects()) {
            if (!train.isConflicting()) {
                if (removed == null)
                    removed = new HashSet<Train>();
                removed.add(train);
            }
        }
        if (removed != null) {
            for (Train t : removed)
                listModel.removeObject(t);
        }
    }

    public void addTrainSelectionListener(ListSelectionListener listener) {
        trainsList.addListSelectionListener(listener);
    }

    public void removeTrainSelectionListener(ListSelectionListener listener) {
        trainsList.removeListSelectionListener(listener);
    }

    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        trainsList = new javax.swing.JList<Wrapper<Train>>();

        setLayout(new java.awt.BorderLayout());

        trainsList.setModel(listModel);
        trainsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(trainsList);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JList<Wrapper<Train>> trainsList;
}
