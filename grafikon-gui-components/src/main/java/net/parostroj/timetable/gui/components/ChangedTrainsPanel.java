package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Train;

import javax.swing.*;

import java.awt.BorderLayout;

import java.util.function.Consumer;

/**
 * Panel for showing changed trains.
 *
 * @author jub
 */
public class ChangedTrainsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private final WrapperListModel<Train> listModel = new WrapperListModel<>(false);
    private long limit = 0;

    public ChangedTrainsPanel(Consumer<Train> trainSelection) {
        initComponents();
        trainsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                JList<?> list = (JList<?>) e.getSource();
                Wrapper<?> wrapper = (Wrapper<?>) list.getSelectedValue();
                if (wrapper != null) {
                    trainSelection.accept((Train) wrapper.getElement());
                }
            }
        });
    }

    public void clearTrainList() {
        listModel.clear();
    }

    public void addTrainToList(Train train) {
        // check if the last train is the same ...
        if (listModel.getSize() > 0) {
            int index = listModel.getSize() - 1;
            Train lastTrain = listModel.getIndex(index).getElement();
            if (lastTrain == train) {
                listModel.refreshIndex(index);
                return;
            }
        }
        limitTrains(limit);
        // add to list
        Wrapper<Train> wrapper = Wrapper.getWrapper(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, train.getDiagram().getTrainsData().getTrainComparator()));
        listModel.addWrapper(wrapper);
        trainsList.ensureIndexIsVisible(listModel.getSize() - 1);
    }

    private void initComponents() {
        JScrollPane scrollPane = new JScrollPane();
        trainsList = new javax.swing.JList<>();

        setLayout(new java.awt.BorderLayout());

        trainsList.setModel(listModel);
        trainsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(trainsList);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        JButton clearButton = new JButton(ResourceLoader.getString("eventsviewer.button.clear"));
        clearButton.addActionListener(e -> listModel.clear());
        buttonPanel.add(clearButton);

        limitTextField = new javax.swing.JFormattedTextField();
        limitTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        limitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        limitTextField.setColumns(4);
        limitTextField.setValue(0L);
        buttonPanel.add(limitTextField);

        JButton limitButton = new JButton(ResourceLoader.getString("eventsviewer.button.limit"));
        limitButton.addActionListener(e -> {
            Long value = (Long) limitTextField.getValue();
            if (value >= 0) {
                limit = value;
                limitTrains(limit);
            } else {
                limit = 0;
                limitTextField.setValue(0L);
            }
        });
        buttonPanel.add(limitButton);
    }

    private void limitTrains(long size) {
        if (size <= 0) {
            return;
        }
        if (listModel.getSize() > size) {
            long removed = listModel.getSize() - size;
            for (int i = 0; i < removed; i++) {
                listModel.removeIndex(0);
            }
        }
    }

    private javax.swing.JList<Wrapper<Train>> trainsList;
    private javax.swing.JFormattedTextField limitTextField;

}
