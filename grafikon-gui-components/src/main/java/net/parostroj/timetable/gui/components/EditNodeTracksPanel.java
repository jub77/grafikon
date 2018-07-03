package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;

import org.beanfabrics.Path;
import org.beanfabrics.event.ModelProviderEvent;
import org.beanfabrics.event.ModelProviderListener;
import org.beanfabrics.model.PresentationModel;
import org.beanfabrics.model.Selection;

import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.pm.NodeTrackPM;

/**
 * Panel for editing tracks.
 *
 * @author jub
 */
public class EditNodeTracksPanel extends BaseEditPanel<NodePM> {

    private static final long serialVersionUID = 1L;

    public EditNodeTracksPanel() {
        ItemListEditPanel<NodeTrackPM> tracksPanel = new ItemListEditPanel<>(new Path("number"), 5);
        tracksPanel.setModelProvider(localProvider);
        tracksPanel.setPath(new Path("tracks"));

        EditNodeTrackPanel trackPanel = new EditNodeTrackPanel();
        trackPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        PropertyChangeListener listener = evt -> {
            Selection<NodeTrackPM> selection = getPresentationModel().getTracks().getSelection();
            trackPanel.setPresentationModel(selection.getIndexes().length == 1 ? selection.getFirst() : null);
        };

        localProvider.addModelProviderListener(new Path("tracks"), new ModelProviderListener() {
            @Override
            public void modelLost(ModelProviderEvent evt) {
                PresentationModel model = evt.getModel();
                if (model != null) {
                    model.removePropertyChangeListener("tracks", listener);
                }
            }

            @Override
            public void modelGained(ModelProviderEvent evt) {
                PresentationModel model = evt.getModel();
                if (model != null) {
                    model.addPropertyChangeListener("tracks", listener);
                }
            }
        });

        this.setLayout(new BorderLayout());
        this.add(tracksPanel, BorderLayout.CENTER);
        this.add(trackPanel, BorderLayout.SOUTH);
    }
}
