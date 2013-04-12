/*
 * ECDetailsView2.java
 *
 * Created on 4. června 2008, 14:01
 */
package net.parostroj.timetable.gui.views;

import net.parostroj.timetable.gui.views.TCDelegate.Action;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.utils.ResourceLoader;
import java.awt.GridLayout;

/**
 * View with details about selected engine cycle.
 *
 * @author jub
 */
public class TCDetailsView2 extends javax.swing.JPanel implements TCDelegate.Listener {

    private TCDelegate delegate;

    /** Creates new form ECDetailsView2 */
    public TCDetailsView2() {
        initComponents();
    }

    public void setModel(TCDelegate delegate) {
        this.delegate = delegate;
        this.delegate.addListener(this);
    }

    @Override
    public void tcEvent(Action action, TrainsCycle cycle, Train train) {
        switch (action) {
            case REFRESH:
            case SELECTED_CHANGED:
                this.updateValues(cycle);
                break;
            case MODIFIED_CYCLE:
                if (delegate.getSelectedCycle() == cycle)
                    this.updateValues(cycle);
                break;
            default:
                break;
        }
    }

    private void updateValues(TrainsCycle cycle) {
        if (cycle == null) {
            nameTextField.setText("");
            descriptionTextField.setText("");
        } else {
            nameTextField.setText(cycle.getName());
            descriptionTextField.setText(delegate.getCycleDescription());
            nameTextField.setCaretPosition(0);
            descriptionTextField.setCaretPosition(0);
        }
    }

    private void initComponents() {
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();

        nameTextField.setColumns(10);
        nameTextField.setEditable(false);

        nameLabel.setText(ResourceLoader.getString("ec.details.name")); // NOI18N
        setLayout(new GridLayout(0, 1, 0, 0));
        add(nameLabel);
        add(nameTextField);
        javax.swing.JLabel descriptionLabel = new javax.swing.JLabel();

        descriptionLabel.setText(ResourceLoader.getString("ec.details.description")); // NOI18N
        add(descriptionLabel);
        descriptionTextField = new javax.swing.JTextField();

        descriptionTextField.setColumns(10);
        descriptionTextField.setEditable(false);
        add(descriptionTextField);
    }

    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JTextField nameTextField;
}
