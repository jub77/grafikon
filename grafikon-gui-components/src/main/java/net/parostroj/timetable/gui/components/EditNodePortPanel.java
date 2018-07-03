package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

import javax.swing.Box;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.NodePortPM;

/**
 * Panel for editing port.
 *
 * @author jub
 */
public class EditNodePortPanel extends BaseEditPanel<NodePortPM> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    public EditNodePortPanel() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        BnComboBox orientationComboBox = new BnComboBox();
        orientationComboBox.setModelProvider(localProvider);
        orientationComboBox.setPrototypeDisplayValue("MMMMMMMMM");
        orientationComboBox.setPath(new Path("orientation"));

        BnTextField positionTextField = new BnTextField();
        positionTextField.setHorizontalAlignment(BnTextField.RIGHT);
        positionTextField.setColumns(2);
        positionTextField.setModelProvider(localProvider);
        positionTextField.setPath(new Path("position"));

        this.add(orientationComboBox);
        this.add(Box.createHorizontalStrut(5));
        this.add(positionTextField);
    }
}
