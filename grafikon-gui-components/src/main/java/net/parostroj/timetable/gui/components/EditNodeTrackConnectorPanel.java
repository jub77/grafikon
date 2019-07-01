package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

import javax.swing.Box;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnComboBox;
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

        BnTextField positionTextField = new BnTextField();
        positionTextField.setHorizontalAlignment(BnTextField.RIGHT);
        positionTextField.setColumns(4);
        positionTextField.setModelProvider(localProvider);
        positionTextField.setPath(new Path("position"));

        BnTextField numberTextField = new BnTextField();
        numberTextField.setColumns(20);
        numberTextField.setModelProvider(localProvider);
        numberTextField.setPath(new Path("number"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.add(orientationComboBox);
        this.add(Box.createHorizontalStrut(5));
        this.add(positionTextField);
        this.add(Box.createHorizontalStrut(5));
        this.add(numberTextField);
    }
}
