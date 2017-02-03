package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.TrainDiagram;

/**
 * Panel for additional information about freight.
 *
 * @author jub
 */
public class FreightPanel extends JPanel {

    private final FreightDestinationPanel destinationPanel;
    private final FreightConnectionPanel connectionPanel;
    private final FreightCheckPanel checkPanel;

    public FreightPanel() {
        destinationPanel = new FreightDestinationPanel();
        connectionPanel = new FreightConnectionPanel();
        checkPanel = new FreightCheckPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.add(ResourceLoader.getString("freight.destination.title"), destinationPanel);
        tabs.add(ResourceLoader.getString("freight.connection.title"), connectionPanel);
        tabs.add(ResourceLoader.getString("freight.check.title"), checkPanel);

        this.setLayout(new BorderLayout());
        this.add(tabs, BorderLayout.CENTER);
    }

    public void setDiagram(TrainDiagram diagram) {
        this.destinationPanel.setDiagram(diagram);
        this.connectionPanel.setDiagram(diagram);
        this.checkPanel.setDiagram(diagram);
    }
}
