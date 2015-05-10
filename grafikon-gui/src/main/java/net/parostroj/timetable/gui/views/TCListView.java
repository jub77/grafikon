/*
 * TCListView.java
 *
 * Created on 12. září 2007, 13:35
 */
package net.parostroj.timetable.gui.views;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.dialogs.CirculationSequenceDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.TCDelegate.Action;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * List with all engine cycles and buttons for create and remove.
 *
 * @author jub
 */
public class TCListView extends javax.swing.JPanel implements TCDelegate.Listener, ListSelectionListener {

    private TCDelegate delegate;

    private final WrapperListModel<TrainsCycle> cycles;

    /** Creates new form ECListView */
    public TCListView() {
        setLayout(new BorderLayout(0, 0));
        cycles = new WrapperListModel<TrainsCycle>(true);
        cyclesList = new javax.swing.JList<Wrapper<TrainsCycle>>(cycles);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        cyclesList.setPrototypeCellValue(Wrapper.getPrototypeWrapper("mmmmmmmmm"));
        cyclesList.setVisibleRowCount(3);
        scrollPane.setViewportView(cyclesList);
        cyclesList.addListSelectionListener(this);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        add(panel, BorderLayout.SOUTH);
        GridBagLayout gbl_panel = new GridBagLayout();
        panel.setLayout(gbl_panel);
        newNameTextField = new javax.swing.JTextField();
        GridBagConstraints gbc_newNameTextField = new GridBagConstraints();
        gbc_newNameTextField.weightx = 1.0;
        gbc_newNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_newNameTextField.insets = new Insets(3, 0, 0, 5);
        gbc_newNameTextField.gridx = 0;
        gbc_newNameTextField.gridy = 0;
        panel.add(newNameTextField, gbc_newNameTextField);

        newNameTextField.setColumns(7);
        newNameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                updateButtonStatus();
            }
        });
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        GridBagConstraints gbc_createButton = new GridBagConstraints();
        gbc_createButton.fill = GridBagConstraints.BOTH;
        gbc_createButton.insets = new Insets(3, 0, 0, 5);
        gbc_createButton.gridx = 1;
        gbc_createButton.gridy = 0;
        panel.add(createButton, gbc_createButton);

        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        GridBagConstraints gbc_editButton = new GridBagConstraints();
        gbc_editButton.fill = GridBagConstraints.BOTH;
        gbc_editButton.insets = new Insets(3, 0, 0, 5);
        gbc_editButton.gridx = 2;
        gbc_editButton.gridy = 0;
        panel.add(editButton, gbc_editButton);

        sequenceButton = GuiComponentUtils.createButton(GuiIcon.VIEW_SORT, 2);
        GridBagConstraints gbcSeqB = new GridBagConstraints();
        gbcSeqB.fill = GridBagConstraints.BOTH;
        gbcSeqB.insets = new Insets(3, 0, 0, 5);
        gbcSeqB.gridx = 3;
        gbcSeqB.gridy = 0;
        panel.add(sequenceButton, gbcSeqB);

        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        GridBagConstraints gbc_deleteButton = new GridBagConstraints();
        gbc_deleteButton.insets = new Insets(3, 0, 0, 0);
        gbc_deleteButton.fill = GridBagConstraints.BOTH;
        gbc_deleteButton.gridx = 4;
        gbc_deleteButton.gridy = 0;
        panel.add(deleteButton, gbc_deleteButton);

        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteButtonActionPerformed(e));

        createButton.setEnabled(false);
        createButton.addActionListener(e -> createButtonActionPerformed(e));

        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            if (delegate.getSelectedCycle() != null) {
                delegate.showEditDialog(editButton);
            }
        });

        sequenceButton.setEnabled(false);
        sequenceButton.addActionListener(e -> {
            CirculationSequenceDialog dialog = new CirculationSequenceDialog(GuiComponentUtils
                    .getWindow(TCListView.this), ModalityType.APPLICATION_MODAL);
            dialog.setLocationRelativeTo(TCListView.this);
            dialog.showDialog(delegate.getSelectedCycle());
            dialog.dispose();
        });
    }

    public void setModel(TCDelegate delegate) {
        this.delegate = delegate;
        this.delegate.addListener(this);

        this.updateView();
    }

    @Override
    public void tcEvent(Action action, TrainsCycle cycle, Train train) {
        switch (action) {
            case REFRESH:
                this.updateView();
                break;
            case NEW_CYCLE:
            case DELETED_CYCLE:
                if (cycle.getType().equals(delegate.getType())) {
                    this.updateView();
                }
                break;
            case MODIFIED_CYCLE:
                cyclesList.repaint();
                break;
            case SELECTED_CHANGED:
                break;
            default:
                // nothing
        }
    }

    private void updateView() {
        cycles.clear();
        cyclesList.clearSelection();
        if (delegate.getTrainDiagram() != null && delegate.getType() != null) {
            // set list
            for (TrainsCycle cycle : delegate.getTrainDiagram().getCycles(delegate.getType())) {
                cycles.addWrapper(Wrapper.getWrapper(cycle));
            }
        }

        this.updateButtonStatus();
    }

    private void updateButtonStatus() {
        boolean status = delegate.getTrainDiagram() != null && delegate.getType() != null
                && newNameTextField.getText().trim().length() > 0;

        createButton.setEnabled(status);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        // set selected engine
        int[] selectedIndices = cyclesList.getSelectedIndices();
        boolean oneSelected = selectedIndices.length == 1;
        boolean atLeastOneSelected = selectedIndices.length > 0;
        if (oneSelected) {
            delegate.setSelectedCycle(cycles.getIndex(selectedIndices[0]).getElement());
        } else {
            delegate.setSelectedCycle(null);
        }
        deleteButton.setEnabled(atLeastOneSelected);
        editButton.setEnabled(oneSelected);
        sequenceButton.setEnabled(oneSelected);
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get selected cycles ...
        int[] selectedIndices = cyclesList.getSelectedIndices();
        List<TrainsCycle> toBeDeleted = new ArrayList<TrainsCycle>(selectedIndices.length);
        for (int selectedIndex : selectedIndices) {
            TrainsCycle cycle = cycles.getIndex(selectedIndex).getElement();
            toBeDeleted.add(cycle);
        }
        for (TrainsCycle cycle : toBeDeleted) {
            if (cycle != null) {
                // remove from diagram
                delegate.getTrainDiagram().removeCycle(cycle);
                // fire event
                delegate.fireEvent(TCDelegate.Action.DELETED_CYCLE, cycle);
            }
        }
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get name from text field (ignore shorter than one character
        if (newNameTextField.getText().length() > 0 && delegate.getType() != null) {
            TrainDiagram trainDiagram = delegate.getTrainDiagram();
            TrainsCycle cycle = new TrainsCycle(IdGenerator.getInstance().getId(), trainDiagram,
                    newNameTextField.getText(), null,
                    delegate.getType());
            trainDiagram.addCycle(cycle);

            // clear field
            newNameTextField.setText("");
            // fire event
            delegate.fireEvent(TCDelegate.Action.NEW_CYCLE, cycle);
            // set selected
            int index = cycles.getIndexOfObject(cycle);
            cyclesList.setSelectedIndex(index);
            cyclesList.ensureIndexIsVisible(index);
            delegate.fireEvent(TCDelegate.Action.SELECTED_CHANGED, cycle);
        }
    }

    private final javax.swing.JButton createButton;
    private final javax.swing.JButton deleteButton;
    private final javax.swing.JButton editButton;
    private final javax.swing.JButton sequenceButton;
    private final javax.swing.JList<Wrapper<TrainsCycle>> cyclesList;
    private final javax.swing.JTextField newNameTextField;
}
