package net.parostroj.timetable.gui.components;

import javax.swing.*;

import net.parostroj.timetable.actions.TrainsHelper;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.Train;

import java.util.function.Consumer;

/**
 * Panel for showing trains with zero weights.
 *
 * @author jub
 */
public class TrainsWithZeroWeightsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

	private final WrapperListModel<Train> listModel = new WrapperListModel<>();
    private boolean selection;

    /** Creates new form TrainsWithConflictsPanel */
    public TrainsWithZeroWeightsPanel(Consumer<Train> trainSelection) {
        initComponents();
        listModel.initializeSet();
        trainsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JList<?> list = (JList<?>) e.getSource();
                Wrapper<?> wrapper = (Wrapper<?>)list.getSelectedValue();
                if (!selection && wrapper != null) {
                    trainSelection.accept((Train) wrapper.getElement());
                }
            }
        });
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
        listModel.addWrapper(new Wrapper<>(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, train.getDiagram().getTrainsData().getTrainComparator())));
    }

    public void updateSelectedTrain(Train train) {
        selection = true;
        try {
            Train selectedTrain = this.getSelectedTrain();
            if (train == selectedTrain) {
                return;
            }
            if (train == null) {
                trainsList.getSelectionModel().clearSelection();
            } else {
                int index = listModel.getIndexOfObject(train);
                if (index >= 0) {
                    trainsList.setSelectedIndex(index);
                    trainsList.scrollRectToVisible(trainsList.getCellBounds(index, index));
                } else
                    trainsList.getSelectionModel().clearSelection();
            }
        } finally {
            selection = false;
        }
    }

    public void updateAllTrains(Iterable<Train> trains) {
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
        for (TimeInterval i : train.getLineIntervals()) {
            Integer weight = TrainsHelper.getWeight(i);
            if (weight != null && weight == 0) {
                return true;
            }
        }
        return false;
    }

    private void initComponents() {
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        trainsList = new javax.swing.JList<>();

        setLayout(new java.awt.BorderLayout());

        trainsList.setModel(listModel);
        trainsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(trainsList);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    private javax.swing.JList<Wrapper<Train>> trainsList;
}
