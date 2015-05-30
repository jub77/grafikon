package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.pm.RegionPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Region;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;

/**
 * Dialog for editing region.
 *
 * @author jub
 */
public class EditRegionDialog extends JDialog {

    private final ModelProvider provider = new ModelProvider(RegionPM.class);

    public EditRegionDialog(Window parent, boolean modal, Collection<Locale> locales) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        RegionPM model = new RegionPM(locales);
        provider.setPresentationModel(model);
        pack();
        this.setResizable(false);
    }

    public void showDialog(Region region) {
        provider.<RegionPM> getPresentationModel().init(region);
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

        Component verticalGlue = Box.createVerticalGlue();
        GridBagConstraints vgCons = new GridBagConstraints();
        vgCons.fill = GridBagConstraints.VERTICAL;
        vgCons.insets = new Insets(0, 0, 0, 5);
        vgCons.gridx = 0;
        vgCons.gridy = 2;
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
