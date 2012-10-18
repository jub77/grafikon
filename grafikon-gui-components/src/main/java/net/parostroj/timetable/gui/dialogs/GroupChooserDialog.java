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

public class GroupChooserDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final GroupsComboBox groupsComboBox;
    private boolean ok;

    /**
     * Create the dialog.
     */
    public GroupChooserDialog() {
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        FlowLayout fl_contentPanel = new FlowLayout();
        fl_contentPanel.setAlignment(FlowLayout.LEFT);
        contentPanel.setLayout(fl_contentPanel);
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        groupsComboBox = new GroupsComboBox(false);
        contentPanel.add(groupsComboBox);
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

    public void showDialog(TrainDiagram diagram, Group group) {
        this.fillCombo(diagram, group);
        this.pack();
        this.setVisible(true);
    }

    private void fillCombo(TrainDiagram diagram, Group group) {
        groupsComboBox.updateGroups(diagram, group);
    }

    public boolean isSelected() {
        return ok;
    }

    public Group getSelected() {
        return groupsComboBox.getGroupSelection().getGroup();
    }
}
