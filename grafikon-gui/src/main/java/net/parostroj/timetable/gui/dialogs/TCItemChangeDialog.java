package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.components.LocalizedStringField;
import net.parostroj.timetable.gui.pm.LocalizedStringPM;
import net.parostroj.timetable.gui.pm.LolizationEditResult;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Change dialog for TCItem.
 *
 * @author jub
 */
public class TCItemChangeDialog extends JDialog {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(TCItemChangeDialog.class);

    private final javax.swing.JComboBox<Wrapper<TimeInterval>> fromComboBox;
    private final javax.swing.JComboBox<Wrapper<TimeInterval>> toComboBox;
    private boolean ok;
    private final LocalizedStringField<LocalizedStringPM> commentTextField;
    private JTextField setupTimeTextField;

    public TCItemChangeDialog(boolean setupTime) {
        setModal(true);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 0, 5));
        getContentPane().add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton buttonOk = new JButton(ResourceLoader.getString("button.ok"));
        buttonOk.addActionListener(e -> {
            ok = true;
            setVisible(false);
        });
        buttonPanel.add(buttonOk);

        JButton buttonCancel = new JButton(ResourceLoader.getString("button.cancel"));
        buttonCancel.addActionListener(e -> setVisible(false));
        buttonPanel.add(buttonCancel);
        GridBagLayout gblPanel = new GridBagLayout();
        panel.setLayout(gblPanel);

        JLabel fromLabel = new JLabel(ResourceLoader.getString("from.node"));
        GridBagConstraints gbcFromLabel = new GridBagConstraints();
        gbcFromLabel.gridx = 0;
        gbcFromLabel.gridy = 0;
        gbcFromLabel.insets = new Insets(0, 0, 5, 5);
        gbcFromLabel.anchor = GridBagConstraints.WEST;
        panel.add(fromLabel, gbcFromLabel);

        fromComboBox = new javax.swing.JComboBox<>();
        fromComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmm"));

        GridBagConstraints gbcFromComboBox = new GridBagConstraints();
        gbcFromComboBox.gridx = 1;
        gbcFromComboBox.gridy = 0;
        gbcFromComboBox.anchor = GridBagConstraints.WEST;
        gbcFromComboBox.insets = new Insets(0, 0, 5, 5);
        panel.add(fromComboBox, gbcFromComboBox);

        JLabel toLabel = new JLabel(ResourceLoader.getString("to.node"));
        GridBagConstraints gbcToLabel = new GridBagConstraints();
        gbcToLabel.gridx = 2;
        gbcToLabel.gridy = 0;
        gbcToLabel.insets = new Insets(0, 0, 5, 5);
        gbcToLabel.anchor = GridBagConstraints.WEST;
        panel.add(toLabel, gbcToLabel);
        toComboBox = new javax.swing.JComboBox<>();
        toComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmm"));
        GridBagConstraints gbcToComboBox = new GridBagConstraints();
        gbcToComboBox.gridx = 3;
        gbcToComboBox.gridy = 0;
        gbcToComboBox.insets = new Insets(0, 0, 5, 0);
        gbcToComboBox.anchor = GridBagConstraints.WEST;
        panel.add(toComboBox, gbcToComboBox);

        JLabel noteLabel = new JLabel(ResourceLoader.getString("ec.list.comment"));
        GridBagConstraints gbcNoteLabel = new GridBagConstraints();
        gbcNoteLabel.anchor = GridBagConstraints.WEST;
        gbcNoteLabel.insets = new Insets(0, 0, 5, 5);
        gbcNoteLabel.gridx = 0;
        gbcNoteLabel.gridy = 1;
        panel.add(noteLabel, gbcNoteLabel);

        commentTextField = new LocalizedStringField<>();
        commentTextField.setPresentationModel(new LocalizedStringPM());
        GridBagConstraints gbcNoteTextField = new GridBagConstraints();
        gbcNoteTextField.insets = new Insets(0, 0, 5, 0);
        gbcNoteTextField.weightx = 1.0;
        gbcNoteTextField.fill = GridBagConstraints.HORIZONTAL;
        gbcNoteTextField.gridwidth = 3;
        gbcNoteTextField.gridx = 1;
        gbcNoteTextField.gridy = 1;
        panel.add(commentTextField, gbcNoteTextField);
        commentTextField.setColumns(50);

        if (setupTime) {
            JLabel setupTimeLabel = new JLabel(ResourceLoader.getString("ec.list.setup.time"));
            GridBagConstraints gbcSetupTimeLabel = new GridBagConstraints();
            gbcSetupTimeLabel.anchor = GridBagConstraints.WEST;
            gbcSetupTimeLabel.insets = new Insets(0, 0, 5, 5);
            gbcSetupTimeLabel.gridx = 0;
            gbcSetupTimeLabel.gridy = 2;
            panel.add(setupTimeLabel, gbcSetupTimeLabel);

            setupTimeTextField = new JTextField();
            GridBagConstraints gbcSetupTimeTextField = new GridBagConstraints();
            gbcSetupTimeTextField.weightx = 1.0;
            gbcSetupTimeTextField.gridwidth = 3;
            gbcSetupTimeTextField.insets = new Insets(0, 0, 5, 0);
            gbcSetupTimeTextField.fill = GridBagConstraints.HORIZONTAL;
            gbcSetupTimeTextField.gridx = 1;
            gbcSetupTimeTextField.gridy = 2;
            panel.add(setupTimeTextField, gbcSetupTimeTextField);
            setupTimeTextField.setColumns(10);
        }
    }

    public boolean showDialog(Component component, TrainsCycleItem tcItem) {
        this.ok = false;
        this.updateValues(tcItem);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(component);
        this.setVisible(true);
        return ok;
    }

    private void updateValues(TrainsCycleItem item) {
        this.updateFromTo(item.getTrain().getTimeIntervalList(), item.getFromInterval(), item.getToInterval());
        LocalizedString lComment = item.getComment();
        commentTextField.getPresentationModel().init(lComment == null ? LocalizedString.fromString("") : lComment,
                item.getCycle().getDiagram().getLocales());
        Integer setupTime = item.getSetupTime();
        if (setupTimeTextField != null) {
            setupTimeTextField.setText(setupTime == null ? null : Integer.toString(setupTime / TimeInterval.MINUTE));
        }
    }

    private void updateFromTo(List<TimeInterval> intervals, TimeInterval from, TimeInterval to) {
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        for (TimeInterval interval : intervals) {
            if (interval.isNodeOwner()) {
                Wrapper<TimeInterval> w = new Wrapper<>(interval);
                fromComboBox.addItem(w);
                toComboBox.addItem(w);
            }
        }
        fromComboBox.setSelectedItem(new Wrapper<>(from));
        toComboBox.setSelectedItem(new Wrapper<>(to));
    }

    public TimeInterval getFrom() {
        return (TimeInterval) ((Wrapper<?>) Objects.requireNonNull(fromComboBox.getSelectedItem())).getElement();
    }

    public TimeInterval getTo() {
        return (TimeInterval) ((Wrapper<?>) Objects.requireNonNull(toComboBox.getSelectedItem())).getElement();
    }

    public LocalizedString getComment() {
        LolizationEditResult edit = commentTextField.getPresentationModel().getCurrentEdit();
        return edit != null ? edit.get().getNullIfEmpty() : null;
    }

    public Integer getSetupTime() {
        if (setupTimeTextField == null) return null;
        String setupTimeStr = ObjectsUtil.checkAndTrim(setupTimeTextField.getText());
        if (setupTimeStr != null) {
            try {
                int setupTime = Integer.parseInt(setupTimeStr) * TimeInterval.MINUTE;
                return setupTime == 0 ? null : setupTime;
            } catch (NumberFormatException e) {
                log.warn("Error parsing int: {}", setupTimeStr);
            }
        }
        return null;
    }
}
