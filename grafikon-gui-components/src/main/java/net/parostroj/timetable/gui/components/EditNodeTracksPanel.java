package net.parostroj.timetable.gui.components;

import javax.swing.Box;
import javax.swing.BoxLayout;

import org.beanfabrics.Path;

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

        ListSelectionSupport<NodeTrackPM> support = new ListSelectionSupport<>(
                trackPanel,
                () -> getPresentationModel().getTracks());
        localProvider.addModelProviderListener(new Path("tracks"), support);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(tracksPanel);
        this.add(Box.createVerticalStrut(5));
        this.add(trackPanel);
    }
}
