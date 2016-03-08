package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * List of train type categories.
 *
 * @author jub
 */
public class TrainTypesCategoriesNewDialog extends javax.swing.JDialog {

    private final JTextField nameTextField;
    private final JComboBox<String> keyTextField;
    private final JComboBox<Wrapper<TrainTypeCategory>> templateComboBox;

    private TrainTypeCategory newCategory;
    private TrainTypeCategory templateCategory;

    private final TrainTypeCategory noneCategory = new TrainTypeCategory(null, ResourceLoader.getString("new.traintypes.categories.template.none"), null);

    public TrainTypesCategoriesNewDialog(Window owner) {
        super(owner, ModalityType.APPLICATION_MODAL);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JPanel okCancelPanel = new JPanel();
        getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
        okCancelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        JButton button = new JButton(ResourceLoader.getString("button.ok"));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (createResult()) {
                    setVisible(false);
                } else {
                    GuiComponentUtils.showError(ResourceLoader.getString("dialog.error.incorrect.values"), TrainTypesCategoriesNewDialog.this);
                }
            }
        });
        okCancelPanel.add(button);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        okCancelPanel.add(cancelButton);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[]{0, 0, 0};
        gbl_panel.rowHeights = new int[]{0, 0, 0, 0};
        gbl_panel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel templateLabel = new JLabel(ResourceLoader.getString("new.traintypes.categories.template") + ":");
        GridBagConstraints gbc_templateLabel = new GridBagConstraints();
        gbc_templateLabel.insets = new Insets(0, 0, 5, 5);
        gbc_templateLabel.anchor = GridBagConstraints.WEST;
        gbc_templateLabel.gridx = 0;
        gbc_templateLabel.gridy = 0;
        panel.add(templateLabel, gbc_templateLabel);

        templateComboBox = new JComboBox<Wrapper<TrainTypeCategory>>();
        GridBagConstraints gbc_templateComboBox = new GridBagConstraints();
        gbc_templateComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_templateComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_templateComboBox.gridx = 1;
        gbc_templateComboBox.gridy = 0;
        panel.add(templateComboBox, gbc_templateComboBox);

        JLabel nameLabel = new JLabel(ResourceLoader.getString("new.traintypes.categories.name") + ":");
        GridBagConstraints gbc_nameLabel = new GridBagConstraints();
        gbc_nameLabel.anchor = GridBagConstraints.WEST;
        gbc_nameLabel.insets = new Insets(0, 0, 5, 5);
        gbc_nameLabel.gridx = 0;
        gbc_nameLabel.gridy = 1;
        panel.add(nameLabel, gbc_nameLabel);

        nameTextField = new JTextField();
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(0, 0, 5, 0);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 1;
        panel.add(nameTextField, gbc_nameTextField);
        nameTextField.setColumns(10);

        JLabel keyLabel = new JLabel(ResourceLoader.getString("new.traintypes.categories.key") + ":");
        GridBagConstraints gbc_keyLabel = new GridBagConstraints();
        gbc_keyLabel.anchor = GridBagConstraints.WEST;
        gbc_keyLabel.insets = new Insets(0, 0, 0, 5);
        gbc_keyLabel.gridx = 0;
        gbc_keyLabel.gridy = 2;
        panel.add(keyLabel, gbc_keyLabel);

        keyTextField = new JComboBox<String>();
        keyTextField.addItem("passenger");
        keyTextField.addItem("freight");
        keyTextField.setEditable(true);
        GridBagConstraints gbc_keyTextField = new GridBagConstraints();
        gbc_keyTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_keyTextField.gridx = 1;
        gbc_keyTextField.gridy = 2;
        panel.add(keyTextField, gbc_keyTextField);

        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        pack();
    }

    protected boolean createResult() {
        String name = nameTextField.getText().trim();
        String key = ((String) keyTextField.getSelectedItem()).trim();
        if (name.equals("") || key.equals("")) {
            return false;
        } else {
            newCategory = new TrainTypeCategory(IdGenerator.getInstance().getId(), name, key);
            TrainTypeCategory template = (TrainTypeCategory) ((Wrapper<?>) templateComboBox.getSelectedItem()).getElement();
            if (template != noneCategory) {
                templateCategory = template;
            }
            return true;
        }
    }

    public void setVisible(TrainDiagram diagram) {
        templateComboBox.removeAllItems();
        templateComboBox.addItem(new Wrapper<TrainTypeCategory>(noneCategory));
        for (TrainTypeCategory cat : diagram.getPenaltyTable().getTrainTypeCategories()) {
            templateComboBox.addItem(new Wrapper<TrainTypeCategory>(cat));
        }
        templateComboBox.setMaximumRowCount(Math.min(10, templateComboBox.getItemCount()));
        nameTextField.setText("");
        keyTextField.setSelectedItem("passenger");
        newCategory = null;
        templateCategory = null;

        setVisible(true);
    }

    public TrainTypeCategory getTemplateCategory() {
        return templateCategory;
    }

    public TrainTypeCategory getNewCategory() {
        return newCategory;
    }
}
