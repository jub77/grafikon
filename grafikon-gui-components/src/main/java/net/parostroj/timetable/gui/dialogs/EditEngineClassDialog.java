package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnButton;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.pm.EngineClassPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog for editing engine class.
 *
 * @author jub
 */
public class EditEngineClassDialog extends JDialog implements View<EngineClassPM>, ModelSubscriber {

    private final ModelProvider localProvider = new ModelProvider(EngineClassPM.class);
    private final Link link = new Link(this);

    public EditEngineClassDialog(Window parent, boolean modal) {
        super(parent, modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        initComponents();
        pack();
        this.setResizable(false);
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
        panel.setLayout(gbLayout);

        JLabel nameLabel = new JLabel(ResourceLoader.getString("edit.engine.class.name")); // NOI18N
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
        nameTextField.setModelProvider(localProvider);
        nameTextField.setPath(new Path("name"));

        JLabel localeLabel = new JLabel(ResourceLoader.getString("edit.engine.class.group.key")); // NOI18N
        GridBagConstraints llCons = new GridBagConstraints();
        llCons.anchor = GridBagConstraints.WEST;
        llCons.insets = new Insets(0, 0, 5, 5);
        llCons.gridx = 0;
        llCons.gridy = 1;
        panel.add(localeLabel, llCons);

        BnTextField groupKeyTextField = new BnTextField();
        GridBagConstraints ltfCons = new GridBagConstraints();
        ltfCons.insets = new Insets(0, 0, 5, 0);
        ltfCons.fill = GridBagConstraints.HORIZONTAL;
        ltfCons.gridx = 1;
        ltfCons.gridy = 1;
        panel.add(groupKeyTextField, ltfCons);
        groupKeyTextField.setModelProvider(localProvider);
        groupKeyTextField.setPath(new Path("groupKey"));

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
        okButton.setModelProvider(localProvider);
        okButton.setPath(new Path("ok"));
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
    public EngineClassPM getPresentationModel() {
        return localProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(EngineClassPM pModel) {
        localProvider.setPresentationModel(pModel);
    }
}
