package net.parostroj.timetable.gui.components;

import java.util.List;

import javax.swing.event.ListSelectionListener;

import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

/**
 * Panel for showing trains with zero weights.
 *
 * @author jub
 */
public class TrainsWithZeroWeightsPanel extends javax.swing.JPanel {

    private final WrapperListModel<Train> listModel = new WrapperListModel<Train>();
    private TrainComparator trainComparator;

    /** Creates new form TrainsWithConflictsPanel */
    public TrainsWithZeroWeightsPanel() {
        initComponents();
        listModel.initializeSet();
    }

    private Train getSelectedTrain() {
        Wrapper<?> selectedValue = (Wrapper<?>) trainsList.getSelectedValue();
        return selectedValue != null ? (Train) selectedValue.getElement() : null;
    }

    private void addTrainToList(Train train) {
        // do nothing if the list already contains the train
        if (listModel.getSetOfObjects().contains(train))
            return;
        // add to list
        listModel.addWrapper(new Wrapper<Train>(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, trainComparator)));
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

    public void setTrainComparator(TrainComparator trainComparator) {
        this.trainComparator = trainComparator;
    }

    public void updateAllTrains(List<Train> trains) {
        listModel.clear();
        if (trains != null) {
            for (Train train : trains) {
                if (this.hasZeroWeight(train)) {
                    this.addTrainToList(train);
                }
            }
        }
    }

    public void removeAllTrains() {
        listModel.clear();
    }

    public void updateTrain(Train train) {
        if (this.hasZeroWeight(train)) {
            this.addTrainToList(train);
        } else {
            this.removeTrain(train);
        }
    }

    public void refreshTrain(Train train) {
        listModel.refreshObject(train);
    }

    public void removeTrain(Train train) {
        listModel.removeObject(train);
    }

    private boolean hasZeroWeight(Train train) {
        for (TimeInterval i : train.getTimeIntervalList()) {
            if (i.isLineOwner()) {
                Integer weight = TrainsHelper.getWeight(i);
                if (weight != null && weight.intValue() == 0)
                    return true;
            }
        }
        return false;
    }

    public void addTrainSelectionListener(ListSelectionListener listener) {
        trainsList.addListSelectionListener(listener);
    }

    public void removeTrainSelectionListener(ListSelectionListener listener) {
        trainsList.removeListSelectionListener(listener);
    }

    private void initComponents() {
        scrollPane = new javax.swing.JScrollPane();
        trainsList = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        trainsList.setModel(listModel);
        trainsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(trainsList);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JList trainsList;
}
