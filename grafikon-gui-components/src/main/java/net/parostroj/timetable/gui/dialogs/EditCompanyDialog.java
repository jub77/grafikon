package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.pm.CompanyPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Company;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;

/**
 * Dialog for editing company.
 *
 * @author jub
 */
public class EditCompanyDialog extends JDialog {

    private final ModelProvider provider = new ModelProvider(CompanyPM.class);

    public EditCompanyDialog(Window parent, boolean modal, Collection<Locale> locales) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        CompanyPM model = new CompanyPM(locales);
        provider.setPresentationModel(model);
        pack();
        this.setResizable(false);
    }

    public void showDialog(Company company) {
        provider.<CompanyPM>getPresentationModel().init(company);
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
        gbLayout.columnWeights = new double[]{0.0, 1.0};
        gbLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
        panel.setLayout(gbLayout);

        JLabel abbrLabel = new JLabel(ResourceLoader.getString("edit.company.abbreviation")); // NOI18N
        GridBagConstraints nlCons = new GridBagConstraints();
        nlCons.anchor = GridBagConstraints.WEST;
        nlCons.insets = new Insets(0, 0, 5, 5);
        nlCons.gridx = 0;
        nlCons.gridy = 0;
        panel.add(abbrLabel, nlCons);

        BnTextField abbrTextField = new BnTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 0;
        panel.add(abbrTextField, gbc_nameTextField);
        abbrTextField.setColumns(10);

        JLabel nameLabel = new JLabel(ResourceLoader.getString("edit.company.name")); // NOI18N
        GridBagConstraints dlCons = new GridBagConstraints();
        dlCons.anchor = GridBagConstraints.WEST;
        dlCons.insets = new Insets(0, 0, 5, 5);
        dlCons.gridx = 0;
        dlCons.gridy = 1;
        panel.add(nameLabel, dlCons);

        BnTextField nameTextField = new BnTextField();
        GridBagConstraints dtfCons = new GridBagConstraints();
        dtfCons.insets = new Insets(0, 0, 5, 0);
        dtfCons.anchor = GridBagConstraints.NORTH;
        dtfCons.fill = GridBagConstraints.HORIZONTAL;
        dtfCons.gridx = 1;
        dtfCons.gridy = 1;
        panel.add(nameTextField, dtfCons);
        nameTextField.setColumns(30);

        JLabel partLabel = new JLabel(ResourceLoader.getString("edit.company.part.name")); // NOI18N
        GridBagConstraints plCons = new GridBagConstraints();
        plCons.anchor = GridBagConstraints.WEST;
        plCons.insets = new Insets(0, 0, 5, 5);
        plCons.gridx = 0;
        plCons.gridy = 2;
        panel.add(partLabel, plCons);

        BnTextField partTextField = new BnTextField();
        partTextField.setColumns(30);
        GridBagConstraints ptfCons = new GridBagConstraints();
        ptfCons.insets = new Insets(0, 0, 5, 0);
        ptfCons.fill = GridBagConstraints.HORIZONTAL;
        ptfCons.gridx = 1;
        ptfCons.gridy = 2;
        panel.add(partTextField, ptfCons);

        JLabel localeLabel = new JLabel(ResourceLoader.getString("edit.company.locale")); // NOI18N
        GridBagConstraints llCons = new GridBagConstraints();
        llCons.anchor = GridBagConstraints.WEST;
        llCons.insets = new Insets(0, 0, 5, 5);
        llCons.gridx = 0;
        llCons.gridy = 3;
        panel.add(localeLabel, llCons);

        BnComboBox localeComboBox = new BnComboBox();
        GridBagConstraints ltfCons = new GridBagConstraints();
        ltfCons.insets = new Insets(0, 0, 5, 0);
        ltfCons.fill = GridBagConstraints.HORIZONTAL;
        ltfCons.gridx = 1;
        ltfCons.gridy = 3;
        panel.add(localeComboBox, ltfCons);

        Component verticalGlue = Box.createVerticalGlue();
        GridBagConstraints vgCons = new GridBagConstraints();
        vgCons.fill = GridBagConstraints.VERTICAL;
        vgCons.insets = new Insets(0, 0, 0, 5);
        vgCons.gridx = 0;
        vgCons.gridy = 4;
        panel.add(verticalGlue, vgCons);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        abbrTextField.setModelProvider(provider);
        abbrTextField.setPath(new Path("abbr"));
        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("name"));
        localeComboBox.setModelProvider(provider);
        localeComboBox.setPath(new Path("locale"));
        partTextField.setModelProvider(provider);
        partTextField.setPath(new Path("part"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));
    }
}
