package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.wrappers.Wrapper;
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

    private static final Logger log = LoggerFactory.getLogger(TCItemChangeDialog.class);

    private final javax.swing.JComboBox<Wrapper<TimeInterval>> fromComboBox;
    private final javax.swing.JComboBox<Wrapper<TimeInterval>> toComboBox;
    private boolean ok;
    private final JTextField commentTextField;
    private JTextField setupTimeTextField;

    public TCItemChangeDialog() {
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
        GridBagLayout gbl_panel = new GridBagLayout();
        panel.setLayout(gbl_panel);

        JLabel fromLabel = new JLabel(ResourceLoader.getString("from.node"));
        GridBagConstraints gbc_fromLabel = new GridBagConstraints();
        gbc_fromLabel.gridx = 0;
        gbc_fromLabel.gridy = 0;
        gbc_fromLabel.insets = new Insets(0, 0, 5, 5);
        gbc_fromLabel.anchor = GridBagConstraints.WEST;
        panel.add(fromLabel, gbc_fromLabel);

        fromComboBox = new javax.swing.JComboBox<Wrapper<TimeInterval>>();
        fromComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmm"));

        GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
        gbc_fromComboBox.gridx = 1;
        gbc_fromComboBox.gridy = 0;
        gbc_fromComboBox.anchor = GridBagConstraints.WEST;
        gbc_fromComboBox.insets = new Insets(0, 0, 5, 5);
        panel.add(fromComboBox, gbc_fromComboBox);

        JLabel toLabel = new JLabel(ResourceLoader.getString("to.node"));
        GridBagConstraints gbc_toLabel = new GridBagConstraints();
        gbc_toLabel.gridx = 2;
        gbc_toLabel.gridy = 0;
        gbc_toLabel.insets = new Insets(0, 0, 5, 5);
        gbc_toLabel.anchor = GridBagConstraints.WEST;
        panel.add(toLabel, gbc_toLabel);
        toComboBox = new javax.swing.JComboBox<Wrapper<TimeInterval>>();
        toComboBox.setPrototypeDisplayValue(Wrapper.getPrototypeWrapper("mmmmmmmmmmmmmmm"));
        GridBagConstraints gbc_toComboBox = new GridBagConstraints();
        gbc_toComboBox.gridx = 3;
        gbc_toComboBox.gridy = 0;
        gbc_toComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_toComboBox.anchor = GridBagConstraints.WEST;
        panel.add(toComboBox, gbc_toComboBox);

        JLabel noteLabel = new JLabel(ResourceLoader.getString("ec.list.comment"));
        GridBagConstraints gbc_noteLabel = new GridBagConstraints();
        gbc_noteLabel.anchor = GridBagConstraints.WEST;
        gbc_noteLabel.insets = new Insets(0, 0, 5, 5);
        gbc_noteLabel.gridx = 0;
        gbc_noteLabel.gridy = 1;
        panel.add(noteLabel, gbc_noteLabel);

        commentTextField = new JTextField();
        GridBagConstraints gbc_noteTextField = new GridBagConstraints();
        gbc_noteTextField.insets = new Insets(0, 0, 5, 0);
        gbc_noteTextField.weightx = 1.0;
        gbc_noteTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_noteTextField.gridwidth = 3;
        gbc_noteTextField.gridx = 1;
        gbc_noteTextField.gridy = 1;
        panel.add(commentTextField, gbc_noteTextField);
        commentTextField.setColumns(50);

        JLabel setupTimeLabel = new JLabel(ResourceLoader.getString("ec.list.setup.time"));
        GridBagConstraints gbc_setupTimeLabel = new GridBagConstraints();
        gbc_setupTimeLabel.anchor = GridBagConstraints.WEST;
        gbc_setupTimeLabel.insets = new Insets(0, 0, 5, 5);
        gbc_setupTimeLabel.gridx = 0;
        gbc_setupTimeLabel.gridy = 2;
        panel.add(setupTimeLabel, gbc_setupTimeLabel);

        setupTimeTextField = new JTextField();
        GridBagConstraints gbc_setupTimeTextField = new GridBagConstraints();
        gbc_setupTimeTextField.weightx = 1.0;
        gbc_setupTimeTextField.gridwidth = 3;
        gbc_setupTimeTextField.insets = new Insets(0, 0, 5, 0);
        gbc_setupTimeTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_setupTimeTextField.gridx = 1;
        gbc_setupTimeTextField.gridy = 2;
        panel.add(setupTimeTextField, gbc_setupTimeTextField);
        setupTimeTextField.setColumns(10);
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
        commentTextField.setText(item.getComment());
        Integer setupTime = item.getSetupTime();
        setupTimeTextField.setText(setupTime == null ? null : Integer.toString(setupTime / TimeInterval.MINUTE));
    }

    private void updateFromTo(List<TimeInterval> intervals, TimeInterval from, TimeInterval to) {
        fromComboBox.removeAllItems();
        toComboBox.removeAllItems();
        for (TimeInterval interval : intervals) {
            if (interval.isNodeOwner()) {
                Wrapper<TimeInterval> w = new Wrapper<TimeInterval>(interval);
                fromComboBox.addItem(w);
                toComboBox.addItem(w);
            }
        }
        fromComboBox.setSelectedItem(new Wrapper<TimeInterval>(from));
        toComboBox.setSelectedItem(new Wrapper<TimeInterval>(to));
    }

    public TimeInterval getFrom() {
        return (TimeInterval) ((Wrapper<?>)fromComboBox.getSelectedItem()).getElement();
    }

    public TimeInterval getTo() {
        return (TimeInterval) ((Wrapper<?>)toComboBox.getSelectedItem()).getElement();
    }

    public String getComment() {
        return commentTextField.getText();
    }

    public Integer getSetupTime() {
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
