package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.pm.CompanyPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Company;

import org.beanfabrics.ModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

/**
 * Dialog for editing company.
 *
 * @author jub
 */
public class EditCompanyDialog extends JDialog {

    private final ModelProvider provider = new ModelProvider(CompanyPM.class);

    public EditCompanyDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        CompanyPM model = new CompanyPM();
        provider.setPresentationModel(model);
        pack();
        this.setResizable(false);
    }

    public void showDialog(Company company) {
        provider.<CompanyPM> getPresentationModel().init(company);
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
        gbLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
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

        abbrTextField.setModelProvider(provider);
        abbrTextField.setPath(new Path("abbr"));
        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("name"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("ok"));
    }
}
