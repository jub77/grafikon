/*
 * TCListView.java
 *
 * Created on 12. září 2007, 13:35
 */
package net.parostroj.timetable.gui.views;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
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

    private static final long serialVersionUID = 1L;

	private transient TCDelegate delegate;

    private final WrapperListModel<TrainsCycle> cycles;

    /** Creates new form ECListView */
    public TCListView() {
        setLayout(new BorderLayout(0, 0));
        cycles = new WrapperListModel<>(true);
        cyclesList = new javax.swing.JList<>(cycles);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        cyclesList.setPrototypeCellValue(Wrapper.getPrototypeWrapper("mmmmmmmmm"));
        cyclesList.setVisibleRowCount(3);
        scrollPane.setViewportView(cyclesList);
        cyclesList.addListSelectionListener(this);

        javax.swing.JPanel panel = new javax.swing.JPanel();
        add(panel, BorderLayout.SOUTH);
        GridBagLayout gblPanel = new GridBagLayout();
        panel.setLayout(gblPanel);
        newNameTextField = new javax.swing.JTextField();
        GridBagConstraints gbcNewNameTextField = new GridBagConstraints();
        gbcNewNameTextField.weightx = 1.0;
        gbcNewNameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbcNewNameTextField.insets = new Insets(3, 0, 0, 5);
        gbcNewNameTextField.gridx = 0;
        gbcNewNameTextField.gridy = 0;
        panel.add(newNameTextField, gbcNewNameTextField);

        newNameTextField.setColumns(7);
        newNameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                updateButtonStatus();
            }
        });
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        GridBagConstraints gbcCreateButton = new GridBagConstraints();
        gbcCreateButton.fill = GridBagConstraints.BOTH;
        gbcCreateButton.insets = new Insets(3, 0, 0, 5);
        gbcCreateButton.gridx = 1;
        gbcCreateButton.gridy = 0;
        panel.add(createButton, gbcCreateButton);

        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        GridBagConstraints gbcEditButton = new GridBagConstraints();
        gbcEditButton.fill = GridBagConstraints.BOTH;
        gbcEditButton.insets = new Insets(3, 0, 0, 5);
        gbcEditButton.gridx = 2;
        gbcEditButton.gridy = 0;
        panel.add(editButton, gbcEditButton);

        sequenceButton = GuiComponentUtils.createButton(GuiIcon.VIEW_SORT, 2);
        GridBagConstraints gbcSeqB = new GridBagConstraints();
        gbcSeqB.fill = GridBagConstraints.BOTH;
        gbcSeqB.insets = new Insets(3, 0, 0, 5);
        gbcSeqB.gridx = 3;
        gbcSeqB.gridy = 0;
        panel.add(sequenceButton, gbcSeqB);

        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        GridBagConstraints gbcDeleteButton = new GridBagConstraints();
        gbcDeleteButton.insets = new Insets(3, 0, 0, 0);
        gbcDeleteButton.fill = GridBagConstraints.BOTH;
        gbcDeleteButton.gridx = 4;
        gbcDeleteButton.gridy = 0;
        panel.add(deleteButton, gbcDeleteButton);

        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this::deleteButtonActionPerformed);

        createButton.setEnabled(false);
        createButton.addActionListener(this::createButtonActionPerformed);

        editButton.setEnabled(false);
        editButton.addActionListener(e -> {
            if (delegate.getSelectedCycle() != null) {
                delegate.showEditDialog(editButton);
            }
        });

        cyclesList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e) && delegate.getSelectedCycle() != null) {
                    delegate.showEditDialog(editButton);
                }
            }
        });
        cyclesList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && delegate.getSelectedCycle() != null) {
                    delegate.showEditDialog(editButton);
                }
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
            for (TrainsCycle cycle : delegate.getType().getCycles()) {
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
        List<TrainsCycle> toBeDeleted = new ArrayList<>(selectedIndices.length);
        for (int selectedIndex : selectedIndices) {
            TrainsCycle cycle = cycles.getIndex(selectedIndex).getElement();
            toBeDeleted.add(cycle);
        }
        for (TrainsCycle cycle : toBeDeleted) {
            if (cycle != null) {
                // remove from diagram
                cycle.getType().getCycles().remove(cycle);
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
            cycle.getType().getCycles().add(cycle);

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
