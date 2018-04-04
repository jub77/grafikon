/*
 * NetPane.java
 *
 * Created on 3. září 2007, 14:44
 */
package net.parostroj.timetable.gui.panes;

import java.awt.BorderLayout;

import net.parostroj.timetable.gui.ApplicationModel;

/**
 * Net editing pane.
 *
 * @author jub
 */
public class NetPane extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

	/** Creates new form NetPane */
    public NetPane() {
        initComponents();
    }

    public void setModel(ApplicationModel model) {
        netEditView.setModel(model);
    }

    private void initComponents() {
        netEditView = new net.parostroj.timetable.gui.views.NetEditView();
        this.setLayout(new BorderLayout());
        this.add(netEditView);
    }

    private net.parostroj.timetable.gui.views.NetEditView netEditView;
}
