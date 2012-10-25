package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.components.GroupsComboBox;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.TrainDiagram;
import javax.swing.JLabel;

public class GroupChooserFromToDialog extends JDialog {

    private final GroupsComboBox fromGroupsComboBox;
    private final GroupsComboBox toGroupsComboBox;
    private boolean ok;

    /**
     * Create the dialog.
     */
    public GroupChooserFromToDialog() {
        this.setModal(true);
        this.setTitle(ResourceLoader.getString("groups.title"));
        getContentPane().setLayout(new BorderLayout());
        FlowLayout fl_contentPanel = new FlowLayout();
        fl_contentPanel.setAlignment(FlowLayout.LEFT);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(fl_contentPanel);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        fromGroupsComboBox = new GroupsComboBox(false);
        contentPanel.add(fromGroupsComboBox);

        JLabel label = new JLabel("->");
        contentPanel.add(label);

        toGroupsComboBox = new GroupsComboBox(false);
        contentPanel.add(toGroupsComboBox);
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok = true;
                setVisible(false);
            }
        });
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok = false;
                setVisible(false);
            }
        });
        buttonPane.add(cancelButton);
        pack();
    }

    public void showDialog(TrainDiagram fromDiagram, Group fromGroup, TrainDiagram toDiagram, Group toGroup) {
        this.fillCombo(fromDiagram, fromGroup, toDiagram, toGroup);
        this.pack();
        this.setVisible(true);
    }

    private void fillCombo(TrainDiagram fromDiagram, Group fromGroup, TrainDiagram toDiagram, Group toGroup) {
        fromGroupsComboBox.updateGroups(fromDiagram, fromGroup);
        toGroupsComboBox.updateGroups(toDiagram, toGroup);
    }

    public boolean isSelected() {
        return ok;
    }

    public Group getSelectedFrom() {
        return fromGroupsComboBox.getGroupSelection().getGroup();
    }

    public Group getSelectedTo() {
        return toGroupsComboBox.getGroupSelection().getGroup();
    }
}
