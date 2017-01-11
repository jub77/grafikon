package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Panel about freight destinations.
 *
 * @author jub
 */
public class FreightDestinationPanel extends JPanel {

    private static final Wrapper<Node> empty = Wrapper.getEmptyWrapper("-");

    private final WrapperListModel<Node> nodesModel;
    private final JTextArea area;

    public FreightDestinationPanel() {
        JComboBox<Wrapper<Node>> nodesComboBox = new JComboBox<>();
        nodesModel = new WrapperListModel<>(true);
        nodesComboBox.setModel(nodesModel);

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.LEFT);
        topPanel.setLayout(topLayout);
        topPanel.add(nodesComboBox);

        nodesComboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                Node node = nodesModel.getSelectedObject();
                updateView(node);
            }
        });

        area = new JTextArea();
        area.setFont(area.getFont().deriveFont(12.0f));
        area.setLineWrap(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scroll.getBorder()));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll, BorderLayout.CENTER);
    }

    public void setDiagram(TrainDiagram diagram) {
        nodesModel.clear();
        if (diagram != null) {
            nodesModel.setListOfWrappers(Wrapper.getWrapperList(diagram.getNet().getNodes()));
        }
        nodesModel.addWrapper(empty);
        nodesModel.setSelectedItem(empty);
    }

    public void updateView(Node node) {
        // update selection
        if (nodesModel.getSelectedObject() != node) {
            nodesModel.setSelectedObject(node);
        }
        StringBuilder text = new StringBuilder();
        if (node != null) {
            // process freight
            text.append(node.getName());
        }
        area.setText(text.toString());
    }
}
