/*
 * TCDetailsViewDialog.java
 *
 * Created on 4. červen 2008, 12:30
 */
package net.parostroj.timetable.gui.dialogs;

import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing
 *
 * @author jub
 */
public class TCDetailsViewDialog extends javax.swing.JDialog {

    private TCDelegate delegate;

    /** Creates new form TCDetailsViewDialog */
    public TCDetailsViewDialog(java.awt.Window parent, boolean modal) {
        super(parent, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        attributesPanel.setCategory(TrainsCycle.CATEGORY_USER);
    }

    public void updateValues(TCDelegate delegate) {
        this.delegate = delegate;
        this.nameTextField.setText(delegate.getSelectedCycle().getName());
        this.descTextField.setText(delegate.getSelectedCycle().getDescription());
        attributesPanel.startEditing(new Attributes(delegate.getSelectedCycle().getAttributes()));

        Company selCompany = delegate.getSelectedCycle().getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        this.companies = new WrapperListModel<Company>(false);
        Wrapper<Company> emptyCompany = Wrapper.<Company>getEmptyWrapper("-");
        this.companies.addWrapper(emptyCompany);
        for (Company company : delegate.getTrainDiagram().getCompanies()) {
            this.companies.addWrapper(Wrapper.getWrapper(company));
        }
        this.companyComboBox.setModel(companies);
        this.companyComboBox.setSelectedItem(selCompany != null ? Wrapper.getWrapper(selCompany) : emptyCompany);
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel companyLabel = new javax.swing.JLabel();
        descTextField = new javax.swing.JTextField();
        attributesPanel = new net.parostroj.timetable.gui.components.AttributesPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        companyComboBox = new javax.swing.JComboBox<Wrapper<Company>>();

        setModal(true);
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("ec.details.name") + ": "); // NOI18N
        jLabel2.setText(ResourceLoader.getString("ec.details.description") + ": "); // NOI18N
        companyLabel.setText(ResourceLoader.getString("ec.details.company") + ": "); // NOI18N

        nameTextField.setColumns(15);
        descTextField.setColumns(15);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> okButtonActionPerformed(evt));

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> {
            setVisible(false);
            attributesPanel.stopEditing();
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(attributesPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(companyLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(descTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(companyComboBox, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(descTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyLabel)
                    .addComponent(companyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // write values back and close
        TrainsCycle cycle = delegate.getSelectedCycle();
        String name = ObjectsUtil.checkAndTrim(nameTextField.getText());
        if (name != null) {
            cycle.setName(name);
        }
        cycle.setDescription(ObjectsUtil.checkAndTrim(descTextField.getText()));

        // write back company
        Company selCompany = this.companies.getSelectedObject();
        cycle.getAttributes().setRemove(TrainsCycle.ATTR_COMPANY, selCompany);

        // event
        delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, cycle);

        this.setVisible(false);
        cycle.getAttributes().merge(attributesPanel.stopEditing(), TrainsCycle.CATEGORY_USER);
    }

    private net.parostroj.timetable.gui.components.AttributesPanel attributesPanel;
    private javax.swing.JTextField descTextField;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JComboBox<Wrapper<Company>> companyComboBox;

    private WrapperListModel<Company> companies;
}
