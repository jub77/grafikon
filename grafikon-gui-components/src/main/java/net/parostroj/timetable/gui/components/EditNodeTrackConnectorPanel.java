package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

import javax.swing.Box;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.TrackConnectorPM;

/**
 * Panel for editing track.
 *
 * @author jub
 */
public class EditNodeTrackConnectorPanel extends BaseEditPanel<TrackConnectorPM> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public EditNodeTrackConnectorPanel() {
        BnComboBox orientationComboBox = new BnComboBox();
        orientationComboBox.setModelProvider(localProvider);
        orientationComboBox.setPrototypeDisplayValue("MMMMMMMMM");
        orientationComboBox.setPath(new Path("orientation"));

        BnTextField numberTextField = new BnTextField();
        numberTextField.setColumns(20);
        numberTextField.setModelProvider(localProvider);
        numberTextField.setPath(new Path("number"));

        BnComboBox lineTrackComboBox = new BnComboBox();
        lineTrackComboBox.setModelProvider(localProvider);
        lineTrackComboBox.setPrototypeDisplayValue("MMMMMMMMMMMMM");
        lineTrackComboBox.setPath(new Path("lineTrack"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.add(numberTextField);
        this.add(Box.createHorizontalStrut(5));
        this.add(orientationComboBox);
        this.add(Box.createHorizontalStrut(5));
        this.add(lineTrackComboBox);
    }
}
