/*
 * CirculationViewPanel.java
 *
 * Created on 22.6.2011, 13:45:33
 */
package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.*;

import net.parostroj.timetable.gui.actions.execution.ActionContext;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.SaveImageAction;
import net.parostroj.timetable.gui.dialogs.SaveImageDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.TrainsCycleType;
import net.parostroj.timetable.output2.gt.DrawUtils;

/**
 * Panel with circulation view and buttons.
 *
 * @author jub
 */
public class CirculationViewPanel extends javax.swing.JPanel {

    private static final int BASE_WIDTH_OFFSET = 5;
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

    public int getSizeSlider() {
        return sizeSlider.getValue();
    }

    public void setZoomSlider(int size) {
        zoomSlider.setValue(size);
    }

    public int getZoomSlider() {
        return zoomSlider.getValue();
    }

    private void enabledDisableSave() {
        saveButton.setEnabled(circulationView.getCount() > 0);
    }

    private void initComponents() {
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();
        circulationView = new net.parostroj.timetable.gui.components.CirculationView();

        setLayout(new java.awt.BorderLayout());

        add(buttonPanel, java.awt.BorderLayout.SOUTH);

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(circulationView);

        add(scrollPane, java.awt.BorderLayout.CENTER);

        sizeSlider = new LimitedSlider();
        sizeSlider.setWidthInChar(11);
        sizeSlider.setMajorTickSpacing(5);
        sizeSlider.setMaximum(10);
        sizeSlider.setMinorTickSpacing(1);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setSnapToTicks(true);
        sizeSlider.setValue(5);
        sizeSlider.setPaintLabels(true);

        circulationView.setStepWidth(this.computeWidth(sizeSlider.getValue()));

        zoomSlider = new LimitedSlider();
        zoomSlider.setWidthInChar(22);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setValue(5);
        zoomSlider.setMinorTickSpacing(1);
        zoomSlider.setMajorTickSpacing(5);
        zoomSlider.setMaximum(20);
        zoomSlider.setLabelTable(this.createDictionaryZoom(zoomSlider));
        zoomSlider.setPaintLabels(true);

        circulationView.setZoom(this.computeZoom(zoomSlider.getValue()));

        saveButton = new javax.swing.JButton();

        saveButton.setText(ResourceLoader.getString("gt.save")); // NOI18N
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        sizeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSliderStateChanged(evt);
            }
        });
        zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomSliderStateChanged(evt);
            }});
        GridBagLayout gbl_buttonPanel = new GridBagLayout();
        buttonPanel.setLayout(gbl_buttonPanel);
        typeComboBox = new javax.swing.JComboBox();

        typeComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmmmmm");
        typeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typeComboBoxItemStateChanged(evt);
            }
        });
        GridBagConstraints gbc_typeComboBox = new GridBagConstraints();
        gbc_typeComboBox.anchor = GridBagConstraints.WEST;
        gbc_typeComboBox.insets = new Insets(0, 5, 0, 5);
        gbc_typeComboBox.gridx = 0;
        gbc_typeComboBox.gridy = 0;
        buttonPanel.add(typeComboBox, gbc_typeComboBox);

        JLabel wLabel = new JLabel(ResourceLoader.getString("circulation.view.panel.width") + ":"); // NOI18N
        GridBagConstraints gbc_wLabel = new GridBagConstraints();
        gbc_wLabel.insets = new Insets(0, 0, 0, 5);
        gbc_wLabel.gridx = 1;
        gbc_wLabel.gridy = 0;
        buttonPanel.add(wLabel, gbc_wLabel);
        GridBagConstraints gbc_sizeSlider = new GridBagConstraints();
        gbc_sizeSlider.insets = new Insets(0, 0, 0, 5);
        gbc_sizeSlider.gridx = 2;
        gbc_sizeSlider.gridy = 0;
        buttonPanel.add(sizeSlider, gbc_sizeSlider);

        JLabel zLabel = new JLabel(ResourceLoader.getString("circulation.view.panel.zoom") + ":"); // NOI18N
        GridBagConstraints gbc_zLabel = new GridBagConstraints();
        gbc_zLabel.insets = new Insets(0, 0, 0, 5);
        gbc_zLabel.gridx = 3;
        gbc_zLabel.gridy = 0;
        buttonPanel.add(zLabel, gbc_zLabel);
        GridBagConstraints gbc_zoomSlider = new GridBagConstraints();
        gbc_zoomSlider.insets = new Insets(0, 0, 0, 5);
        gbc_zoomSlider.gridx = 4;
        gbc_zoomSlider.gridy = 0;
        buttonPanel.add(zoomSlider, gbc_zoomSlider);

        horizontalGlue = Box.createHorizontalGlue();
        GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
        gbc_horizontalGlue.insets = new Insets(0, 0, 0, 5);
        gbc_horizontalGlue.weightx = 1.0;
        gbc_horizontalGlue.fill = GridBagConstraints.HORIZONTAL;
        gbc_horizontalGlue.gridx = 5;
        gbc_horizontalGlue.gridy = 0;
        buttonPanel.add(horizontalGlue, gbc_horizontalGlue);
        GridBagConstraints gbc_saveButton = new GridBagConstraints();
        gbc_saveButton.insets = new Insets(0, 0, 0, 5);
        gbc_saveButton.gridx = 6;
        gbc_saveButton.gridy = 0;
        buttonPanel.add(saveButton, gbc_saveButton);
    }

    private Dictionary<?, ?> createDictionaryZoom(JSlider slider) {
        Hashtable<?, ?> map = slider.createStandardLabels(5);
        for (int i = 0; i <= 4; i++) {
            ((JLabel) map.get(i * 5)).setText(Float.toString(0.5f + i * 0.5f));
        }
        return map;
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (dialog == null)
            dialog = new SaveImageDialog((Dialog) this.getTopLevelAncestor(), true);
        dialog.setLocationRelativeTo(this.getParent());
        dialog.setSaveSize(circulationView.getPreferredSize());
        dialog.setSizeChangeEnabled(false);
        dialog.setVisible(true);
        if (!dialog.isSave()) {
            return;
        }
        ActionContext actionContext = new ActionContext(GuiComponentUtils.getTopLevelComponent(this));
        SaveImageAction action = new SaveImageAction(actionContext, dialog, circulationView);
        ActionHandler.getInstance().execute(action);
    }

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.DESELECTED && typeComboBox.getSelectedItem() == null) {
            circulationView.setType(null);
        } else if (evt.getStateChange() == ItemEvent.SELECTED) {
            circulationView.setType((TrainsCycleType) ((Wrapper<?>) typeComboBox.getSelectedItem()).getElement());
        }
        this.enabledDisableSave();
    }

    private void sizeSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        if (!sizeSlider.getValueIsAdjusting()) {
            circulationView.setStepWidth(computeWidth(sizeSlider.getValue()));
        }
    }

    private void zoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        if (!sizeSlider.getValueIsAdjusting()) {
            circulationView.setZoom(this.computeZoom(zoomSlider.getValue()));
        }
    }

    private int computeWidth(int sliderValue) {
        return sliderValue + BASE_WIDTH_OFFSET;
    }

    private float computeZoom(int sliderValue) {
        return 0.5f + 0.1f * sliderValue;
    }

    private net.parostroj.timetable.gui.components.CirculationView circulationView;
    private javax.swing.JButton saveButton;
    private LimitedSlider sizeSlider;
    private javax.swing.JComboBox typeComboBox;
    private LimitedSlider zoomSlider;
    private Component horizontalGlue;

    static class LimitedSlider extends javax.swing.JSlider {

        private int widthInChar = 10;

        public void setWidthInChar(int widthInChar) {
            this.widthInChar = widthInChar;
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            Graphics2D g = (Graphics2D) this.getGraphics();
            if (g != null) {
                int w = DrawUtils.getStringWidth(g, "M") * widthInChar;
                d.width = w;
            }
            return d;
        }
    }
}
