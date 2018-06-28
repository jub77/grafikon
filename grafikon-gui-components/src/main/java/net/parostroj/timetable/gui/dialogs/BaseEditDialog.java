package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;

import javax.swing.JDialog;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.model.PresentationModel;

/**
 * Base dialog for editing items of model using presentation model.
 *
 * @author jub
 *
 * @param <T> content of the dialog
 */
public class BaseEditDialog<T extends PresentationModel> extends JDialog implements ModelSubscriber, View<T> {

    private static final long serialVersionUID = 1L;

    protected ModelProvider localProvider;
    protected Link link;

    public BaseEditDialog(Window owner, boolean modal) {
        super(owner, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
        this.link = new Link(this);
        this.localProvider = new ModelProvider();
    }

    @Override
    public T getPresentationModel() {
        return localProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(T pModel) {
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
