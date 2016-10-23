package net.parostroj.timetable.gui.components;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnTextField;

import net.parostroj.timetable.gui.dialogs.EditLocalizedStringDialog;
import net.parostroj.timetable.gui.pm.LocalizedStringDefaultPM;
import net.parostroj.timetable.gui.pm.LocalizedStringPM;
import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;

public class LocalizedStringField<T extends LocalizedStringPM> extends JPanel implements View<T>, ModelSubscriber {

    private final ModelProvider localProvider = new ModelProvider();
    private final Link link = new Link(this);
    private final BnTextField textField;
    private final boolean doNotUseCurrent;
    private EditLocalizedStringDialog editDialog;

    public LocalizedStringField() {
        this(false);
    }

    public LocalizedStringField(boolean doNotUseCurrent) {
        this.doNotUseCurrent = doNotUseCurrent;
        textField = new BnTextField();
        textField.setColumns(10);

        textField.setModelProvider(localProvider);
        textField.setPath(new Path("string"));

        JButton editButton = GuiComponentUtils.createButton(GuiIcon.EDIT, 2);
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addComponent(textField, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(editButton))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
                    .addComponent(editButton)
                    .addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
        setLayout(groupLayout);

        editButton.addActionListener(evt -> {
            if (editDialog == null) {
                editDialog = new EditLocalizedStringDialog(GuiComponentUtils.getWindow(LocalizedStringField.this), true);
                editDialog.setModelProvider(localProvider);
                editDialog.setPath(new Path("this"));
                editDialog.pack();
            }
            editDialog.setLocation(editButton.getLocationOnScreen());
            editDialog.setVisible(true);
        });

    }

    public void setColumns(int columns) {
        textField.setColumns(columns);
    }

    @Override
    public IModelProvider getModelProvider() {
        return null;
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
    public T getPresentationModel() {
        return localProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(T pModel) {
        localProvider.setPresentationModel(pModel);
        if (pModel instanceof LocalizedStringDefaultPM && !doNotUseCurrent) {
            textField.setPath(new Path("current"));
        } else {
            textField.setPath(new Path("string"));
        }
    }
}
