package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.pm.RegionPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.TrainDiagram;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnCheckBox;
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;
import org.beanfabrics.swing.table.BnColumn;
import org.beanfabrics.swing.table.BnTable;

/**
 * Dialog for editing region.
 *
 * @author jub
 */
public class EditRegionDialog extends JDialog {

    private static final int DEFAULT_ROW_COUNT = 3;

    private final ModelProvider provider = new ModelProvider(RegionPM.class);

    public EditRegionDialog(Window parent, boolean modal, Collection<Locale> locales) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        RegionPM model = new RegionPM(locales);
        provider.setPresentationModel(model);
        pack();
        this.setResizable(false);
    }

    public void showDialog(Region region, TrainDiagram diagram) {
        provider.<RegionPM> getPresentationModel().init(region, diagram.getNet().getRegions());
        this.setVisible(true);
    }

    private void initComponents() {
        ActionListener closeListener = evt -> setVisible(false);

        BnButton okButton = new BnButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(closeListener);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(closeListener);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.CENTER);
        GridBagLayout gbLayout = new GridBagLayout();
        gbLayout.columnWeights = new double[] { 0.0, 1.0 };
        gbLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
        panel.setLayout(gbLayout);

        JLabel nameLabel = new JLabel(ResourceLoader.getString("edit.region.name")); // NOI18N
        GridBagConstraints dlCons = new GridBagConstraints();
        dlCons.anchor = GridBagConstraints.WEST;
        dlCons.insets = new Insets(0, 0, 5, 5);
        dlCons.gridx = 0;
        dlCons.gridy = 0;
        panel.add(nameLabel, dlCons);

        BnTextField nameTextField = new BnTextField();
        GridBagConstraints dtfCons = new GridBagConstraints();
        dtfCons.insets = new Insets(0, 0, 5, 0);
        dtfCons.anchor = GridBagConstraints.NORTH;
        dtfCons.fill = GridBagConstraints.HORIZONTAL;
        dtfCons.gridx = 1;
        dtfCons.gridy = 0;
        panel.add(nameTextField, dtfCons);
        nameTextField.setColumns(30);
        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("name"));

        JLabel localeLabel = new JLabel(ResourceLoader.getString("edit.region.locale")); // NOI18N
        GridBagConstraints llCons = new GridBagConstraints();
        llCons.anchor = GridBagConstraints.WEST;
        llCons.insets = new Insets(0, 0, 5, 5);
        llCons.gridx = 0;
        llCons.gridy = 1;
        panel.add(localeLabel, llCons);

        BnComboBox localeComboBox = new BnComboBox();
        GridBagConstraints ltfCons = new GridBagConstraints();
        ltfCons.insets = new Insets(0, 0, 5, 0);
        ltfCons.fill = GridBagConstraints.HORIZONTAL;
        ltfCons.gridx = 1;
        ltfCons.gridy = 1;
        panel.add(localeComboBox, ltfCons);
        localeComboBox.setModelProvider(provider);
        localeComboBox.setPath(new Path("locale"));

        JLabel superRegionLabel = new JLabel(ResourceLoader.getString("edit.region.super.region")); // NOI18N
        GridBagConstraints srlCons = new GridBagConstraints();
        srlCons.anchor = GridBagConstraints.WEST;
        srlCons.insets = new Insets(0, 0, 5, 5);
        srlCons.gridx = 0;
        srlCons.gridy = 2;
        panel.add(superRegionLabel, srlCons);

        BnComboBox superRegionComboBox = new BnComboBox();
        GridBagConstraints srltfCons = new GridBagConstraints();
        srltfCons.insets = new Insets(0, 0, 5, 0);
        srltfCons.fill = GridBagConstraints.HORIZONTAL;
        srltfCons.gridx = 1;
        srltfCons.gridy = 2;
        panel.add(superRegionComboBox, srltfCons);
        superRegionComboBox.setModelProvider(provider);
        superRegionComboBox.setPath(new Path("superRegion"));

        JLabel colorCenterLabel = new JLabel(ResourceLoader.getString("edit.region.color.center")); // NOI18N
        GridBagConstraints cclCons = new GridBagConstraints();
        cclCons.anchor = GridBagConstraints.WEST;
        cclCons.insets = new Insets(0, 0, 5, 5);
        cclCons.gridx = 0;
        cclCons.gridy = 3;
        panel.add(colorCenterLabel, cclCons);

        BnCheckBox colorCenterCheckBox = new BnCheckBox();
        colorCenterCheckBox.setBorder(null);
        GridBagConstraints cccbCons = new GridBagConstraints();
        cccbCons.insets = new Insets(0, 0, 5, 0);
        cccbCons.fill = GridBagConstraints.HORIZONTAL;
        cccbCons.gridx = 1;
        cccbCons.gridy = 3;
        panel.add(colorCenterCheckBox, cccbCons);
        colorCenterCheckBox.setModelProvider(provider);
        colorCenterCheckBox.setPath(new Path("colorCenter"));

        JScrollPane scrollPane = new JScrollPane();
        BnTable mapTable = new BnTable();
        Dimension viewportSize = mapTable.getPreferredScrollableViewportSize();
        viewportSize.setSize(0, mapTable.getRowHeight() * DEFAULT_ROW_COUNT);
        mapTable.setPreferredScrollableViewportSize(viewportSize);
        GridBagConstraints mapCons = new GridBagConstraints();
        mapCons.insets = new Insets(0, 0, 5, 0);
        mapCons.fill = GridBagConstraints.BOTH;
        mapCons.gridx = 0;
        mapCons.gridy = 4;
        mapCons.weighty = 1.0;
        mapCons.gridwidth = 2;
        panel.add(scrollPane, mapCons);
        scrollPane.setViewportView(mapTable);
        mapTable.setModelProvider(provider);
        mapTable.setPath(new Path("colorMap"));
        mapTable.addColumn(new BnColumn(new Path("color"), "color"));
        mapTable.addColumn(new BnColumn(new Path("region"), "region"));
        mapTable.setSortable(true);

        JPanel addRemovePanel = new JPanel();
        GridBagConstraints arCons = new GridBagConstraints();
        arCons.insets = new Insets(0, 0, 5, 0);
        arCons.fill = GridBagConstraints.HORIZONTAL;
        arCons.gridx = 0;
        arCons.gridy = 5;
        arCons.gridwidth = 2;
        panel.add(addRemovePanel, arCons);
        addRemovePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        BnButton addButton = GuiComponentUtils.createBnButton(GuiIcon.ADD, 2);
        addRemovePanel.add(addButton);
        addButton.setModelProvider(provider);
        addButton.setPath(new Path("add"));
        addRemovePanel.add(Box.createHorizontalStrut(5));
        BnButton removeButton = GuiComponentUtils.createBnButton(GuiIcon.REMOVE, 2);
        addRemovePanel.add(removeButton);
        removeButton.setModelProvider(provider);
        removeButton.setPath(new Path("remove"));

        Component verticalGlue = Box.createVerticalGlue();
        GridBagConstraints vgCons = new GridBagConstraints();
        vgCons.fill = GridBagConstraints.VERTICAL;
        vgCons.insets = new Insets(0, 0, 0, 5);
        vgCons.gridx = 0;
        vgCons.gridy = 6;
        panel.add(verticalGlue, vgCons);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));
    }
}
