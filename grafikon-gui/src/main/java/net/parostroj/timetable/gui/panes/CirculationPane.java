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

import org.ini4j.Ini;

import net.parostroj.timetable.gui.*;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.components.TrainColorChooser;
import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialog;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.GTEventType;
import net.parostroj.timetable.model.events.TrainDiagramEvent;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.Tuple;

/**
 * Editing of circulations.
 *
 * @author jub
 */
public class CirculationPane extends javax.swing.JPanel implements StorableGuiData {

    private String type;
    private TrainDiagram diagram;
    private TCDelegate delegate;

    /** Creates new form CirculationView */
    public CirculationPane() {
        initComponents();
        createButton.setEnabled(false);
    }

    private void initComponents() {
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        typesComboBox = new javax.swing.JComboBox();
        newNameTextField = new javax.swing.JTextField();
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        trainsCyclesPane = new net.parostroj.timetable.gui.panes.TrainsCyclesPane();

        setLayout(new java.awt.BorderLayout());

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        typesComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmm");
        typesComboBox.addItemListener(new java.awt.event.ItemListener() {
            @Override
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                typesComboBoxItemStateChanged(evt);
            }
        });
        controlPanel.add(typesComboBox);

        newNameTextField.setColumns(15);
        newNameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                createButton.setEnabled(ObjectsUtil.checkAndTrim(newNameTextField.getText()) != null);
            }
        });
        controlPanel.add(newNameTextField);

        createButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });
        controlPanel.add(createButton);

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        controlPanel.add(deleteButton);

        add(controlPanel, java.awt.BorderLayout.PAGE_START);
        add(trainsCyclesPane, java.awt.BorderLayout.CENTER);
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String name = newNameTextField.getText();
        if (!delegate.getTrainDiagram().getCycleTypeNames().contains(name) && !TrainsCycleType.isDefaultType(name)) {
            TrainsCycleType type = new TrainsCycleType(UUID.randomUUID().toString(), name);
            diagram.addCyclesType(type);
        }
        newNameTextField.setText("");
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // test if empty
        if (diagram.getCycles(delegate.getType()).isEmpty()) {
            diagram.removeCyclesType(delegate.getType());
        }
    }

    private void typesComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            // select circulation type
            String selectedItem = ((TrainsCycleType) ((Wrapper<?>) typesComboBox.getSelectedItem()).getElement())
                    .getName();
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
    }

    public void setModel(ApplicationModel model) {
        this.delegate = new TCDelegate(model) {

            private TCDetailsViewDialog editDialog;

            @Override
            public String getTrainCycleErrors(TrainsCycle cycle) {
                StringBuilder result = new StringBuilder();
                List<Tuple<TrainsCycleItem>> conflicts = cycle.checkConflicts();
                for (Tuple<TrainsCycleItem> item : conflicts) {
                    if (item.first.getToInterval().getOwnerAsNode() != item.second.getFromInterval().getOwnerAsNode()) {
                        if (result.length() != 0) {
                            result.append('\n');
                        }
                        result.append(String.format(ResourceLoader.getString("ec.problem.nodes"), item.first.getTrain()
                                .getName(), item.first.getToInterval().getOwnerAsNode().getName(), item.second
                                .getTrain().getName(), item.second.getFromInterval().getOwnerAsNode().getName()));
                    } else if (item.first.getEndTime() >= item.second.getStartTime()) {
                        if (result.length() != 0) {
                            result.append('\n');
                        }
                        TimeConverter c = item.first.getTrain().getTrainDiagram().getTimeConverter();
                        result.append(String.format(ResourceLoader.getString("ec.problem.time"), item.first.getTrain()
                                .getName(), c.convertIntToText(item.first.getEndTime()), item.second.getTrain()
                                .getName(), c.convertIntToText(item.second.getStartTime())));
                    }
                }
                return result.toString();
            }

            @Override
            public void showEditDialog(JComponent component) {
                if (editDialog == null) {
                    editDialog = new TCDetailsViewDialog((java.awt.Window) component.getTopLevelAncestor(), true);
                }
                editDialog.setLocationRelativeTo(component);
                editDialog.updateValues(this);
                editDialog.setVisible(true);
            }

            @Override
            public String getCycleDescription() {
                return getSelectedCycle().getDescription();
            }

            @Override
            public boolean isOverlappingEnabled() {
                return true;
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public void handleEvent(Action action, TrainsCycle cycle, Train train) {
                if (action == Action.DIAGRAM_CHANGE) {
                    diagram = delegate.getTrainDiagram();
                    updateTypes();
                    this.fireEvent(Action.REFRESH, null);
                }
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
        model.getMediator().addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainDiagramEvent(TrainDiagramEvent event) {
                if (event.getType() == GTEventType.CYCLE_TYPE_ADDED) {
                    Wrapper<TrainsCycleType> wrapper = new Wrapper<TrainsCycleType>((TrainsCycleType) event.getObject());
                    typesComboBox.addItem(wrapper);
                    typesComboBox.setSelectedItem(wrapper);
                } else if (event.getType() == GTEventType.CYCLE_TYPE_REMOVED) {
                    typesComboBox.removeItem(new Wrapper<TrainsCycleType>((TrainsCycleType) event.getObject()));
                }
            }
        });
    }

    private void updateTypes() {
        typesComboBox.removeAllItems();
        if (diagram != null) {
            for (TrainsCycleType t : diagram.getCycleTypes()) {
                if (!TrainsCycleType.isDefaultType(t.getName())) {
                    typesComboBox.addItem(new Wrapper<TrainsCycleType>(t));
                }
            }
        }
        if (typesComboBox.getItemCount() > 0) {
            typesComboBox.setSelectedIndex(0);
        }
        deleteButton.setEnabled(type != null);
    }

    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextField newNameTextField;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane trainsCyclesPane;
    private javax.swing.JComboBox typesComboBox;

    @Override
    public Ini.Section saveToPreferences(Ini prefs) {
        return trainsCyclesPane.saveToPreferences(prefs);
    }

    @Override
    public Ini.Section loadFromPreferences(Ini prefs) {
        return trainsCyclesPane.loadFromPreferences(prefs);
    }
}
