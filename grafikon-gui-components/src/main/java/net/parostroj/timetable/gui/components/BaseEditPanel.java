package net.parostroj.timetable.gui.components;

import javax.swing.JPanel;

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
public class BaseEditPanel<T extends PresentationModel> extends JPanel implements ModelSubscriber, View<T> {

    private static final long serialVersionUID = 1L;

    protected ModelProvider localProvider;
    protected Link link;

    public BaseEditPanel() {
        this.localProvider = new ModelProvider();
        this.link = new Link(this);
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
