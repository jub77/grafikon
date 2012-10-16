package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import net.parostroj.timetable.actions.GroupRemoval;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.IdGenerator;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/**
 * Dialog for editing groups.
 *
 * @author jub
 */
public class GroupsDialog extends JDialog {

    private final JTextField groupNameTextField;
    private final JButton newButton;
    private final JButton deleteButton;
    private final JList list;

    private TrainDiagram diagram;
    private final WrapperListModel<Group> groupsModel;

    public GroupsDialog(Frame parent, boolean modal) {
        super(parent, modal);

        JPanel controlPanel = new JPanel();
        getContentPane().add(controlPanel, BorderLayout.EAST);
        controlPanel.setLayout(new BorderLayout(0, 0));

        JPanel handlePanel = new JPanel();
        handlePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        controlPanel.add(handlePanel, BorderLayout.NORTH);
        handlePanel.setLayout(new GridLayout(0, 1, 0, 5));

        groupNameTextField = new JTextField();
        groupNameTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                updateButtons();
            }
        });
        handlePanel.add(groupNameTextField);
        groupNameTextField.setColumns(10);

        newButton = new JButton(ResourceLoader.getString("button.new"));
        newButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createGroup();
            }
        });
        handlePanel.add(newButton);

        deleteButton = new JButton(ResourceLoader.getString("button.delete"));
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteGroup();
            }
        });
        handlePanel.add(deleteButton);

        JPanel okPanel = new JPanel();
        okPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        controlPanel.add(okPanel, BorderLayout.SOUTH);
        okPanel.setLayout(new GridLayout(0, 1, 0, 5));

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        okPanel.add(okButton);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 0));
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        list = new JList();
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateButtons();
                }
            }
        });
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane.setViewportView(list);

        groupsModel = new WrapperListModel<Group>(true);
        list.setModel(groupsModel);

        pack();
    }

    @Override
    public void setVisible(boolean b) {
        if (b)
            updateButtons();
        super.setVisible(b);
    }

    public void showDialog(TrainDiagram diagram) {
        this.diagram = diagram;
        this.fillList();
        this.setVisible(true);
    }

    private void fillList() {
        for (Group group : diagram.getGroups()) {
            groupsModel.addWrapper(new Wrapper<Group>(group));
        }
    }

    private void updateButtons() {
        boolean selected = list.getSelectedValue() != null;
        deleteButton.setEnabled(selected);
        // create button
        newButton.setEnabled(!"".equals(groupNameTextField.getText().trim()));
    }

    private void createGroup() {
        Group group = diagram.createGroup(IdGenerator.getInstance().getId());
        group.setName(groupNameTextField.getText().trim());
        diagram.addGroup(group);
        groupNameTextField.setText("");
        Wrapper<Group> wrapper = new Wrapper<Group>(group);
        groupsModel.addWrapper(wrapper);
        list.setSelectedValue(wrapper, true);
        this.updateButtons();
    }

    private void deleteGroup() {
        int index = list.getSelectedIndex();
        if (index != -1) {
            Wrapper<Group> wrapper = groupsModel.removeIndex(index);
            list.setSelectedIndex(index < groupsModel.getSize() ? index : groupsModel.getSize() - 1);
            GroupRemoval removal = new GroupRemoval(diagram);
            removal.removeGroup(wrapper.getElement());
        }
    }

    private void ok() {
        this.setVisible(false);
    }
}
