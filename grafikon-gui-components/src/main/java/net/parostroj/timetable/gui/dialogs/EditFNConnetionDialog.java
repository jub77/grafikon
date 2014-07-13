package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.components.ElementSelectionPanel;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.FNConnection;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;

public class EditFNConnetionDialog extends javax.swing.JDialog {

    private final ElementSelectionPanel<Node> selectionPanel;
    private FNConnection connection;

    public EditFNConnetionDialog(Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        getContentPane().setLayout(new BorderLayout(0, 0));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Wrapper<Node>> selectedList = selectionPanel.getSelectedList();
                List<Node> res = Wrapper.unwrap(selectedList);
                if (res.isEmpty()) {
                    res = null;
                }
                connection.set(FNConnection.ATTR_LAST_NODES, res);
                setVisible(false);
            }
        });
        panel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        panel.add(cancelButton);

        JPanel editPanel = new JPanel();
        editPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(editPanel, BorderLayout.CENTER);
        editPanel.setLayout(new BorderLayout(0, 0));

        selectionPanel = new ElementSelectionPanel<Node>();
        editPanel.add(selectionPanel, BorderLayout.CENTER);
    }

    public void edit(Component center, FNConnection connection, TrainDiagram diagram) {
        this.connection = connection;

        // add nodes and data
        selectionPanel.setListForSelection(Wrapper.getWrapperList(diagram.getNet().getNodes()));
        List<?> lastNodes = connection.get(FNConnection.ATTR_LAST_NODES, List.class);
        if (lastNodes != null) {
            for (Object node : lastNodes) {
                selectionPanel.addSelected(Wrapper.getWrapper((Node) node));
            }
        }

        pack();
        setLocationRelativeTo(center);
        setVisible(true);
    }

}
