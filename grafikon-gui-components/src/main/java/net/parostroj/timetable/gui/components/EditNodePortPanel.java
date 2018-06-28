package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.NodePortPM;

/**
 * Panel for editing port.
 *
 * @author jub
 */
public class EditNodePortPanel extends BaseEditPanel<NodePortPM> {

    public EditNodePortPanel() {
        BnTextField xTextField = new BnTextField();
        xTextField.setHorizontalAlignment(BnTextField.RIGHT);
        xTextField.setColumns(2);
        xTextField.setModelProvider(localProvider);
        xTextField.setPath(new Path("x"));

        BnTextField yTextField = new BnTextField();
        yTextField.setHorizontalAlignment(BnTextField.RIGHT);
        yTextField.setColumns(2);
        yTextField.setModelProvider(localProvider);
        yTextField.setPath(new Path("y"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(xTextField);
        this.add(yTextField);
    }
}
