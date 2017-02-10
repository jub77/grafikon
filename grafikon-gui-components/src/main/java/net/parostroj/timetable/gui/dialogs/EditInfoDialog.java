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
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.TrainDiagram;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.*;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JLabel;

/**
 * Dialog for editing additional information - routes, validity.
 *
 * @author jub
 */
public class EditInfoDialog extends javax.swing.JDialog implements View<InfoPM> {

    private final ModelProvider provider = new ModelProvider(InfoPM.class);
    private JTabbedPane tabbedPane;

    /** Creates new form EditInfoDialog */
    public EditInfoDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        InfoPM model = new InfoPM();
        provider.setPresentationModel(model);
        initComponents();
        model.addPropertyChangeListener("isRouteInfo", event -> tabbedPane.setEnabledAt(1, model.isRouteInfo()));
    }

    public void showDialog(TrainDiagram diagram) {
        ((InfoPM) this.provider.getPresentationModel()).init(diagram);
        this.setVisible(true);
    }

    private void initComponents() {
        Font font = new JTextField().getFont();

        javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        BnButton okButton = new BnButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        ActionListener closeListener = evt -> setVisible(false);

        setTitle(ResourceLoader.getString("info.title")); // NOI18N
        getContentPane().setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        javax.swing.JPanel dataPanel = createDataPanel();

        tabbedPane.addTab(ResourceLoader.getString("info.info"), dataPanel);

        JPanel routePanel = createRoutePanel(font);

        tabbedPane.addTab(ResourceLoader.getString("info.routes.overwrite"), routePanel);
        tabbedPane.setEnabledAt(1, false);

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(closeListener);
        buttonsPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(closeListener);
        buttonsPanel.add(cancelButton);

        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));

        pack();
    }

    private javax.swing.JPanel createDataPanel() {
        java.awt.GridBagConstraints gridBagConstraints;
        javax.swing.JPanel dataPanel = new javax.swing.JPanel();

        GridBagLayout gbl_dataPanel = new GridBagLayout();
        dataPanel.setLayout(gbl_dataPanel);

        GridBagConstraints gbc_infoLabel = new GridBagConstraints();
        gbc_infoLabel.anchor = GridBagConstraints.WEST;
        gbc_infoLabel.insets = new Insets(3, 3, 2, 0);
        gbc_infoLabel.gridx = 0;
        gbc_infoLabel.gridy = 0;
        dataPanel.add(new javax.swing.JLabel(ResourceLoader.getString("info.description")), gbc_infoLabel); // NOI18N

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane();

        BnTextArea infoTextArea = new BnTextArea();
        infoTextArea.setColumns(35);
        infoTextArea.setRows(4);
        scrollPane.setViewportView(infoTextArea);

        GridBagConstraints gbc_scrollPane = new GridBagConstraints();
        gbc_scrollPane.weighty = 1.0;
        gbc_scrollPane.weightx = 1.0;
        gbc_scrollPane.anchor = GridBagConstraints.WEST;
        gbc_scrollPane.insets = new Insets(0, 5, 5, 5);
        gbc_scrollPane.fill = GridBagConstraints.BOTH;
        gbc_scrollPane.gridx = 0;
        gbc_scrollPane.gridy = 1;
        dataPanel.add(scrollPane, gbc_scrollPane);

        BnCheckBox routeInfoCheckBox = new BnCheckBox();
        routeInfoCheckBox.setText(ResourceLoader.getString("info.route.enabled")); // NOI18N
        GridBagConstraints gbc_routeInfoCheckBox = new GridBagConstraints();
        gbc_routeInfoCheckBox.anchor = GridBagConstraints.WEST;
        gbc_routeInfoCheckBox.insets = new Insets(0, 0, 5, 0);
        gbc_routeInfoCheckBox.gridx = 0;
        gbc_routeInfoCheckBox.gridy = 4;
        dataPanel.add(routeInfoCheckBox, gbc_routeInfoCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 5, 5, 5);
        dataPanel.add(new javax.swing.JLabel(ResourceLoader.getString("info.validity")), gridBagConstraints);
        BnTextField validityTextField = new BnTextField();

        validityTextField.setColumns(25);
        java.awt.GridBagConstraints gridBagConstraints_4 = new java.awt.GridBagConstraints();
        gridBagConstraints_4.weightx = 1.0;
        gridBagConstraints_4.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints_4.gridx = 0;
        gridBagConstraints_4.gridy = 3;
        gridBagConstraints_4.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints_4.insets = new Insets(0, 5, 5, 5);
        dataPanel.add(validityTextField, gridBagConstraints_4);
        infoTextArea.setFont(validityTextField.getFont());

        infoTextArea.setModelProvider(provider);
        infoTextArea.setPath(new Path("info"));
        routeInfoCheckBox.setModelProvider(provider);
        routeInfoCheckBox.setPath(new Path("isRouteInfo"));
        validityTextField.setModelProvider(provider);
        validityTextField.setPath(new Path("validity"));
        return dataPanel;
    }

    private JPanel createRoutePanel(Font font) {
        JPanel routePanel = new JPanel();
        GridBagLayout gbl_routePanel = new GridBagLayout();
        routePanel.setLayout(gbl_routePanel);
        JLabel label = new JLabel(ResourceLoader.getString("info.route.number"));
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.anchor = GridBagConstraints.NORTHWEST;
        gbc_label.insets = new Insets(0, 5, 0, 5);
        gbc_label.gridx = 0;
        gbc_label.gridy = 0;
        routePanel.add(label, gbc_label);

        BnTextAreaGrey routeNumberTextArea = new BnTextAreaGrey();
        routeNumberTextArea.setColumns(35);
        routeNumberTextArea.setRows(3);
        javax.swing.JScrollPane scrollPane1 = new javax.swing.JScrollPane();
        GridBagConstraints gbc_scrollPane1 = new GridBagConstraints();
        gbc_scrollPane1.anchor = GridBagConstraints.NORTHWEST;
        gbc_scrollPane1.insets = new Insets(0, 5, 0, 5);
        gbc_scrollPane1.gridx = 0;
        gbc_scrollPane1.gridy = 1;
        gbc_scrollPane1.weightx = 1.0;
        gbc_scrollPane1.fill = GridBagConstraints.HORIZONTAL;
        routePanel.add(scrollPane1, gbc_scrollPane1);
        scrollPane1.setViewportView(routeNumberTextArea);
        routeNumberTextArea.setFont(font);

        JLabel label_1 = new JLabel(ResourceLoader.getString("info.routes"));
        GridBagConstraints gbc_label_1 = new GridBagConstraints();
        gbc_label_1.anchor = GridBagConstraints.NORTHWEST;
        gbc_label_1.insets = new Insets(0, 5, 0, 5);
        gbc_label_1.gridx = 0;
        gbc_label_1.gridy = 2;
        routePanel.add(label_1, gbc_label_1);
        javax.swing.JScrollPane scrollPane2 = new javax.swing.JScrollPane();
        GridBagConstraints gbc_scrollPane2 = new GridBagConstraints();
        gbc_scrollPane2.weighty = 1.0;
        gbc_scrollPane2.insets = new Insets(0, 5, 5, 5);
        gbc_scrollPane2.anchor = GridBagConstraints.NORTHWEST;
        gbc_scrollPane2.gridx = 0;
        gbc_scrollPane2.gridy = 3;
        gbc_scrollPane2.weightx = 1.0;
        gbc_scrollPane2.fill = GridBagConstraints.BOTH;
        routePanel.add(scrollPane2, gbc_scrollPane2);

        BnTextAreaGrey routesTextArea = new BnTextAreaGrey();
        routesTextArea.setColumns(35);
        routesTextArea.setRows(5);
        scrollPane2.setViewportView(routesTextArea);
        routesTextArea.setFont(font);

        routeNumberTextArea.setModelProvider(provider);
        routeNumberTextArea.setPath(new Path("routeNumbers"));
        routesTextArea.setModelProvider(provider);
        routesTextArea.setPath(new Path("routeNodes"));
        return routePanel;
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
