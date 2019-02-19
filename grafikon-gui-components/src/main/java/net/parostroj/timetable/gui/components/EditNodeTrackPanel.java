package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

import javax.swing.Box;

import org.beanfabrics.Path;
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.NodeTrackPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Panel for editing track.
 *
 * @author jub
 */
public class EditNodeTrackPanel extends BaseEditPanel<NodeTrackPM> {

    private static final long serialVersionUID = 1L;

    public EditNodeTrackPanel() {
        BnTextField numberTextField = new BnTextField();
        numberTextField.setColumns(20);
        numberTextField.setModelProvider(localProvider);
        numberTextField.setPath(new Path("number"));

        BnCheckBox platformCheckBox = new BnCheckBox();
        platformCheckBox.setModelProvider(localProvider);
        platformCheckBox.setPath(new Path("platform"));
        platformCheckBox.setText(ResourceLoader.getString("ne.platform"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.add(numberTextField);
        this.add(Box.createHorizontalStrut(5));
        this.add(platformCheckBox);
    }
}
