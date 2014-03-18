package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.TimeInterval;
import net.parostroj.timetable.model.TrainsCycleItem;
import net.parostroj.timetable.utils.ResourceLoader;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Change dialog for TCItem.
 *
 * @author jub
 */
public class TCItemChangeDialog extends JDialog {

    private final javax.swing.JComboBox fromComboBox;
    private final javax.swing.JComboBox toComboBox;
    private boolean ok;
    private final JTextField commentTextField;

    public TCItemChangeDialog() {
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setModal(true);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 0, 5));
        getContentPane().add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton buttonOk = new JButton(ResourceLoader.getString("button.ok"));
        buttonOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok = true;
                setVisible(false);
            }
        });
        buttonPanel.add(buttonOk);

        JButton buttonCancel = new JButton(ResourceLoader.getString("button.cancel"));
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(buttonCancel);
        panel.setLayout(new GridBagLayout());

        JLabel fromLabel = new JLabel(ResourceLoader.getString("from.node"));
        GridBagConstraints gbc_fromLabel = new GridBagConstraints();
        gbc_fromLabel.insets = new Insets(0, 0, 5, 5);
        gbc_fromLabel.anchor = GridBagConstraints.WEST;
        panel.add(fromLabel, gbc_fromLabel);

        fromComboBox = new javax.swing.JComboBox();
        fromComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmmm");

        GridBagConstraints gbc_fromComboBox = new GridBagConstraints();
        gbc_fromComboBox.anchor = GridBagConstraints.WEST;
        gbc_fromComboBox.insets = new Insets(0, 0, 5, 5);
        panel.add(fromComboBox, gbc_fromComboBox);

        JLabel toLabel = new JLabel(ResourceLoader.getString("to.node"));
        GridBagConstraints gbc_toLabel = new GridBagConstraints();
        gbc_toLabel.insets = new Insets(0, 0, 5, 5);
        gbc_toLabel.anchor = GridBagConstraints.WEST;
        panel.add(toLabel, gbc_toLabel);
        toComboBox = new javax.swing.JComboBox();
        toComboBox.setPrototypeDisplayValue("mmmmmmmmmmmmmmm");
        GridBagConstraints gbc_toComboBox = new GridBagConstraints();
        gbc_toComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_toComboBox.anchor = GridBagConstraints.WEST;
        panel.add(toComboBox, gbc_toComboBox);

        JLabel noteLabel = new JLabel(ResourceLoader.getString("ec.list.comment"));
        GridBagConstraints gbc_noteLabel = new GridBagConstraints();
        gbc_noteLabel.anchor = GridBagConstraints.EAST;
        gbc_noteLabel.insets = new Insets(0, 0, 0, 5);
        gbc_noteLabel.gridx = 0;
        gbc_noteLabel.gridy = 1;
        panel.add(noteLabel, gbc_noteLabel);

        commentTextField = new JTextField();
        GridBagConstraints gbc_noteTextField = new GridBagConstraints();
        gbc_noteTextField.weightx = 1.0;
        gbc_noteTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_noteTextField.gridwidth = 3;
        gbc_noteTextField.gridx = 1;
        gbc_noteTextField.gridy = 1;
        panel.add(commentTextField, gbc_noteTextField);
        commentTextField.setColumns(50);
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
}
