/*
 * CirculationViewPanel.java
 *
 * Created on 22.6.2011, 13:45:33
 */
package net.parostroj.timetable.gui.components;

import java.awt.Dialog;
import java.awt.event.ItemEvent;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ActionUtils;
import net.parostroj.timetable.gui.actions.execution.SaveImageAction;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;

/**
 * Panel with circulation view and buttons.
 *
 * @author jub
 */
public class CirculationViewPanel extends javax.swing.JPanel {

    private SaveImageDialog dialog;

    /** Creates new form CirculationViewPanel */
    public CirculationViewPanel() {
        initComponents();
    }

    private void updateListOfTypes(TrainDiagram diagram) {
        typeComboBox.removeAllItems();
        if (diagram != null) {
            for (TrainsCycleType type : diagram.getCycleTypes()) {
                typeComboBox.addItem(new Wrapper<TrainsCycleType>(type));
            }
        }
    }

    public void setDiagram(TrainDiagram diagram) {
        saveButton.setEnabled(diagram != null);
        circulationView.setDiagram(diagram);
        this.updateListOfTypes(diagram);
        this.enabledDisableSave();
    }

    public void circulationRemoved(TrainsCycle circulation) {
        circulationView.circulationRemoved(circulation);
        this.enabledDisableSave();
    }

    public void circulationAdded(TrainsCycle circulation) {
        circulationView.circulationAdded(circulation);
        this.enabledDisableSave();
    }

    public void circulationUpdated(TrainsCycle circulation) {
        circulationView.circulationUpdated(circulation);
    }

    public void typeAdded(TrainsCycleType type) {
        typeComboBox.addItem(new Wrapper<TrainsCycleType>(type));
    }

    public void typeRemoved(TrainsCycleType type) {
        typeComboBox.removeItem(new Wrapper<TrainsCycleType>(type));
    }

    public void timeLimitsUpdated() {
        circulationView.timeLimitsUpdated();
    }

    public void setSizeSlider(int size) {
        sizeSlider.setValue(size);
    }

    public int geSizeSlider() {
        return sizeSlider.getValue();
    }

    private void enabledDisableSave() {
        saveButton.setEnabled(circulationView.getCount() > 0);
    }

    private void initComponents() {
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        javax.swing.JPanel leftPanel = new javax.swing.JPanel();
        typeComboBox = new javax.swing.JComboBox();
        sizeSlider = new javax.swing.JSlider();
        javax.swing.JPanel rightPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        circulationView = new net.parostroj.timetable.gui.components.CirculationView();

        setLayout(new java.awt.BorderLayout());

        buttonPanel.setLayout(new java.awt.BorderLayout());

        leftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        typeComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmmmmm");
        typeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typeComboBoxItemStateChanged(evt);
            }
        });
        leftPanel.add(typeComboBox);

        sizeSlider.setMajorTickSpacing(5);
        sizeSlider.setMaximum(10);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setValue(5);
        sizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSliderStateChanged(evt);
            }
        });
        leftPanel.add(sizeSlider);

        buttonPanel.add(leftPanel, java.awt.BorderLayout.CENTER);

        saveButton.setText(ResourceLoader.getString("gt.save")); // NOI18N
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        rightPanel.add(saveButton);

        buttonPanel.add(rightPanel, java.awt.BorderLayout.EAST);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(circulationView);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (dialog == null)
            dialog = new SaveImageDialog((Dialog)this.getTopLevelAncestor(), true);
        dialog.setLocationRelativeTo(this.getParent());
        dialog.setSaveSize(circulationView.getPreferredSize());
        dialog.setSizeChangeEnabled(false);
        dialog.setVisible(true);
        if (!dialog.isSave()) {
            return;
        }
        ActionContext actionContext = new ActionContext(ActionUtils.getTopLevelComponent(this));
        SaveImageAction action = new SaveImageAction(actionContext, dialog, circulationView);
        ActionHandler.getInstance().execute(action);
    }

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.DESELECTED && typeComboBox.getSelectedItem() == null) {
            circulationView.setType(null);
        } else if (evt.getStateChange() == ItemEvent.SELECTED){
            circulationView.setType((TrainsCycleType) ((Wrapper<?>) typeComboBox.getSelectedItem()).getElement());
        }
        this.enabledDisableSave();
    }

    private void sizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        if (!sizeSlider.getValueIsAdjusting()) {
            circulationView.setStepWidth(sizeSlider.getValue());
        }
    }

    private net.parostroj.timetable.gui.components.CirculationView circulationView;
    private javax.swing.JButton saveButton;
    private javax.swing.JSlider sizeSlider;
    private javax.swing.JComboBox typeComboBox;
}
