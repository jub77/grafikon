/*
 * EditInfoDialog.java
 *
 * Created on 13. říjen 2007, 13:03
 */
package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionListener;

import net.parostroj.timetable.gui.components.BnTextAreaGrey;
import net.parostroj.timetable.gui.pm.InfoPM;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ResourceLoader;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.*;

/**
 * Dialog for editing additional information - routes, validity.
 *
 * @author jub
 */
public class EditInfoDialog extends javax.swing.JDialog implements View<InfoPM> {

    private final ModelProvider provider = new ModelProvider(InfoPM.class);

    /** Creates new form EditInfoDialog */
    public EditInfoDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        provider.setPresentationModel(new InfoPM());
        initComponents();
    }

    public void showDialog(TrainDiagram diagram) {
        ((InfoPM) this.provider.getPresentationModel()).readFromDiagram(diagram);
        this.setVisible(true);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        BnTextArea infoTextArea = new BnTextArea();
        javax.swing.JPanel dataPanel = new javax.swing.JPanel();
        BnTextAreaGrey routeNumberTextArea = new BnTextAreaGrey();
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        BnTextAreaGrey routesTextArea = new BnTextAreaGrey();
        javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
        BnButton okButton = new BnButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        ActionListener closeListener = evt -> setVisible(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("info.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        GridBagLayout gbl_dataPanel = new GridBagLayout();
        dataPanel.setLayout(gbl_dataPanel);

        GridBagConstraints gbc_infoLabel = new GridBagConstraints();
        gbc_infoLabel.anchor = GridBagConstraints.WEST;
        gbc_infoLabel.gridwidth = 2;
        gbc_infoLabel.insets = new Insets(3, 3, 2, 0);
        gbc_infoLabel.gridx = 0;
        gbc_infoLabel.gridy = 0;
        dataPanel.add(new javax.swing.JLabel(ResourceLoader.getString("info.info")), gbc_infoLabel); // NOI18N

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();

        infoTextArea.setColumns(35);
        infoTextArea.setRows(4);
        scrollPane.setViewportView(infoTextArea);

        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.gridwidth = 2;
        gbc_scrollPane.weighty = 1.0;
        gbc_scrollPane.weightx = 1.0;
        gbc_scrollPane.anchor = GridBagConstraints.WEST;
        gbc_scrollPane.insets = new Insets(3, 3, 5, 3);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        dataPanel.add(scrollPane, gbc_scrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 3, 2, 0);
        dataPanel.add(new javax.swing.JLabel(ResourceLoader.getString("info.route.number")), gridBagConstraints); // NOI18N

        routeNumberTextArea.setColumns(35);
        routeNumberTextArea.setRows(3);
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        scrollPane1.setViewportView(routeNumberTextArea);

        java.awt.GridBagConstraints gridBagConstraints_2 = new java.awt.GridBagConstraints();
        gridBagConstraints_2.gridwidth = 2;
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 3;
        gridBagConstraints_2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints_2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_2.weightx = 1.0;
        gridBagConstraints_2.weighty = 1.0;
        gridBagConstraints_2.insets = new Insets(3, 3, 5, 3);
        dataPanel.add(scrollPane1, gridBagConstraints_2);

        java.awt.GridBagConstraints gridBagConstraints_3 = new java.awt.GridBagConstraints();
        gridBagConstraints_3.gridwidth = 2;
        gridBagConstraints_3.gridx = 0;
        gridBagConstraints_3.gridy = 4;
        gridBagConstraints_3.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_3.insets = new Insets(0, 3, 2, 0);
        dataPanel.add(new javax.swing.JLabel(ResourceLoader.getString("info.routes")), gridBagConstraints_3); // NOI18N

        routesTextArea.setColumns(35);
        routesTextArea.setRows(5);
        scrollPane2.setViewportView(routesTextArea);

        java.awt.GridBagConstraints gridBagConstraints_1 = new java.awt.GridBagConstraints();
        gridBagConstraints_1.gridwidth = 2;
        gridBagConstraints_1.gridx = 0;
        gridBagConstraints_1.gridy = 5;
        gridBagConstraints_1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints_1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_1.weightx = 1.0;
        gridBagConstraints_1.weighty = 1.0;
        gridBagConstraints_1.insets = new Insets(3, 3, 5, 3);
        dataPanel.add(scrollPane2, gridBagConstraints_1);

        BnCheckBox routeInfoCheckBox = new BnCheckBox();
        routeInfoCheckBox.setText(ResourceLoader.getString("info.route.enabled")); // NOI18N
        GridBagConstraints gbc_routeInfoCheckBox = new GridBagConstraints();
        gbc_routeInfoCheckBox.gridwidth = 2;
        gbc_routeInfoCheckBox.anchor = GridBagConstraints.WEST;
        gbc_routeInfoCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_routeInfoCheckBox.gridx = 0;
        gbc_routeInfoCheckBox.gridy = 6;
        dataPanel.add(routeInfoCheckBox, gbc_routeInfoCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 3, 5, 5);
        dataPanel.add(new javax.swing.JLabel(ResourceLoader.getString("info.validity") + ":"), gridBagConstraints);  // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(dataPanel, gridBagConstraints);
        BnTextField validityTextField = new BnTextField();

        validityTextField.setColumns(25);
        java.awt.GridBagConstraints gridBagConstraints_4 = new java.awt.GridBagConstraints();
        gridBagConstraints_4.weightx = 1.0;
        gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_4.gridx = 1;
        gridBagConstraints_4.gridy = 7;
        gridBagConstraints_4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_4.insets = new Insets(0, 3, 5, 3);
        dataPanel.add(validityTextField, gridBagConstraints_4);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(closeListener);
        buttonsPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(closeListener);
        buttonsPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(buttonsPanel, gridBagConstraints);

        routeNumberTextArea.setFont(validityTextField.getFont());
        routesTextArea.setFont(validityTextField.getFont());
        infoTextArea.setFont(validityTextField.getFont());

        infoTextArea.setModelProvider(provider);
        infoTextArea.setPath(new Path("info"));
        routeNumberTextArea.setModelProvider(provider);
        routeNumberTextArea.setPath(new Path("routeNumbers"));
        routesTextArea.setModelProvider(provider);
        routesTextArea.setPath(new Path("routeNodes"));
        routeInfoCheckBox.setModelProvider(provider);
        routeInfoCheckBox.setPath(new Path("isRouteInfo"));
        validityTextField.setModelProvider(provider);
        validityTextField.setPath(new Path("validity"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));

        pack();
    }

    @Override
    public InfoPM getPresentationModel() {
        return provider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(InfoPM pModel) {
        provider.setPresentationModel(pModel);
    }
}
