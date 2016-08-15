package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.swing.BnTextField;
import org.beanfabrics.swing.table.BnColumn;
import org.beanfabrics.swing.table.BnTable;

import net.parostroj.timetable.gui.pm.LocalizedStringPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;

public class EditLocalizedStringPanel extends JPanel implements View<LocalizedStringPM>, ModelSubscriber {

    private Link link;
    private ModelProvider localModelProvider;

    public EditLocalizedStringPanel(int gap) {
        localModelProvider = new ModelProvider();
        link = new Link(this);
        setLayout(new BorderLayout(gap, gap));
        JScrollPane scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);

        BnTable table = new BnTable();
        table.addColumn(new BnColumn(new Path("locale"), ResourceLoader.getString("localization.column.locale"), 110, true));
        table.addColumn(new BnColumn(new Path("string"), ResourceLoader.getString("localization.column.text")));
        table.setModelProvider(localModelProvider);
        table.setPath(new Path("strings"));
        table.setSortable(true);
        scrollPane.setViewportView(table);

        BnTextField field = new BnTextField();
        field.setBackground(null);
        field.setModelProvider(localModelProvider);
        field.setPath(new Path("string"));
        this.add(field, BorderLayout.NORTH);
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
    public LocalizedStringPM getPresentationModel() {
        return localModelProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(LocalizedStringPM pModel) {
        this.localModelProvider.setPresentationModel(pModel);
    }
}
