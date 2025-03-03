/*
 * NetPane.java
 *
 * Created on 3. září 2007, 14:44
 */
package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.gui.views.NetEditView;

/**
 * Net editing pane.
 *
 * @author jub
 */
public class NetPane extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

	/** Creates new form NetPane */
    public NetPane(ApplicationModel model) {
        NetEditView netEditView = new NetEditView(model);
        this.setLayout(new BorderLayout());
        this.add(netEditView);
    }
}
