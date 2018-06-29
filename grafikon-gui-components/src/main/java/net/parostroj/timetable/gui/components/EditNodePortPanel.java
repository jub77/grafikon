package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

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
        BnComboBox orientationComboBox = new BnComboBox();
        orientationComboBox.setModelProvider(localProvider);
        orientationComboBox.setPrototypeDisplayValue("MMMMMMMMM");
        orientationComboBox.setPath(new Path("orientation"));

        BnTextField positionTextField = new BnTextField();
        positionTextField.setHorizontalAlignment(BnTextField.RIGHT);
        positionTextField.setColumns(2);
        positionTextField.setModelProvider(localProvider);
        positionTextField.setPath(new Path("position"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(orientationComboBox);
        this.add(positionTextField);
    }
}
