/*
 * TCDetailsViewDialogEngineClass.java
 *
 * Created on 4. červen 2008, 12:30
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.views.TCDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ResourceLoader;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing details of circulation.
 *
 * @author jub
 */
public class TCDetailsViewDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private static final int NAME_COLUMNS = 25;
    private static final Logger log = LoggerFactory.getLogger(TCDetailsViewDialog.class);

    private transient TCDelegate delegate;
    private static final Wrapper<EngineClass> NO_ENGINE = Wrapper.getEmptyWrapper("-");
    private static final String NO_LEVEL = "-";

    public TCDetailsViewDialog(Window window, boolean modal) {
        super(window, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        initComponents();
        attributesPanel.setCategory(TrainsCycle.CATEGORY_USER);
        levelComboBox.addItem(NO_LEVEL);
        for (int i = 1; i <= 10; i++) {
            levelComboBox.addItem(i);
        }
        levelComboBox.setEditable(true);
    }

    public void updateValues(TCDelegate delegate, TrainDiagram diagram) {
        this.delegate = delegate;
        TrainsCycle cycle = delegate.getSelectedCycle();
        EngineClass clazz = cycle.getAttribute(TrainsCycle.ATTR_ENGINE_CLASS, EngineClass.class);
        Company selCompany = cycle.getAttribute(TrainsCycle.ATTR_COMPANY, Company.class);
        this.nameTextField.setText(cycle.getName());
        this.descTextField.setText(cycle.getDescription());
        boolean isEngineType = delegate.getType().isEngineType();
        boolean isDriverCycle = delegate.getType().isDriverType();
        boolean isTrainUnitCycle = delegate.getType().isTrainUnitType();
        if (isEngineType) {
            this.engines = new WrapperListModel<>(true);
            this.engines.addWrapper(NO_ENGINE);
            for (EngineClass c : diagram.getEngineClasses()) {
                this.engines.addWrapper(Wrapper.getWrapper(c));
            }
            this.engineClassComboBox.setModel(engines);
            this.engineClassComboBox.setSelectedItem(clazz != null ? Wrapper.getWrapper(clazz) : NO_ENGINE);
        }
        if (isDriverCycle) {
            Integer level = cycle.getAttribute(TrainsCycle.ATTR_LEVEL, Integer.class);
            if (level == null) {
                levelComboBox.setSelectedItem(NO_LEVEL);
            } else {
                levelComboBox.setSelectedItem(level);
            }
        }
        if (isTrainUnitCycle) {
            boolean freight = cycle.getAttributeAsBool(TrainsCycle.ATTR_FREIGHT);
            freightCheckBox.setSelected(freight);
        }
        this.levelComboBox.setVisible(isDriverCycle);
        this.levelLabel.setVisible(isDriverCycle);
        this.engineClassComboBox.setVisible(isEngineType);
        this.engineClassLabel.setVisible(isEngineType);
        this.freightCheckBox.setVisible(isTrainUnitCycle);
        this.freightLabel.setVisible(isTrainUnitCycle);
        this.companies = new WrapperListModel<>(true);
        Wrapper<Company> emptyCompany = Wrapper.getEmptyWrapper("-");
        this.companies.addWrapper(emptyCompany);
        for (Company company : diagram.getCompanies()) {
            this.companies.addWrapper(Wrapper.getWrapper(company));
        }
        attributesPanel.startEditing(new Attributes(cycle.getAttributes()));
        this.companyComboBox.setModel(companies);
        this.companyComboBox.setSelectedItem(selCompany != null ? Wrapper.getWrapper(selCompany) : emptyCompany);
        pack();
    }

    private void initComponents() {
        freightLabel = new javax.swing.JLabel();
        freightCheckBox = new javax.swing.JCheckBox();
        freightCheckBox.setMargin(null);
        freightCheckBox.setBorder(null);
        nameTextField = new javax.swing.JTextField();
        engineClassComboBox = new javax.swing.JComboBox<>();
        javax.swing.JLabel nameLabel = new javax.swing.JLabel();
        javax.swing.JLabel descLabel = new javax.swing.JLabel();
        descTextField = new javax.swing.JTextField();
        companyComboBox = new javax.swing.JComboBox<>();
        levelComboBox = new javax.swing.JComboBox<>();
        engineClassLabel = new javax.swing.JLabel();
        levelLabel = new javax.swing.JLabel();
        javax.swing.JLabel companyLabel = new javax.swing.JLabel();
        attributesPanel = new net.parostroj.timetable.gui.components.AttributesPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        nameLabel.setText(ResourceLoader.getString("ec.details.name") + ": "); // NOI18N
        descLabel.setText(ResourceLoader.getString("ec.details.description") + ": "); // NOI18N
        engineClassLabel.setText(ResourceLoader.getString("ec.details.engineclass") + ": "); // NOI18N
        companyLabel.setText(ResourceLoader.getString("ec.details.company") + ": "); // NOI18N
        levelLabel.setText(ResourceLoader.getString("ec.details.level") + ": "); // NOI18N
        freightLabel.setText(ResourceLoader.getString("ec.details.freight") + ": "); // NOI18N

        nameTextField.setColumns(NAME_COLUMNS);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> okButtonActionPerformed());

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
                    .addComponent(attributesPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(descLabel)
                            .addComponent(nameLabel)
                            .addComponent(engineClassLabel)
                            .addComponent(companyLabel)
                            .addComponent(levelLabel)
                            .addComponent(freightLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(descTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(engineClassComboBox, 0, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(companyComboBox, 0, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(levelComboBox, 0, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                            .addComponent(freightCheckBox, 0, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameLabel)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(descLabel)
                    .addComponent(descTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(engineClassComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(engineClassLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(companyComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(companyLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(levelComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(levelLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(freightCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(freightLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }

    private void okButtonActionPerformed() {
        // write values back and close
        TrainsCycle cycle = delegate.getSelectedCycle();
        String name = ObjectsUtil.checkAndTrim(nameTextField.getText());
        if (name != null) {
            cycle.setName(name);
        }
        cycle.setDescription(ObjectsUtil.checkAndTrim(descTextField.getText()));

        if (delegate.getType().isEngineType()) {
            // write back engine class
            EngineClass selEngineClass = engines.getSelectedObject();
            cycle.getAttributes().setRemove(TrainsCycle.ATTR_ENGINE_CLASS, selEngineClass);
        }
        if (delegate.getType().isDriverType()) {
            Object objectLevel = levelComboBox.getSelectedItem();
            Integer level = null;
            if (objectLevel instanceof Integer) {
                level = (Integer) objectLevel;
            } else if (objectLevel instanceof String && !objectLevel.equals(NO_LEVEL)) {
                try {
                    level = Integer.parseInt((String) objectLevel);
                } catch (NumberFormatException e) {
                    log.warn("Error parsing level: " + objectLevel, e);
                }
            }
            cycle.setRemoveAttribute(TrainsCycle.ATTR_LEVEL, level);
        }
        if (delegate.getType().isTrainUnitType()) {
            cycle.setAttributeAsBool(TrainsCycle.ATTR_FREIGHT, freightCheckBox.isSelected());
        }
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
    private javax.swing.JComboBox<Wrapper<EngineClass>> engineClassComboBox;
    private javax.swing.JComboBox<Wrapper<Company>> companyComboBox;
    private javax.swing.JComboBox<Object> levelComboBox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel engineClassLabel;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JLabel freightLabel;
    private javax.swing.JCheckBox freightCheckBox;

    private WrapperListModel<Company> companies;
    private WrapperListModel<EngineClass> engines;
}
