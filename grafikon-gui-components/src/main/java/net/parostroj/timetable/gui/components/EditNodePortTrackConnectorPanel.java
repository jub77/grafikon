package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.TrackConnectorPM;

/**
 * Panel for editing track.
 *
 * @author jub
 */
public class EditNodePortTrackConnectorPanel extends BaseEditPanel<TrackConnectorPM> {

    private static final long serialVersionUID = 1L;

    public EditNodePortTrackConnectorPanel() {
        BnTextField numberTextField = new BnTextField();
        numberTextField.setColumns(5);
        numberTextField.setModelProvider(localProvider);
        numberTextField.setPath(new Path("number"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.add(numberTextField);
    }
}
