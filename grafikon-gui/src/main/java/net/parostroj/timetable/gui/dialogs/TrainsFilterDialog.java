/*
 * TrainsFilterDialog.java
 *
 * Created on 26.11.2008, 13:26:04
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.GridLayout;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBox;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Dialog for filtering trains.
 *
 * @author jub
 */
public class TrainsFilterDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private static final int COLUMNS = 3;

    private transient List<Pair<TrainType,JCheckBox>> typesList;
    private transient Set<TrainType> selectedTypes;

    /** Creates new form TrainsFilterDialog */
    public TrainsFilterDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public void setTrainTypes(TrainDiagram diagram, Set<TrainType> types) {
        if (diagram == null)
            return;
        typesPanel.removeAll();
        typesPanel.setLayout(new GridLayout((diagram.getTrainTypes().size() - 1) / COLUMNS + 1, COLUMNS));
        typesList = new LinkedList<>();
        for (TrainType type : diagram.getTrainTypes()) {
            JCheckBox checkBox = new JCheckBox(type.getDefaultAbbr() + " (" + type.getDesc() + ")");
            typesList.add(new Pair<>(type, checkBox));
            checkBox.setSelected(types.contains(type));
            typesPanel.add(checkBox);
        }
        this.pack();
    }

    public Set<TrainType> getSelectedTypes() {
        return selectedTypes;
    }

    private void initComponents() {
        typesPanel = new javax.swing.JPanel();
        javax.swing.JPanel okPanel = new javax.swing.JPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        typesPanel.setLayout(new java.awt.GridLayout());
        getContentPane().add(typesPanel, java.awt.BorderLayout.CENTER);

        okPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(this::okButtonActionPerformed);
        okPanel.add(okButton);

        getContentPane().add(okPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        selectedTypes = new HashSet<>();
        for (Pair<TrainType, JCheckBox> pair : typesList) {
            if (pair.second.isSelected())
                selectedTypes.add(pair.first);
        }
        this.setVisible(false);
    }

    private javax.swing.JPanel typesPanel;
}
