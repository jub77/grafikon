package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.OutputPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;

public class NewOutputDialog extends JDialog implements View<OutputPM>, ModelSubscriber {

    private ModelProvider provider;
    private Link link;

    public NewOutputDialog(Window window, boolean modal) {
        super(window, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));

        ActionListener closeListener = evt -> this.setVisible(false);

        provider = new ModelProvider();
        link = new Link(this);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        BnButton okButton = new BnButton();
        okButton.setText(ResourceLoader.getString("button.ok"));
        okButton.setModelProvider(provider);
        okButton.setPath(new Path("create"));
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton();
        cancelButton.setText(ResourceLoader.getString("button.cancel"));
        buttonPanel.add(cancelButton);

        JPanel dataPanel = new JPanel();
        getContentPane().add(dataPanel, BorderLayout.CENTER);
        GridBagLayout layout = new GridBagLayout();
        dataPanel.setLayout(layout);

        JLabel nameLabel = new JLabel(ResourceLoader.getString("output.name"));
        GridBagConstraints nameConstraints = new GridBagConstraints();
        nameConstraints.anchor = GridBagConstraints.WEST;
        nameConstraints.insets = new Insets(5, 5, 5, 5);
        nameConstraints.gridx = 0;
        nameConstraints.gridy = 0;
        dataPanel.add(nameLabel, nameConstraints);

        BnTextField nameTextField = new BnTextField();
        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("name.current"));
        GridBagConstraints nameSelectConstraints = new GridBagConstraints();
        nameSelectConstraints.weightx = 1.0;
        nameSelectConstraints.insets = new Insets(5, 0, 5, 5);
        nameSelectConstraints.fill = GridBagConstraints.HORIZONTAL;
        nameSelectConstraints.gridx = 1;
        nameSelectConstraints.gridy = 0;
        dataPanel.add(nameTextField, nameSelectConstraints);
        nameTextField.setColumns(40);

        JLabel templateLabel = new JLabel(ResourceLoader.getString("output.template"));
        GridBagConstraints templateConstraints = new GridBagConstraints();
        templateConstraints.anchor = GridBagConstraints.WEST;
        templateConstraints.insets = new Insets(0, 5, 0, 5);
        templateConstraints.gridx = 0;
        templateConstraints.gridy = 1;
        dataPanel.add(templateLabel, templateConstraints);

        BnComboBox templateComboBoxField = new BnComboBox();
        templateComboBoxField.setModelProvider(provider);
        templateComboBoxField.setPath(new Path("templates"));
        GridBagConstraints templateSelectConstraints = new GridBagConstraints();
        templateSelectConstraints.insets = new Insets(0, 0, 0, 5);
        templateSelectConstraints.weightx = 1.0;
        templateSelectConstraints.fill = GridBagConstraints.HORIZONTAL;
        templateSelectConstraints.gridx = 1;
        templateSelectConstraints.gridy = 1;
        dataPanel.add(templateComboBoxField, templateSelectConstraints);

        okButton.addActionListener(closeListener);
        cancelButton.addActionListener(closeListener);

        this.setResizable(false);
        pack();
    }

    @Override
    public OutputPM getPresentationModel() {
        return provider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(OutputPM pModel) {
        provider.setPresentationModel(pModel);
    }

    @Override
    public IModelProvider getModelProvider() {
        return link.getModelProvider();
    }

    @Override
    public void setModelProvider(IModelProvider provider) {
        link.setModelProvider(provider);
    }

    @Override
    public Path getPath() {
        return link.getPath();
    }

    @Override
    public void setPath(Path path) {
        link.setPath(path);
    }
}
