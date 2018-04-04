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
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.output2.gt.CirculationDraw;
import net.parostroj.timetable.output2.gt.CirculationDrawColors;
import net.parostroj.timetable.output2.gt.DrawUtils;

/**
 * Panel with circulation view and buttons.
 *
 * @author jub
 */
public class CirculationViewPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final int BASE_WIDTH_OFFSET = 5;
    private static final float BASE_ZOOM_OFFSET = 0.5f;
    private static final float ZOOM_SLIDER_RATIO = 0.1f;

    private SaveImageDialog dialog;

    private static enum DrawType {
        NORMAL("circulation.view.panel.type.normal"),
        NO_BG("circulation.view.panel.type.nobg"),
        TRAIN_COLORS("circulation.view.panel.type.traincolors"),
        TRAIN_COLORS_NO_BG("circulation.view.panel.type.traincolors.nobg");

        private String key;

        private DrawType(String key) {
            this.key = key;
        }

        @Override
        public String toString() {
            return ResourceLoader.getString(key);
        }

        public String toSaveString() {
            return name();
        }

        public static DrawType fromSaveString(String strType) {
            for (DrawType type : values()) {
                if (type.name().equals(strType)) {
                    return type;
                }
            }
            return NORMAL;
        }
    }

    /** Creates new form CirculationViewPanel */
    public CirculationViewPanel() {
        initComponents();
        for (DrawType td : DrawType.values()) {
            drawTypeComboBox.addItem(td);
        }
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

    public String getDrawType() {
        return ((DrawType) drawTypeComboBox.getSelectedItem()).toSaveString();
    }

    public void setDrawType(String drawType) {
        DrawType td = DrawType.fromSaveString(drawType);
        drawTypeComboBox.setSelectedItem(td);
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

        drawTypeComboBox = new javax.swing.JComboBox<DrawType>();
        drawTypeComboBox.addItemListener(event -> drawTypeComboBoxItemStateChanged(event));

        circulationView.setZoom(this.computeZoom(zoomSlider.getValue()));

        saveButton = new javax.swing.JButton();

        saveButton.setText(ResourceLoader.getString("gt.save")); // NOI18N
        saveButton.setEnabled(false);
        saveButton.addActionListener(evt -> saveButtonActionPerformed(evt));
        sizeSlider.addChangeListener(evt -> sizeSliderStateChanged(evt));
        zoomSlider.addChangeListener(evt -> zoomSliderStateChanged(evt));
        GridBagLayout gbl_buttonPanel = new GridBagLayout();
        buttonPanel.setLayout(gbl_buttonPanel);
        typeComboBox = new javax.swing.JComboBox<Wrapper<TrainsCycleType>>();

        typeComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmmmm"));
        typeComboBox.addItemListener(evt -> typeComboBoxItemStateChanged(evt));
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

        GridBagConstraints gbc_drawType = new GridBagConstraints();
        gbc_drawType.insets = new Insets(0, 0, 0, 5);
        gbc_zoomSlider.gridx = 5;
        gbc_zoomSlider.gridy = 0;
        buttonPanel.add(drawTypeComboBox, gbc_drawType);

        Component horizontalGlue = Box.createHorizontalGlue();
        GridBagConstraints gbc_horizontalGlue = new GridBagConstraints();
        gbc_horizontalGlue.insets = new Insets(0, 0, 0, 5);
        gbc_horizontalGlue.weightx = 1.0;
        gbc_horizontalGlue.fill = GridBagConstraints.HORIZONTAL;
        gbc_horizontalGlue.gridx = 6;
        gbc_horizontalGlue.gridy = 0;
        buttonPanel.add(horizontalGlue, gbc_horizontalGlue);

        GridBagConstraints gbc_saveButton = new GridBagConstraints();
        gbc_saveButton.insets = new Insets(0, 0, 0, 5);
        gbc_saveButton.gridx = 7;
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

    private void drawTypeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        DrawType dt = DrawType.NORMAL;
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            dt = (DrawType) drawTypeComboBox.getSelectedItem();
        }
        switch(dt) {
            case NORMAL: circulationView.setDrawColors(null); break;
            case NO_BG: circulationView.setDrawColors(getDrawColors(false, false)); break;
            case TRAIN_COLORS: circulationView.setDrawColors(getDrawColors(true, true)); break;
            case TRAIN_COLORS_NO_BG: circulationView.setDrawColors(getDrawColors(true, false)); break;
            default: circulationView.setDrawColors(null); break;
        }
    }

    private CirculationDrawColors getDrawColors(boolean trainColors, boolean background) {
        return (type, item) -> {
            Color color = null;
            // background
            if (!background) {
                switch (type) {
                    case CirculationDraw.COLOR_COLUMN_1:
                    case CirculationDraw.COLOR_COLUMN_2:
                        color = Color.WHITE;
                        break;
                    default:
                        // nothing
                }
            }
            if (trainColors && item != null && CirculationDraw.COLOR_FILL.equals(type)) {
                TrainType trainType = item.getTrain().getType();
                if (trainType != null) {
                    color = trainType.getColor();
                }
            }
            return color;
        };
    }

    private int computeWidth(int sliderValue) {
        return sliderValue + BASE_WIDTH_OFFSET;
    }

    private float computeZoom(int sliderValue) {
        return BASE_ZOOM_OFFSET + ZOOM_SLIDER_RATIO * sliderValue;
    }

    private net.parostroj.timetable.gui.components.CirculationView circulationView;
    private javax.swing.JButton saveButton;
    private LimitedSlider sizeSlider;
    private javax.swing.JComboBox<Wrapper<TrainsCycleType>> typeComboBox;
    private javax.swing.JComboBox<DrawType> drawTypeComboBox;
    private LimitedSlider zoomSlider;

    static class LimitedSlider extends javax.swing.JSlider {

        private static final long serialVersionUID = 1L;

		private static final int DEFAULT_SLIDER_WIDTH_IN_CHARS = 10;

        private int widthInChar = DEFAULT_SLIDER_WIDTH_IN_CHARS;

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
