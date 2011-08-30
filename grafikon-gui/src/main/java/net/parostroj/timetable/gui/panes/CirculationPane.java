/*
 * CirculationView.java
 *
 * Created on 29.8.2011, 13:39:21
 */
package net.parostroj.timetable.gui.panes;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.TrainColorChooser;
import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialog;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.TimeConverter;
import net.parostroj.timetable.utils.Tuple;

/**
 * Editing of circulations.
 *
 * @author jub
 */
public class CirculationPane extends javax.swing.JPanel implements StorableGuiData {

    private TrainsCycle selected;
    private String type;
    private TCDetailsViewDialog editDialog;
    private TrainDiagram diagram;
    private TCDelegate delegate;

    /** Creates new form CirculationView */
    public CirculationPane() {
        initComponents();
        createButton.setEnabled(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        typesComboBox = new javax.swing.JComboBox();
        newNameTextField = new javax.swing.JTextField();
        createButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        trainsCyclesPane = new net.parostroj.timetable.gui.panes.TrainsCyclesPane();

        setLayout(new java.awt.BorderLayout());

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        typesComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmm");
        typesComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typesComboBoxItemStateChanged(evt);
            }
        });
        controlPanel.add(typesComboBox);

        newNameTextField.setColumns(15);
        newNameTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                newNameTextFieldCaretUpdate(evt);
            }
        });
        controlPanel.add(newNameTextField);

        createButton.setText(ResourceLoader.getString("button.new")); // NOI18N
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        controlPanel.add(createButton);

        deleteButton.setText(ResourceLoader.getString("button.delete")); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        controlPanel.add(deleteButton);

        add(controlPanel, java.awt.BorderLayout.PAGE_START);
        add(trainsCyclesPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
        String name = newNameTextField.getText();
        if (!delegate.getTrainDiagram().getCyclesTypes().contains(name) && !TrainsCycleType.isDefaultType(name)) {
            diagram.addCyclesType(new TrainsCycleType(UUID.randomUUID().toString(), name));
            typesComboBox.addItem(name);
            typesComboBox.setSelectedItem(name);
        }
        newNameTextField.setText("");
    }//GEN-LAST:event_createButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // test if empty
        if (diagram.getCycles(delegate.getType()).isEmpty()) {
            diagram.removeCyclesType(delegate.getType());
            typesComboBox.removeItem(delegate.getType());
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void newNameTextFieldCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_newNameTextFieldCaretUpdate
        createButton.setEnabled(newNameTextField.getText().length() != 0);
    }//GEN-LAST:event_newNameTextFieldCaretUpdate

    private void typesComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_typesComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            // select circulation type
            String selectedItem = (String)typesComboBox.getSelectedItem();
            String oldType = type;
            type = selectedItem;
            if (oldType == null || !oldType.equals(type)) {
                // update view
                this.delegate.fireEvent(TCDelegate.Action.REFRESH, null);
            }
        } else {
            if (typesComboBox.getSelectedItem() == null) {
                if (type != null) {
                    type = null;
                    this.delegate.fireEvent(TCDelegate.Action.REFRESH, null);
                }
            }
        }
        deleteButton.setEnabled(type != null);
    }//GEN-LAST:event_typesComboBoxItemStateChanged

    public void setModel(ApplicationModel model) {
        model.addListener(new ApplicationModelListener() {

            @Override
            public void modelChanged(ApplicationModelEvent event) {
                if (event.getType() == ApplicationModelEventType.SET_DIAGRAM_CHANGED) {
                    diagram = event.getModel().getDiagram();
                    updateTypes();
                }
            }
        });
        
        this.delegate = new TCDelegate(model) {

            @Override
            public String getTrainCycleErrors(TrainsCycle cycle) {
                StringBuilder result = new StringBuilder();
                List<Tuple<TrainsCycleItem>> conflicts = cycle.checkConflicts();
                for (Tuple<TrainsCycleItem> item : conflicts) {
                    if (item.first.getToInterval().getOwnerAsNode() != item.second.getFromInterval().getOwnerAsNode()) {
                        if (result.length() != 0) {
                            result.append('\n');
                        }
                        result.append(String.format(ResourceLoader.getString("ec.problem.nodes"), item.first.getTrain().getName(), item.first.getToInterval().getOwnerAsNode().getName(), item.second.getTrain().getName(), item.second.getFromInterval().getOwnerAsNode().getName()));
                    } else if (item.first.getEndTime() >= item.second.getStartTime()) {
                        if (result.length() != 0) {
                            result.append('\n');
                        }
                        result.append(String.format(ResourceLoader.getString("ec.problem.time"), item.first.getTrain().getName(), TimeConverter.convertFromIntToText(item.first.getEndTime()), item.second.getTrain().getName(), TimeConverter.convertFromIntToText(item.second.getStartTime())));
                    }
                }
                return result.toString();
            }

            @Override
            public void showEditDialog(JComponent component) {
                if (editDialog == null) {
                    editDialog = new TCDetailsViewDialog((java.awt.Frame) component.getTopLevelAncestor(), true);
                }
                editDialog.setLocationRelativeTo(component);
                editDialog.updateValues(this);
                editDialog.setVisible(true);
            }

            @Override
            public String getCycleDescription() {
                if (selected != null) {
                    return selected.getDescription();
                } else {
                    return null;
                }
            }

            @Override
            public boolean isOverlappingEnabled() {
                return true;
            }

            @Override
            public String getType() {
                return type;
            }
            
            public void handleEvent(Action action, TrainsCycle cycle, Train train) {
                if (action == Action.REFRESH)
                    type = null;
            }
        };
        trainsCyclesPane.setModel(this.delegate, new TrainColorChooser() {

            @Override
            public Color getIntervalColor(TimeInterval interval) {
                if (!interval.getTrain().isCovered(type, interval)) {
                    return Color.black;
                } else {
                    return Color.gray;
                }
            }
        });
    }
    
    private void updateTypes() {
        typesComboBox.removeAllItems();
        if (diagram != null) {
            for (String t : diagram.getCyclesTypes()) {
                if (!TrainsCycleType.isDefaultType(t))
                typesComboBox.addItem(t);
            }
        }
        if (typesComboBox.getItemCount() > 0) {
            typesComboBox.setSelectedIndex(0);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField newNameTextField;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane trainsCyclesPane;
    private javax.swing.JComboBox typesComboBox;
    // End of variables declaration//GEN-END:variables

    @Override
    public void saveToPreferences(AppPreferences prefs) {
        trainsCyclesPane.saveToPreferences(prefs);
    }

    @Override
    public void loadFromPreferences(AppPreferences prefs) {
        trainsCyclesPane.loadFromPreferences(prefs);
    }
}
