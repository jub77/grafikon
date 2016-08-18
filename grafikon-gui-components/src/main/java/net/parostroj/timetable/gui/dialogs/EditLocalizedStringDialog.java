package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;

import net.parostroj.timetable.gui.components.EditLocalizedStringPanel;
import net.parostroj.timetable.gui.pm.LocalizedStringPM;

public class EditLocalizedStringDialog extends JDialog implements ModelSubscriber, View<LocalizedStringPM> {

    private ModelProvider localProvider;
    private Link link;

    public EditLocalizedStringDialog(Window owner, boolean modal) {
        super(owner, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        link = new Link(this);
        localProvider = new ModelProvider();
        this.setLayout(new BorderLayout());

        EditLocalizedStringPanel editStringPanel = new EditLocalizedStringPanel(5);
        editStringPanel.setModelProvider(localProvider);
        editStringPanel.setPath(new Path("this"));
        editStringPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        this.getContentPane().add(editStringPanel, BorderLayout.CENTER);
        this.pack();
    }

    @Override
    public LocalizedStringPM getPresentationModel() {
        return localProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(LocalizedStringPM pModel) {
        localProvider.setPresentationModel(pModel);
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
