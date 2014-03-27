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

    /** Creates new form TrainsWithConflictsPanel */
    public ChangedTrainsPanel() {
        initComponents();
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
        // add to list
        Wrapper<Train> wrapper = Wrapper.getWrapper(train, new TrainWrapperDelegate(TrainWrapperDelegate.Type.NAME_AND_END_NODES_WITH_TIME, train.getTrainDiagram().getTrainsData().getTrainComparator()));
        listModel.addWrapper(wrapper);
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
    }

    public void addTrainSelectionListener(ListSelectionListener listSelectionListener) {
        trainsList.addListSelectionListener(listSelectionListener);
    }

    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JList trainsList;

}
