package net.parostroj.timetable.gui.components;

import java.awt.FlowLayout;

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

    public EditNodeTrackPanel() {
        BnTextField numberTextField = new BnTextField();
        numberTextField.setColumns(20);
        numberTextField.setModelProvider(localProvider);
        numberTextField.setPath(new Path("number"));

        BnCheckBox platformCheckBox = new BnCheckBox();
        platformCheckBox.setModelProvider(localProvider);
        platformCheckBox.setPath(new Path("platform"));
        platformCheckBox.setText(ResourceLoader.getString("ne.platform"));

        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.add(numberTextField);
        this.add(platformCheckBox);
    }
}
