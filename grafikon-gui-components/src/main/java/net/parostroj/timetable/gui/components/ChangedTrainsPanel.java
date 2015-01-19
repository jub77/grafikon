package net.parostroj.timetable.gui.components;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.TrainWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Train;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Panel for showing changed trains.
 *
 * @author jub
 */
public class ChangedTrainsPanel extends javax.swing.JPanel {

    private final WrapperListModel<Train> listModel = new WrapperListModel<Train>(false);
    private long limit = 0;

    /** Creates new form TrainsWithConflictsPanel */
    public ChangedTrainsPanel() {
        initComponents();
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
        scrollPane = new javax.swing.JScrollPane();
        trainsList = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        trainsList.setModel(listModel);
        trainsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(trainsList);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        JButton clearButton = new JButton(ResourceLoader.getString("eventsviewer.button.clear"));
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                listModel.clear();
            }
        });
        buttonPanel.add(clearButton);

        limitTextField = new javax.swing.JFormattedTextField();
        limitTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        limitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        limitTextField.setColumns(4);
        limitTextField.setValue(0l);
        buttonPanel.add(limitTextField);

        JButton limitButton = new JButton(ResourceLoader.getString("eventsviewer.button.limit"));
        limitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Long value = (Long) limitTextField.getValue();
                if (value >= 0) {
                    limit = value;
                    limitTrains(limit);
                } else {
                    limit = 0;
                    limitTextField.setValue(0l);
                }
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

    public void addTrainSelectionListener(ListSelectionListener listSelectionListener) {
        trainsList.addListSelectionListener(listSelectionListener);
    }



    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JList trainsList;
    private javax.swing.JFormattedTextField limitTextField;

}
