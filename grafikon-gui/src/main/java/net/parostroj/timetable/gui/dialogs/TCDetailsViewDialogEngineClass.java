/*
 * TCDetailsViewDialogEngineClass.java
 *
 * Created on 4. červen 2008, 12:30
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;

import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing
 *
 * @author jub
 */
public class TCDetailsViewDialogEngineClass extends javax.swing.JDialog {

    private TCDelegate delegate;
    private static final EngineClass noneEngineClass = new EngineClass(null, ResourceLoader.getString("ec.details.engineclass.none"));

    public TCDetailsViewDialogEngineClass() {
        this(null, true);
    }

    public TCDetailsViewDialogEngineClass(Window window, boolean modal) {
        super(window, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        attributesPanel.setCategory(Attributes.USER_CATEGORY);
    }

    public void updateValues(TCDelegate delegate, TrainDiagram diagram) {
        this.delegate = delegate;
        TrainsCycle cycle = delegate.getSelectedCycle();
        EngineClass clazz = cycle.getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
        Company selCompany = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        this.nameTextField.setText(cycle.getName());
        this.descTextField.setText(cycle.getDescription());
        this.engineClassComboBox.removeAllItems();
        this.engineClassComboBox.addItem(noneEngineClass);
        for (EngineClass c : diagram.getEngineClasses()) {
            this.engineClassComboBox.addItem(c);
        }
        this.companies = new WrapperListModel<Company>(false);
        Wrapper<Company> emptyCompany = Wrapper.<Company>getEmptyWrapper("-");
        this.companies.addWrapper(emptyCompany);
        for (Company company : diagram.getCompanies()) {
            this.companies.addWrapper(Wrapper.getWrapper(company));
        }
        attributesPanel.startEditing(new Attributes(cycle.getAttributes()));
        this.engineClassComboBox.setSelectedItem(clazz != null ? clazz : noneEngineClass);
        this.companyComboBox.setModel(companies);
        this.companyComboBox.setSelectedItem(selCompany != null ? Wrapper.getWrapper(selCompany) : emptyCompany);
    }

    private void initComponents() {
        nameTextField = new javax.swing.JTextField();
        engineClassComboBox = new javax.swing.JComboBox<EngineClass>();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        descTextField = new javax.swing.JTextField();
        companyComboBox = new javax.swing.JComboBox<Wrapper<Company>>();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        javax.swing.JLabel companyLabel = new javax.swing.JLabel();
        attributesPanel = new net.parostroj.timetable.gui.components.AttributesPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setModal(true);
        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("ec.details.name") + ": "); // NOI18N
        jLabel2.setText(ResourceLoader.getString("ec.details.description") + ": "); // NOI18N
        jLabel3.setText(ResourceLoader.getString("ec.details.engineclass") + ": "); // NOI18N
        companyLabel.setText(ResourceLoader.getString("ec.details.company") + ": "); // NOI18N

        nameTextField.setColumns(15);
        descTextField.setColumns(15);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisible(false);
                attributesPanel.stopEditing();
            }
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
                            .addComponent(jLabel3)
                            .addComponent(companyLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(descTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)
                            .addComponent(engineClassComboBox, 0, 286, Short.MAX_VALUE)
                            .addComponent(companyComboBox, 0, 286, Short.MAX_VALUE))))
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
                    .addComponent(engineClassComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(companyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
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

        // write back engine class
        EngineClass selEngineClass = (EngineClass) engineClassComboBox.getSelectedItem();
        if (selEngineClass == noneEngineClass) {
            selEngineClass = null;
        }
        cycle.getAttributes().setRemove(TrainsCycle.ATTR_ENGINE_CLASS, selEngineClass);

        // write back company
        Company selCompany = this.companies.getSelectedObject();
        cycle.getAttributes().setRemove(TrainsCycle.ATTR_COMPANY, selCompany);

        // event
        delegate.fireEvent(TCDelegate.Action.MODIFIED_CYCLE, cycle);

        this.setVisible(false);
        cycle.getAttributes().merge(attributesPanel.stopEditing(), Attributes.USER_CATEGORY);
    }

    private net.parostroj.timetable.gui.components.AttributesPanel attributesPanel;
    private javax.swing.JTextField descTextField;
    private javax.swing.JComboBox<EngineClass> engineClassComboBox;
    private javax.swing.JComboBox<Wrapper<Company>> companyComboBox;
    private javax.swing.JTextField nameTextField;

    private WrapperListModel<Company> companies;
}
