/*
 * CirculationView.java
 *
 * Created on 29.8.2011, 13:39:21
 */
package net.parostroj.timetable.gui.panes;

import java.awt.Color;
import java.awt.event.ItemEvent;

import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.components.ChangeDocumentListener;
import net.parostroj.timetable.gui.dialogs.EditLocalizedStringOkCancelDialog;
import net.parostroj.timetable.gui.dialogs.TCDetailsViewDialog;
import net.parostroj.timetable.gui.ini.IniConfig;
import net.parostroj.timetable.gui.ini.IniConfigSection;
import net.parostroj.timetable.gui.ini.StorableGuiData;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.mediator.GTEventsReceiverColleague;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.events.Event;
import net.parostroj.timetable.utils.IdGenerator;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Editing of circulations.
 *
 * @author jub
 */
public class CirculationPane extends javax.swing.JPanel implements StorableGuiData {

    private static final long serialVersionUID = 1L;

	private transient TrainsCycleType type;
    private transient TrainDiagram diagram;
    private transient TCDelegate delegate;

    /** Creates new form CirculationView */
    public CirculationPane() {
        initComponents();
        createButton.setEnabled(false);
    }

    private void initComponents() {
        javax.swing.JPanel controlPanel = new javax.swing.JPanel();
        typesComboBox = new javax.swing.JComboBox<>(new CPModel());
        newNameTextField = new javax.swing.JTextField();
        createButton = GuiComponentUtils.createButton(GuiIcon.ADD, 2);
        deleteButton = GuiComponentUtils.createButton(GuiIcon.REMOVE, 2);
        editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        trainsCyclesPane = new net.parostroj.timetable.gui.panes.TrainsCyclesPane();
        trainsCyclesPane.setKey("cycles.custom");

        setLayout(new java.awt.BorderLayout());

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        typesComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmm"));
        typesComboBox.addItemListener(this::typesComboBoxItemStateChanged);
        controlPanel.add(typesComboBox);

        newNameTextField.setColumns(15);
        newNameTextField.getDocument().addDocumentListener(new ChangeDocumentListener() {
            @Override
            protected void change() {
                createButton.setEnabled(ObjectsUtil.checkAndTrim(newNameTextField.getText()) != null);
            }
        });
        controlPanel.add(newNameTextField);

        createButton.addActionListener(this::createButtonActionPerformed);
        controlPanel.add(createButton);

        editButton.addActionListener(this::editButtonActionPerformed);
        controlPanel.add(editButton);

        deleteButton.addActionListener(this::deleteButtonActionPerformed);
        controlPanel.add(deleteButton);

        add(controlPanel, java.awt.BorderLayout.PAGE_START);
        add(trainsCyclesPane, java.awt.BorderLayout.CENTER);
    }

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String name = newNameTextField.getText();
        if (!TrainsCycleType.isDefaultType(name)) {
            TrainsCycleType tcType = new TrainsCycleType(IdGenerator.getInstance().getId(), diagram);
            tcType.setName(LocalizedString.fromString(name));
            tcType.setKey(name);
            diagram.getCycleTypes().add(tcType);
        }
        newNameTextField.setText("");
    }

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // test if empty
        if (delegate.getType().getCycles().isEmpty()) {
            diagram.getCycleTypes().remove(delegate.getType());
        }
    }

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        EditLocalizedStringOkCancelDialog dialog = new EditLocalizedStringOkCancelDialog(GuiComponentUtils.getWindow(this));
        dialog.setLocationRelativeTo(this);
        LocalizedString name = dialog.edit(type.getName(), diagram.getLocales());

        if (name != null) {
            type.setName(name);
            type.setKey(name.getDefaultString());
            ((CPModel) typesComboBox.getModel()).refreshSelected();
        }
    }

    private void typesComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            // select circulation type
            TrainsCycleType selectedItem = (TrainsCycleType) ((Wrapper<?>) Objects.requireNonNull(typesComboBox.getSelectedItem())).getElement();
            TrainsCycleType oldType = type;
            type = selectedItem;
            if (oldType == null || !oldType.equals(type)) {
                // update view
                this.delegate.fireEvent(TCDelegate.Action.REFRESH, null);
            }
        } else {
            if (typesComboBox.getSelectedItem() == null && type != null) {
                type = null;
                this.delegate.fireEvent(TCDelegate.Action.REFRESH, null);
            }
        }
        deleteButton.setEnabled(type != null);
        editButton.setEnabled(type != null);
    }

    public void setModel(ApplicationModel model) {
        this.delegate = new TCDelegate(model) {

            private TCDetailsViewDialog editDialog;

            @Override
            public void showEditDialog(JComponent component) {
                if (editDialog == null) {
                    editDialog = new TCDetailsViewDialog(GuiComponentUtils.getWindow(component), true);
                }
                editDialog.setLocationRelativeTo(component);
                editDialog.updateValues(this, diagram);
                editDialog.setVisible(true);
            }

            @Override
            public TrainsCycleType getType() {
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
        trainsCyclesPane.setModel(this.delegate, interval -> {
            if (!interval.getTrain().isCovered(type, interval)) {
                return Color.black;
            } else {
                return Color.gray;
            }
        });
        model.getMediator().addColleague(new GTEventsReceiverColleague() {
            @Override
            public void processTrainDiagramEvent(Event event) {
                if (event.getType() == Event.Type.ADDED && event.getObject() instanceof TrainsCycleType) {
                    Wrapper<TrainsCycleType> wrapper = new Wrapper<>((TrainsCycleType) event.getObject());
                    typesComboBox.addItem(wrapper);
                    typesComboBox.setSelectedItem(wrapper);
                } else if (event.getType() == Event.Type.REMOVED && event.getObject() instanceof TrainsCycleType) {
                    typesComboBox.removeItem(new Wrapper<>((TrainsCycleType) event.getObject()));
                }
            }
        });
    }

    private void updateTypes() {
        typesComboBox.removeAllItems();
        if (diagram != null) {
            for (TrainsCycleType t : diagram.getCycleTypes()) {
                if (!TrainsCycleType.isDefaultType(t)) {
                    typesComboBox.addItem(new Wrapper<>(t));
                }
            }
        }
        if (typesComboBox.getItemCount() > 0) {
            typesComboBox.setSelectedIndex(0);
        }
        deleteButton.setEnabled(type != null);
        editButton.setEnabled(type != null);
    }

    private javax.swing.JButton createButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JTextField newNameTextField;
    private net.parostroj.timetable.gui.panes.TrainsCyclesPane trainsCyclesPane;
    private javax.swing.JComboBox<Wrapper<TrainsCycleType>> typesComboBox;

    @Override
    public IniConfigSection saveToPreferences(IniConfig prefs) {
        return trainsCyclesPane.saveToPreferences(prefs);
    }

    @Override
    public IniConfigSection loadFromPreferences(IniConfig prefs) {
        return trainsCyclesPane.loadFromPreferences(prefs);
    }

    private static class CPModel extends DefaultComboBoxModel<Wrapper<TrainsCycleType>> {

        private static final long serialVersionUID = 1L;

		public void refreshSelected() {
            int index = getIndexOf(getSelectedItem());
            fireContentsChanged(this, index, index);
        }
    }
}
