package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;

import javax.swing.JDialog;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;

import net.parostroj.timetable.gui.GuiContext;
import net.parostroj.timetable.gui.GuiContextComponent;
import net.parostroj.timetable.gui.pm.OutputPM;

public class EditOutputDialog extends JDialog implements View<OutputPM>, ModelSubscriber, GuiContextComponent {

    private Link link = new Link(this);
    private ModelProvider provider = new ModelProvider();

    public EditOutputDialog(Window window, boolean modal) {
        super(window, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
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
