package net.parostroj.timetable.gui.components;

import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.beanfabrics.Path;

import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.pm.NodePortPM;

/**
 * Panel for editing ports.
 *
 * @author jub
 */
public class EditNodePortsPanel extends BaseEditPanel<NodePM> {

    private static final long serialVersionUID = 1L;

    public EditNodePortsPanel() {

        ItemSetEditPanel<NodePortPM> portsPanel = new ItemSetEditPanel<>(new Path("portId"), 5);
        portsPanel.setModelProvider(localProvider);
        portsPanel.setPath(new Path("ports"));

        EditNodePortPanel portPanel = new EditNodePortPanel();

        EditNodePortTrackConnectorsPanel connectorsPanel = new EditNodePortTrackConnectorsPanel();

        ListSelectionSupport<NodePortPM> support = new ListSelectionSupport<>(
                Arrays.asList(connectorsPanel, portPanel),
                () -> getPresentationModel().getPorts());

        localProvider.addModelProviderListener(new Path("ports"), support);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Box combinedPortsPanel = Box.createVerticalBox();
        combinedPortsPanel.add(portsPanel);
        combinedPortsPanel.add(Box.createVerticalStrut(5));
        combinedPortsPanel.add(portPanel);
        this.add(combinedPortsPanel);
        this.add(Box.createHorizontalStrut(5));
        this.add(connectorsPanel);
    }
}
