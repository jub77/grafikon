/*
 * CreateTrainDialog.java
 *
 * Created on 3. září 2007, 19:49
 */
package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.gui.ApplicationModel;
import net.parostroj.timetable.model.Group;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Create train dialog.
 *
 * @author jub
 */
public class CreateTrainDialog extends javax.swing.JDialog {

    private net.parostroj.timetable.gui.views.CreateTrainView createTrainView;

    /**
     * Creates new form CreateTrainDialog.
     *
     * @param parent parent window
     * @param model model
     */
    public CreateTrainDialog(java.awt.Frame parent, ApplicationModel model) {
        super(parent, true);
        initComponents();
        createTrainView.setModel(model);

        // fix size
        this.setResizable(false);
    }

    private void initComponents() {
        createTrainView = new net.parostroj.timetable.gui.views.CreateTrainView();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(ResourceLoader.getString("dialog.createtrain.title")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(createTrainView, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(createTrainView, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    public void updateView(Group selectedGroup) {
        createTrainView.updateView(selectedGroup);
    }
}
