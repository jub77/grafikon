/*
 * EditNodeDialog.java
 *
 * Created on 28. září 2007, 16:49
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

import net.parostroj.timetable.gui.components.ValueWithUnitEditBox;
import net.parostroj.timetable.gui.pm.NodePM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.wrappers.NodeTypeWrapperDelegate;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.gui.wrappers.WrapperListModel;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.units.*;
import net.parostroj.timetable.output2.util.OutputFreightUtil;
import net.parostroj.timetable.utils.ObjectsUtil;
import net.parostroj.timetable.utils.ResourceLoader;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.SwingConstants;

/**
 * Edit dialog for node.
 *
 * @author jub
 */
public class EditNodeDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

    private static final BigDecimal DEFAULT_NOT_STRAIGHT_SPEED = new BigDecimal(40);
    private static final BigDecimal DEFAULT_STRAIGHT_SPEED = new BigDecimal(100);

    private static final Logger log = LoggerFactory.getLogger(EditNodeDialog.class);

    private static final Wrapper<Company> EMPTY_COMPANY = Wrapper.<Company>getEmptyWrapper("-");

    private Node node;
    private ModelProvider modelProvider = new ModelProvider();
    private Set<FreightColor> colors;
    private final WrapperListModel<NodeType> types;

    /** Creates new form EditNodeDialog */
    public EditNodeDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        types = new WrapperListModel<>(false);

        // fill combo box
        NodeTypeWrapperDelegate delegate = new NodeTypeWrapperDelegate();
        for (NodeType type : NodeType.values()) {
            types.addWrapper(Wrapper.getWrapper(type, delegate));
        }
        typeComboBox.setModel(types);

        // set units
        lengthEditBox.setUnits(LengthUnit.getScaleDependent());
        lengthCheckBox = new javax.swing.JCheckBox();
        lengthPanel.add(lengthCheckBox, BorderLayout.EAST);
        lengthCheckBox.addItemListener(evt -> lengthCheckBoxItemStateChanged(evt));
        nsSpeedEditBox.setUnits(Arrays.asList(SpeedUnit.values()));
        nsSpeedCheckBox.addItemListener(evt -> nsSpeedCheckBoxItemStateChanged(evt));
        sSpeedEditBox.setUnits(Arrays.asList(SpeedUnit.values()));
        sSpeedCheckBox.addItemListener(evt -> sSpeedCheckBoxItemStateChanged(evt));
    }

    public void showDialog(Node node, LengthUnit unit, SpeedUnit speedUnit) {
        this.node = node;
        this.modelProvider.setPresentationModel(new NodePM(node));
        this.lengthEditBox.setUnit(unit);
        this.nsSpeedEditBox.setUnit(speedUnit);
        this.sSpeedEditBox.setUnit(speedUnit);
        this.updateValues();
        this.pack();
        this.setVisible(true);
    }

    private void updateValues() {
        nameTextField.setText(node.getName());
        abbrTextField.setText(node.getAbbr());
        signalsCheckBox.setSelected(Node.IP_NEW_SIGNALS.equals(node.getAttribute(Node.ATTR_INTERLOCKING_PLANT, String.class)));
        controlCheckBox.setSelected(node.getAttributes().getBool(Node.ATTR_CONTROL_STATION));
        trapezoidCheckBox.setSelected(node.getAttributes().getBool(Node.ATTR_TRAPEZOID_SIGN));
        updateColors();

        // add current regions
        this.regions = new HashSet<>();
        this.regions.addAll(node.getRegions());
        this.updateRegionsTextField(regionsTextField, regions);

        // add center of regions
        this.centerRegions = new HashSet<>();
        this.centerRegions.addAll(node.getCenterRegions());
        this.updateRegionsTextField(centerRegionsTextField, centerRegions);

        // set node length
        Integer length = node.getAttribute(Node.ATTR_LENGTH, Integer.class);
        this.setBoxValue(lengthEditBox, lengthCheckBox, length, LengthUnit.MM, BigDecimal.ZERO);

        // set speed for not straight drive through
        Integer nsSpeed = node.getAttribute(Node.ATTR_NOT_STRAIGHT_SPEED, Integer.class);
        this.setBoxValue(nsSpeedEditBox, nsSpeedCheckBox, nsSpeed, SpeedUnit.KMPH, DEFAULT_NOT_STRAIGHT_SPEED);

        // set speed for straight drive through
        Integer sSpeed = node.getAttribute(Node.ATTR_SPEED, Integer.class);
        this.setBoxValue(sSpeedEditBox, sSpeedCheckBox, sSpeed, SpeedUnit.KMPH, BigDecimal.ZERO);

        types.setSelectedObject(node.getType());

        // company
        this.companies = new WrapperListModel<>(true);
        this.companies.addWrapper(EMPTY_COMPANY);
        TrainDiagram diagram = node.getDiagram();
        for (Company company : diagram.getCompanies()) {
            this.companies.addWrapper(Wrapper.getWrapper(company));
        }
        this.companyComboBox.setModel(companies);
        Company selCompany = node.getAttribute(Node.ATTR_COMPANY, Company.class);
        this.companyComboBox.setSelectedItem(selCompany != null ? Wrapper.getWrapper(selCompany) : EMPTY_COMPANY);
    }

    private void updateRegionsTextField(javax.swing.JTextField field, Collection<Region> regions) {
        field.setText(Wrapper.getWrapperList(OutputFreightUtil.sortRegions(regions, Locale.getDefault())).toString());
    }

    private Set<Region> editRegions(javax.swing.JTextField field, Set<Region> all, Set<Region> selected, Set<Region> locked) {
        ElementSelectionCheckBoxDialog<Region> dialog = new ElementSelectionCheckBoxDialog<>(this, true);
        dialog.setLocationRelativeTo(this);
        Collection<Region> returnedSelection = dialog.selectElements(all, selected, locked);
        dialog.dispose();
        if (returnedSelection != null) {
            Set<Region> newSelection = new HashSet<>(returnedSelection);
            updateRegionsTextField(field, newSelection);
            return newSelection;
        } else {
            return selected;
        }
    }

    private void setBoxValue(ValueWithUnitEditBox box, javax.swing.JCheckBox check, Integer value, Unit unit, BigDecimal defaultValue) {
        check.setSelected(value != null);
        box.setEnabled(value != null);
        if (value != null) {
            box.setValueInUnit(new BigDecimal(value), unit);
        } else {
            box.setValue(defaultValue);
        }
    }

    private void updateColors() {
        colors = node.getFreightColors();
        if (colors == null) {
            colors = Collections.emptySet();
        }
    }

    private void writeValuesBack() {
        String newName = ObjectsUtil.checkAndTrim(nameTextField.getText());
        if (newName != null) {
            node.setName(newName);
        }
        String newAbbr = ObjectsUtil.checkAndTrim(abbrTextField.getText());
        if (newAbbr != null) {
            node.setAbbr(newAbbr);
        }

        node.getAttributes().setRemove(Node.ATTR_INTERLOCKING_PLANT, signalsCheckBox.isSelected() ? Node.IP_NEW_SIGNALS : null);

        node.setType(types.getSelectedObject());

        node.getAttributes().setBool(Node.ATTR_CONTROL_STATION, controlCheckBox.isSelected());
        node.getAttributes().setBool(Node.ATTR_TRAPEZOID_SIGN, trapezoidCheckBox.isSelected());

        // length
        this.getBoxValue(lengthEditBox, lengthCheckBox, LengthUnit.MM, Node.ATTR_LENGTH);
        // ns speed
        this.getBoxValue(nsSpeedEditBox, nsSpeedCheckBox, SpeedUnit.KMPH, Node.ATTR_NOT_STRAIGHT_SPEED);
        // s speed
        this.getBoxValue(sSpeedEditBox, sSpeedCheckBox, SpeedUnit.KMPH, Node.ATTR_SPEED);

        // colors
        if (colors.isEmpty()) {
            colors = null;
        }
        node.getAttributes().setRemove(Node.ATTR_FREIGHT_COLORS, colors);

        // regions
        node.setRemoveAttribute(
                Node.ATTR_REGIONS, this.regions.isEmpty() ? null : this.regions);

        // company
        node.getAttributes().setRemove(Node.ATTR_COMPANY, companies.getSelectedObject());

        // center of regions
        node.setRemoveAttribute(
                Node.ATTR_CENTER_OF_REGIONS, this.centerRegions.isEmpty() ? null : this.centerRegions);

        NodePM pm = modelProvider.getPresentationModel();
        pm.writeResult();
    }

    private void getBoxValue(ValueWithUnitEditBox box, javax.swing.JCheckBox check, Unit unit, String attribute) {
        if (check.isSelected()) {
            try {
                Integer value = UnitUtil.convert(box.getValueInUnit(unit));
                node.getAttributes().set(attribute, value);
            } catch (ArithmeticException e) {
                log.warn("Value overflow: {}", box.getValueInUnit(unit));
                log.warn(e.getMessage());
            }
        } else {
            node.removeAttribute(attribute);
        }
    }

    private void initComponents() {
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        nameTextField.setColumns(40);
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        abbrTextField = new javax.swing.JTextField();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox<>();
        editTracksButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        javax.swing.JLabel companyLabel = new javax.swing.JLabel();

        tracksAndConnectorsDialog = new EditNodeTracksAndConnectorsDialog(this, false);
        tracksAndConnectorsDialog.setModelProvider(modelProvider);
        tracksAndConnectorsDialog.setPath(new Path("this"));

        regionsTextField = new javax.swing.JTextField();
        regionsTextField.setEditable(false);
        centerRegionsTextField = new javax.swing.JTextField();
        centerRegionsTextField.setEditable(false);

        companyComboBox = new javax.swing.JComboBox<>();

        setResizable(false);

        jLabel1.setText(ResourceLoader.getString("ne.name")); // NOI18N

        jLabel2.setText(ResourceLoader.getString("ne.abbr")); // NOI18N

        jLabel3.setText(ResourceLoader.getString("ne.type")); // NOI18N

        jLabel5.setText(ResourceLoader.getString("ne.regions") + ":"); // NOI18N

        jLabel6.setText(ResourceLoader.getString("ne.center.regions") + ":"); // NOI18N

        companyLabel.setText(ResourceLoader.getString("ne.company") + ":"); // NOI18N

        typeComboBox.addItemListener(evt -> typeComboBoxItemStateChanged(evt));

        editTracksButton.addActionListener(evt -> {
            tracksAndConnectorsDialog.setLocationRelativeTo(this);
            tracksAndConnectorsDialog.setVisible(true);
        });

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(evt -> {
            writeValuesBack();
            setVisible(false);
        });

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(evt -> setVisible(false));

        jLabel4.setText(ResourceLoader.getString("ne.length")); // NOI18N

        javax.swing.JPanel panel = new javax.swing.JPanel();

        colorsButton = new javax.swing.JButton(ResourceLoader.getString("ne.colors"));
        colorsButton.addActionListener(e -> {
            FreightColorsDialog dialog = new FreightColorsDialog(EditNodeDialog.this);
            dialog.setLocationRelativeTo(EditNodeDialog.this);
            List<FreightColor> result = dialog.showDialog(colors);
            if (result != null) {
                colors = new HashSet<>(result);
            }
            dialog.dispose();
        });

        lengthPanel = new javax.swing.JPanel();

        javax.swing.JLabel label = new javax.swing.JLabel();
        label.setText(ResourceLoader.getString("ne.not.straight.speed")); // NOI18N

        javax.swing.JPanel nsSpeedPanel = new javax.swing.JPanel();

        javax.swing.JLabel label_1 = new javax.swing.JLabel();
        label_1.setText(ResourceLoader.getString("ne.straight.speed")); // NOI18N

        javax.swing.JPanel sSpeedPanel = new javax.swing.JPanel();
        sSpeedPanel.setLayout(new BorderLayout(0, 0));

        javax.swing.JButton editRegionsButton = new javax.swing.JButton("..."); // NOI18N
        editRegionsButton
                .addActionListener(e -> {
                    boolean isEmpty = this.regions.isEmpty();
                    Region superRegion = isEmpty ? null : this.regions.iterator().next().getSuperRegion();
                    Set<Region> available = node.getDiagram().getNet().getRegions().stream()
                            .filter(r -> !r.hasSubRegions() && (isEmpty || r.getSuperRegion() == superRegion))
                            .collect(Collectors.toSet());
                    this.regions = editRegions(
                            regionsTextField, available,
                            this.regions, this.centerRegions);
                });
        javax.swing.JButton editCenterRegionsButton = new javax.swing.JButton("..."); // NOI18N
        editCenterRegionsButton.addActionListener(
                e -> this.centerRegions = editRegions(centerRegionsTextField, regions, centerRegions, null));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(editTracksButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(colorsButton)
                            .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(okButton)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(cancelButton))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel5, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(regionsTextField, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(editRegionsButton))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel6, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(centerRegionsTextField, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(editCenterRegionsButton))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(companyLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(companyComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(nameTextField))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(abbrTextField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(lengthPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel3, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(typeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(nsSpeedPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(label_1)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(sSpeedPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(nameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(abbrTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(label_1)
                        .addComponent(sSpeedPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(7)
                    .addGroup(layout.createParallelGroup(Alignment.CENTER)
                        .addComponent(label)
                        .addComponent(nsSpeedPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(6)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(regionsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(editRegionsButton))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(centerRegionsTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(editCenterRegionsButton))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(companyLabel)
                        .addComponent(companyComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(typeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addGap(7)
                    .addGroup(layout.createParallelGroup(Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lengthPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(10)))
                    .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(editTracksButton))
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(cancelButton)
                            .addComponent(okButton)
                            .addComponent(colorsButton)))
                    .addContainerGap())
        );
        layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, companyLabel, label, label_1});

        sSpeedEditBox = new ValueWithUnitEditBox();
        sSpeedPanel.add(sSpeedEditBox, BorderLayout.CENTER);

        sSpeedCheckBox = new javax.swing.JCheckBox();
        sSpeedPanel.add(sSpeedCheckBox, BorderLayout.EAST);
        nsSpeedPanel.setLayout(new BorderLayout(0, 0));

        nsSpeedEditBox = new ValueWithUnitEditBox();
        nsSpeedPanel.add(nsSpeedEditBox, BorderLayout.CENTER);

        nsSpeedCheckBox = new javax.swing.JCheckBox();
        nsSpeedPanel.add(nsSpeedCheckBox, BorderLayout.EAST);
        lengthPanel.setLayout(new BorderLayout(0, 0));
        lengthEditBox = new ValueWithUnitEditBox();
        lengthPanel.add(lengthEditBox, BorderLayout.CENTER);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
        signalsCheckBox = new javax.swing.JCheckBox();
        panel.add(signalsCheckBox);

        signalsCheckBox.setText(ResourceLoader.getString("ne.new.signals"));
        trapezoidCheckBox = new javax.swing.JCheckBox();
        panel.add(trapezoidCheckBox);

        trapezoidCheckBox.setText(ResourceLoader.getString("ne.trapezoid.table"));
        controlCheckBox = new javax.swing.JCheckBox();
        panel.add(controlCheckBox);

        controlCheckBox.setText(ResourceLoader.getString("ne.control.station"));

        getContentPane().setLayout(layout);

        pack();
    }

    private void typeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            // selected ... (SIGNAL disables editing of length)
            boolean signal = types.getSelectedObject() == NodeType.SIGNAL;
            if (signal) {
                // disable editing of length
                this.lengthCheckBox.setSelected(false);
            }
            this.lengthCheckBox.setEnabled(!signal);
        }
    }

    private void lengthCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        lengthEditBox.setEnabled(evt.getStateChange() == ItemEvent.SELECTED);
    }

    private void nsSpeedCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        boolean selected = evt.getStateChange() == ItemEvent.SELECTED;
        nsSpeedEditBox.setEnabled(selected);
        nsSpeedEditBox.setValueInUnit(DEFAULT_NOT_STRAIGHT_SPEED, SpeedUnit.KMPH);
    }

    private void sSpeedCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        boolean selected = evt.getStateChange() == ItemEvent.SELECTED;
        sSpeedEditBox.setEnabled(selected);
        sSpeedEditBox.setValueInUnit(selected ? DEFAULT_STRAIGHT_SPEED : BigDecimal.ZERO, SpeedUnit.KMPH);
    }

    private javax.swing.JTextField abbrTextField;
    private javax.swing.JCheckBox controlCheckBox;
    private final javax.swing.JCheckBox lengthCheckBox;
    private ValueWithUnitEditBox lengthEditBox;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton editTracksButton;
    private javax.swing.JCheckBox signalsCheckBox;
    private javax.swing.JCheckBox trapezoidCheckBox;
    private javax.swing.JComboBox<Wrapper<NodeType>> typeComboBox;
    private javax.swing.JTextField regionsTextField;
    private javax.swing.JTextField centerRegionsTextField;
    private javax.swing.JComboBox<Wrapper<Company>> companyComboBox;
    private javax.swing.JButton colorsButton;
    private javax.swing.JPanel lengthPanel;
    private ValueWithUnitEditBox nsSpeedEditBox;
    private javax.swing.JCheckBox nsSpeedCheckBox;
    private ValueWithUnitEditBox sSpeedEditBox;
    private javax.swing.JCheckBox sSpeedCheckBox;
    private EditNodeTracksAndConnectorsDialog tracksAndConnectorsDialog;

    private WrapperListModel<Company> companies;
    private Set<Region> regions;
    private Set<Region> centerRegions;
}
