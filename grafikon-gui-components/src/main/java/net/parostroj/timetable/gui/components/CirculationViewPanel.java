/*
 * CirculationViewPanel.java
 *
 * Created on 22.6.2011, 13:45:33
 */
package net.parostroj.timetable.gui.components;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Comparator;
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

    private enum DrawType {
        NORMAL("circulation.view.panel.type.normal"),
        NO_BG("circulation.view.panel.type.nobg"),
        TRAIN_COLORS("circulation.view.panel.type.traincolors"),
        TRAIN_COLORS_NO_BG("circulation.view.panel.type.traincolors.nobg");

        private final String key;

        DrawType(String key) {
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
            diagram.getCycleTypes().stream()
                    .map(Wrapper::new)
                    .sorted(Comparator.comparing(Wrapper::toString))
                    .forEach(typeComboBox::addItem);
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
        this.updateCirculationsInfo();
        this.enabledDisableSave();
    }

    public void circulationAdded(TrainsCycle circulation) {
        circulationView.circulationAdded(circulation);
        this.updateCirculationsInfo();
        this.enabledDisableSave();
    }

    public void circulationUpdated(TrainsCycle circulation) {
        circulationView.circulationUpdated(circulation);
        this.updateCirculationsInfo();
    }

    public void typeAdded(TrainsCycleType type) {
        typeComboBox.addItem(new Wrapper<>(type));
    }

    public void typeRemoved(TrainsCycleType type) {
        typeComboBox.removeItem(new Wrapper<>(type));
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

        infoTextField = new javax.swing.JTextField();
        infoTextField.setEditable(false);
        infoTextField.setColumns(15);

        drawTypeComboBox = new javax.swing.JComboBox<>();
        drawTypeComboBox.addItemListener(this::drawTypeComboBoxItemStateChanged);

        circulationView.setZoom(this.computeZoom(zoomSlider.getValue()));

        saveButton = new javax.swing.JButton();

        saveButton.setText(ResourceLoader.getString("gt.save")); // NOI18N
        saveButton.setEnabled(false);
        saveButton.addActionListener(this::saveButtonActionPerformed);
        sizeSlider.addChangeListener(this::sizeSliderStateChanged);
        zoomSlider.addChangeListener(this::zoomSliderStateChanged);
        GridBagLayout gblButtonPanel = new GridBagLayout();
        buttonPanel.setLayout(gblButtonPanel);
        typeComboBox = new javax.swing.JComboBox<>();

        typeComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmmmm"));
        typeComboBox.addItemListener(this::typeComboBoxItemStateChanged);
        GridBagConstraints gbcTypeComboBox = new GridBagConstraints();
        gbcTypeComboBox.anchor = GridBagConstraints.WEST;
        gbcTypeComboBox.insets = new Insets(0, 5, 0, 5);
        gbcTypeComboBox.gridx = 0;
        gbcTypeComboBox.gridy = 0;
        buttonPanel.add(typeComboBox, gbcTypeComboBox);

        JLabel wLabel = new JLabel(ResourceLoader.getString("circulation.view.panel.width") + ":"); // NOI18N
        GridBagConstraints gbcwLabel = new GridBagConstraints();
        gbcwLabel.insets = new Insets(0, 0, 0, 5);
        gbcwLabel.gridx = 1;
        gbcwLabel.gridy = 0;
        buttonPanel.add(wLabel, gbcwLabel);
        GridBagConstraints gbcSizeSlider = new GridBagConstraints();
        gbcSizeSlider.insets = new Insets(0, 0, 0, 5);
        gbcSizeSlider.gridx = 2;
        gbcSizeSlider.gridy = 0;
        buttonPanel.add(sizeSlider, gbcSizeSlider);

        JLabel zLabel = new JLabel(ResourceLoader.getString("circulation.view.panel.zoom") + ":"); // NOI18N
        GridBagConstraints gbczLabel = new GridBagConstraints();
        gbczLabel.insets = new Insets(0, 0, 0, 5);
        gbczLabel.gridx = 3;
        gbczLabel.gridy = 0;
        buttonPanel.add(zLabel, gbczLabel);

        GridBagConstraints gbcZoomSlider = new GridBagConstraints();
        gbcZoomSlider.insets = new Insets(0, 0, 0, 5);
        gbcZoomSlider.gridx = 4;
        gbcZoomSlider.gridy = 0;
        buttonPanel.add(zoomSlider, gbcZoomSlider);

        GridBagConstraints gbcDrawType = new GridBagConstraints();
        gbcDrawType.insets = new Insets(0, 0, 0, 5);
        gbcZoomSlider.gridx = 5;
        gbcZoomSlider.gridy = 0;
        buttonPanel.add(drawTypeComboBox, gbcDrawType);

        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.insets = new Insets(0, 10, 0, 5);
        gbcZoomSlider.gridx = 6;
        gbcZoomSlider.gridy = 0;
        buttonPanel.add(infoTextField, gbcInfo);

        Component horizontalGlue = Box.createHorizontalGlue();
        GridBagConstraints gbcHorizontalGlue = new GridBagConstraints();
        gbcHorizontalGlue.insets = new Insets(0, 0, 0, 5);
        gbcHorizontalGlue.weightx = 1.0;
        gbcHorizontalGlue.fill = GridBagConstraints.HORIZONTAL;
        gbcHorizontalGlue.gridx = 7;
        gbcHorizontalGlue.gridy = 0;
        buttonPanel.add(horizontalGlue, gbcHorizontalGlue);

        GridBagConstraints gbcSaveButton = new GridBagConstraints();
        gbcSaveButton.insets = new Insets(0, 0, 0, 5);
        gbcSaveButton.gridx = 8;
        gbcSaveButton.gridy = 0;
        buttonPanel.add(saveButton, gbcSaveButton);
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
            circulationView.setType(getCirculationType());
        }
        this.updateCirculationsInfo();
        this.enabledDisableSave();
    }

    private TrainsCycleType getCirculationType() {
        return (TrainsCycleType) ((Wrapper<?>) typeComboBox.getSelectedItem()).getElement();
    }

    private void updateCirculationsInfo() {
        Wrapper<?> wrapper = (Wrapper<?>) typeComboBox.getSelectedItem();
        infoTextField.setText(wrapper == null
                ? ""
                : String.format("%s: %d",
                        wrapper,
                        ((TrainsCycleType) wrapper.getElement()).getCycles().size()));
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
            case NO_BG: circulationView.setDrawColors(getDrawColors(false, false)); break;
            case TRAIN_COLORS: circulationView.setDrawColors(getDrawColors(true, true)); break;
            case TRAIN_COLORS_NO_BG: circulationView.setDrawColors(getDrawColors(true, false)); break;
            case NORMAL:
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
    private javax.swing.JTextField infoTextField;
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
                d.width = DrawUtils.getStringWidth(g, "M") * widthInChar;
            }
            return d;
        }
    }
}
