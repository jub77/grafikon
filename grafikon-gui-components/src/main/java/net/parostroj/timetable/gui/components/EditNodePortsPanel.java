package net.parostroj.timetable.gui.components;

import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.beanfabrics.Path;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.Selection;

import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.pm.NodePortPM;

/**
 * Panel for editing ports.
 *
 * @author jub
 */
public class EditNodePortsPanel extends BaseEditPanel<NodePM> {

    private static final long serialVersionUID = 1L;

    private PropertyChangeListener listener;

    public EditNodePortsPanel() {

        ItemSetEditPanel<NodePortPM> portsPanel = new ItemSetEditPanel<>(new Path("portId"), 5);
        portsPanel.setModelProvider(localProvider);
        portsPanel.setPath(new Path("ports"));

        EditNodePortPanel portPanel = new EditNodePortPanel();

        EditNodePortTrackConnectorsPanel connectorsPanel = new EditNodePortTrackConnectorsPanel();

        listener = evt -> {
            Selection<NodePortPM> selection = getPresentationModel().getPorts().getSelection();
            NodePortPM portModel = selection.getIndexes().length == 1 ? selection.getFirst() : null;
            portPanel.setPresentationModel(portModel);
            connectorsPanel.setPresentationModel(portModel);
        };

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        Box combinedPortsPanel = Box.createVerticalBox();
        combinedPortsPanel.add(portsPanel);
        combinedPortsPanel.add(Box.createVerticalStrut(5));
        combinedPortsPanel.add(portPanel);
        this.add(combinedPortsPanel);
        this.add(Box.createHorizontalStrut(5));
        this.add(connectorsPanel);
    }

    @Override
    public void setPresentationModel(NodePM pModel) {
        PresentationModel oldModel = super.getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener("ports", listener);
        }
        super.setPresentationModel(pModel);
        if (pModel != null) {
            pModel.addPropertyChangeListener("ports", listener);
        }
    }
}
