package net.parostroj.timetable.gui.components;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.swing.list.BnList;
import org.beanfabrics.swing.list.CellConfig;

import net.parostroj.timetable.gui.pm.LocalizedStringListPM;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.utils.Reference;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class EditLocalizedStringListPanel<T extends Reference<LocalizedString>> extends JPanel implements View<LocalizedStringListPM<T>>, ModelSubscriber {

    private static final long serialVersionUID = 1L;

	private Link link;
    private ModelProvider localModelProvider;
    private BnList list;

    public EditLocalizedStringListPanel(int gap) {
        localModelProvider = new ModelProvider();
        link = new  Link(this);

        setLayout(new BorderLayout(gap, gap));

        EditLocalizedStringPanel localizedStringPanel = new EditLocalizedStringPanel(gap);
        localizedStringPanel.setModelProvider(localModelProvider);
        localizedStringPanel.setPath(new Path("selected"));
        this.add(localizedStringPanel, BorderLayout.CENTER);

        list = new BnList();
        list.setModelProvider(localModelProvider);
        list.setPath(new Path("list"));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellConfig(new CellConfig(new Path()));

        JScrollPane scrolPane = new JScrollPane();
        scrolPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrolPane.setViewportView(list);
        this.add(scrolPane, BorderLayout.WEST);
        setPrototypeWidth(list);
    }

    @SuppressWarnings("unchecked")
    private void setPrototypeWidth(BnList list) {
        list.setPrototypeCellValue(new TextPM("mmmmmmmmmmmmmmmmm"));
    }

    @Override
    public IModelProvider getModelProvider() {
        return this.link.getModelProvider();
    }

    @Override
    public void setModelProvider(IModelProvider provider) {
        this.link.setModelProvider(provider);
    }

    @Override
    public Path getPath() {
        return this.link.getPath();
    }

    @Override
    public void setPath(Path path) {
        this.link.setPath(path);
    }

    @Override
    public LocalizedStringListPM<T> getPresentationModel() {
        return localModelProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(LocalizedStringListPM<T> pModel) {
        localModelProvider.setPresentationModel(pModel);
    }

    public void setMultipleSelection(boolean multipleSelection) {
        this.list.setSelectionMode(multipleSelection ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
                : ListSelectionModel.SINGLE_SELECTION);
    }
}
