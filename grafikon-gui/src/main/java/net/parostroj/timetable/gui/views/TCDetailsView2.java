/*
 * ECDetailsView2.java
 *
 * Created on 4. června 2008, 14:01
 */
package net.parostroj.timetable.gui.views;

import java.awt.GridLayout;

import net.parostroj.timetable.gui.views.TCDelegate.Action;
import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainsCycle;

/**
 * View with details about selected engine cycle.
 *
 * @author jub
 */
public class TCDetailsView2 extends javax.swing.JPanel implements TCDelegate.Listener {

    private static final long serialVersionUID = 1L;

	private transient TCDelegate delegate;

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
            nameTextField.setText(this.getNameText(cycle));
            descriptionTextField.setText(delegate.getCycleDescription());
            nameTextField.setCaretPosition(0);
            descriptionTextField.setCaretPosition(0);
        }
    }

    private void initComponents() {
        nameTextField = new javax.swing.JTextField();

        nameTextField.setColumns(10);
        nameTextField.setEditable(false);

        GridLayout layout = new GridLayout(0, 1, 0, 3);
        setLayout(layout);
        add(nameTextField);

        descriptionTextField = new javax.swing.JTextField();

        descriptionTextField.setColumns(10);
        descriptionTextField.setEditable(false);
        add(descriptionTextField);
    }

    private String getNameText(TrainsCycle cycle) {
        Company company = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        return company == null ? cycle.getName() : String.format("%s (%s)", cycle.getName(), company.getAbbr());
    }

    private javax.swing.JTextField descriptionTextField;
    private javax.swing.JTextField nameTextField;
}
