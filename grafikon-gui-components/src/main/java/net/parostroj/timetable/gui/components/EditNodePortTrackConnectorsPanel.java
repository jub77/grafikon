package net.parostroj.timetable.gui.components;

import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.beanfabrics.Path;
import org.beanfabrics.event.ModelProviderEvent;
import org.beanfabrics.event.ModelProviderListener;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.Selection;

import net.parostroj.timetable.gui.pm.NodePortPM;
import net.parostroj.timetable.gui.pm.TrackConnectorPM;

/**
 * Panel for editing port connectors.
 *
 * @author jub
 */
public class EditNodePortTrackConnectorsPanel extends BaseEditPanel<NodePortPM> {

    private static final long serialVersionUID = 1L;

    public EditNodePortTrackConnectorsPanel() {
        ItemListEditPanel<TrackConnectorPM> connectorsPanel = new ItemListEditPanel<>(new Path("number"), 5);
        connectorsPanel.setModelProvider(localProvider);
        connectorsPanel.setPath(new Path("connectors"));

        EditNodePortTrackConnectorPanel connectorPanel = new EditNodePortTrackConnectorPanel();

        Runnable listener = () -> {
            Selection<TrackConnectorPM> selection = getPresentationModel().getConnectors().getSelection();
            TrackConnectorPM connectorModel = selection.getIndexes().length == 1 ? selection.getFirst() : null;
            connectorPanel.setPresentationModel(connectorModel);
        };
        PropertyChangeListener pcListener = evt -> listener.run();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(connectorsPanel);
        this.add(Box.createVerticalStrut(5));
        this.add(connectorPanel);

        localProvider.addModelProviderListener(new Path("this"), new ModelProviderListener() {
            @Override
            public void modelLost(ModelProviderEvent evt) {
                PresentationModel model = evt.getModel();
                if (model != null) {
                    model.removePropertyChangeListener("connectors", pcListener);
                }
                connectorPanel.setPresentationModel(null);
            }

            @Override
            public void modelGained(ModelProviderEvent evt) {
                PresentationModel model = evt.getModel();
                if (model != null) {
                    model.addPropertyChangeListener("connectors", pcListener);
                    listener.run();
                }
            }
        });
    }
}
