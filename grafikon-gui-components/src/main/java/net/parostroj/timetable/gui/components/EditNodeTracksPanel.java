package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;

import org.beanfabrics.Path;
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

    private PropertyChangeListener listener;

    public EditNodeTracksPanel() {
        ItemListEditPanel<NodeTrackPM> tracksPanel = new ItemListEditPanel<>(new Path("number"), 5);
        tracksPanel.setModelProvider(localProvider);
        tracksPanel.setPath(new Path("tracks"));

        EditNodeTrackPanel trackPanel = new EditNodeTrackPanel();
        trackPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        listener = evt -> {
            Selection<NodeTrackPM> selection = getPresentationModel().getTracks().getSelection();
            trackPanel.setPresentationModel(selection.getIndexes().length == 1 ? selection.getFirst() : null);
        };

        this.setLayout(new BorderLayout());
        this.add(tracksPanel, BorderLayout.CENTER);
        this.add(trackPanel, BorderLayout.SOUTH);
    }

    @Override
    public void setPresentationModel(NodePM pModel) {
        PresentationModel oldModel = super.getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener("tracks", listener);
        }
        super.setPresentationModel(pModel);
        if (pModel != null) {
            pModel.addPropertyChangeListener("tracks", listener);
        }
    }
}
