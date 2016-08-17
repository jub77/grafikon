package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;

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
import org.beanfabrics.swing.BnComboBox;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.components.AttributesPanel;
import net.parostroj.timetable.gui.pm.OutputPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;

import javax.swing.JLabel;

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
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
        contentPanel.setLayout(gridBagLayout);

        JLabel nameLabel = new JLabel(ResourceLoader.getString("output.name"));
        GridBagConstraints gbc_lblNameLabel = new GridBagConstraints();
        gbc_lblNameLabel.insets = new Insets(5, 5, 5, 5);
        gbc_lblNameLabel.anchor = GridBagConstraints.WEST;
        gbc_lblNameLabel.gridx = 0;
        gbc_lblNameLabel.gridy = 0;
        contentPanel.add(nameLabel, gbc_lblNameLabel);

        BnTextField nameTextField = new BnTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.weightx = 1.0;
        gbc_nameTextField.insets = new Insets(5, 0, 5, 5);
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 0;
        contentPanel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);

        JLabel lblLocaleLabel = new JLabel(ResourceLoader.getString("output.locale"));
        GridBagConstraints gbc_lblLocaleLabel = new GridBagConstraints();
        gbc_lblLocaleLabel.anchor = GridBagConstraints.WEST;
        gbc_lblLocaleLabel.insets = new Insets(0, 5, 5, 5);
        gbc_lblLocaleLabel.gridx = 0;
        gbc_lblLocaleLabel.gridy = 1;
        contentPanel.add(lblLocaleLabel, gbc_lblLocaleLabel);

        BnComboBox localeComboBox = new BnComboBox();
        GridBagConstraints gbc_localeComboBox = new GridBagConstraints();
        gbc_localeComboBox.weightx = 1.0;
        gbc_localeComboBox.insets = new Insets(0, 0, 5, 5);
        gbc_localeComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_localeComboBox.gridx = 1;
        gbc_localeComboBox.gridy = 1;
        contentPanel.add(localeComboBox, gbc_localeComboBox);
        localeComboBox.setModelProvider(provider);
        localeComboBox.setPath(new Path("locale"));

        JLabel lblKeyLabel = new JLabel(ResourceLoader.getString("output.key"));
        GridBagConstraints gbc_lblKeyLabel = new GridBagConstraints();
        gbc_lblKeyLabel.insets = new Insets(0, 5, 5, 5);
        gbc_lblKeyLabel.anchor = GridBagConstraints.WEST;
        gbc_lblKeyLabel.gridx = 0;
        gbc_lblKeyLabel.gridy = 2;
        contentPanel.add(lblKeyLabel, gbc_lblKeyLabel);

        BnTextField keyTextField = new BnTextField();
        GridBagConstraints gbc_keyTextField = new GridBagConstraints();
        gbc_keyTextField.insets = new Insets(0, 0, 5, 5);
        gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_keyTextField.gridx = 1;
        gbc_keyTextField.gridy = 2;
        contentPanel.add(keyTextField, gbc_keyTextField);
        keyTextField.setModelProvider(provider);
        keyTextField.setPath(new Path("key"));

        AttributesPanel attributesPanel = new AttributesPanel();
        attributesPanel.setEnabledAddRemove(false);
        GridBagConstraints gbc_attributes = new GridBagConstraints();
        gbc_attributes.gridwidth = 2;
        gbc_attributes.insets = new Insets(0, 5, 5, 5);
        gbc_attributes.fill = GridBagConstraints.BOTH;
        gbc_attributes.gridx = 0;
        gbc_attributes.gridy = 3;
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

        JPanel panel = new JPanel();
        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.weightx = 1.0;
        gbc_panel.gridwidth = 2;
        gbc_panel.insets = new Insets(0, 5, 0, 5);
        gbc_panel.fill = GridBagConstraints.HORIZONTAL;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 4;
        contentPanel.add(panel, gbc_panel);
        panel.setLayout(new BorderLayout(0, 0));

        BnTextField selectionTextField = new BnTextField();
        panel.add(selectionTextField, BorderLayout.CENTER);
        selectionTextField.setColumns(10);
        selectionTextField.setModelProvider(provider);
        selectionTextField.setPath(new Path("selection"));

        BnButton selectionButton = GuiComponentUtils.createBnButton(GuiIcon.EDIT, 2);
        panel.add(selectionButton, BorderLayout.EAST);
        selectionButton.setModelProvider(provider);
        selectionButton.setPath(new Path("editSelection"));

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
    public void setPresentationModel(final OutputPM pModel) {
        provider.setPresentationModel(pModel);
        pModel.setOperationEditSelection(() -> {
            Collection<? extends ObjectWithId> currentSelection = pModel.getSelection();
            ElementSelectionDialog<ObjectWithId> dialog = new ElementSelectionDialog<>(this, true);
            OutputTemplate template = pModel.getEditedOutput().getTemplate();
            Collection<ObjectWithId> collection = template.getSelectionType().extract(template.getDiagram(),
                    ObjectWithId.class);
            dialog.setLocationRelativeTo(this);
            List<ObjectWithId> newSelection = dialog.selectElements(collection, currentSelection);
            if (newSelection != null) {
                if (newSelection.isEmpty()) {
                    newSelection = null;
                }
                pModel.updateSelection(newSelection);
            }
            return true;
        });
    }

    @Override
    public void registerContext(GuiContext context) {
        context.registerWindow("output.edit", this);
    }
}
