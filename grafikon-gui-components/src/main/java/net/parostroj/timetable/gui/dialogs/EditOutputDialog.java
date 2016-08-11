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
import javax.swing.JPanel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.components.AttributesPanel;
import net.parostroj.timetable.gui.pm.OutputPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;

public class EditOutputDialog extends JDialog implements View<OutputPM>, ModelSubscriber, GuiContextComponent {

    private Link link = new Link(this);
    private ModelProvider provider = new ModelProvider();

    public EditOutputDialog(Window window, boolean modal) {
        super(window, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        ActionListener closeListener = evt -> setVisible(false);

        JPanel contentPanel = new JPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        GridBagLayout gridBagLayout = new GridBagLayout();
        contentPanel.setLayout(gridBagLayout);

        BnTextField nameTextField = new BnTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.weightx = 1.0;
        gbc_nameTextField.insets = new Insets(5, 5, 5, 5);
        gbc_nameTextField.gridx = 0;
        gbc_nameTextField.gridy = 0;
        contentPanel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);

        AttributesPanel attributesPanel = new AttributesPanel();
        attributesPanel.setEnabledAddRemove(false);
        GridBagConstraints gbc_attributes = new GridBagConstraints();
        gbc_attributes.insets = new Insets(0, 5, 5, 5);
        gbc_attributes.fill = GridBagConstraints.BOTH;
        gbc_attributes.gridx = 0;
        gbc_attributes.gridy = 1;
        gbc_attributes.weightx = 1.0;
        gbc_attributes.weighty = 1.0;
        contentPanel.add(attributesPanel, gbc_attributes);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        BnButton okButton = new BnButton();
        buttonPanel.add(okButton);
        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(closeListener);

        okButton.setModelProvider(provider);
        okButton.setPath(new Path("writeBack"));

        JButton cancelButton = new JButton();
        buttonPanel.add(cancelButton);
        cancelButton.setText(ResourceLoader.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(closeListener);

        nameTextField.setModelProvider(provider);
        nameTextField.setPath(new Path("name"));

        attributesPanel.setModelProvider(provider);
        attributesPanel.setPath(new Path("attributes"));

        pack();
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

    @Override
    public OutputPM getPresentationModel() {
        return provider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(OutputPM pModel) {
        provider.setPresentationModel(pModel);
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("output.edit", this);
    }
}
