package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Link;
import org.beanfabrics.ModelProvider;
import org.beanfabrics.ModelSubscriber;
import org.beanfabrics.Path;
import org.beanfabrics.View;
import org.beanfabrics.event.WeakPropertyChangeListener;
import org.beanfabrics.model.ITextPM;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.swing.BnTextArea;
import org.beanfabrics.swing.list.BnList;
import org.beanfabrics.swing.list.CellConfig;

import net.parostroj.timetable.gui.pm.LocalizedStringPM;

public class EditLocalizedStringMultilinePanel extends JPanel implements View<LocalizedStringPM>, ModelSubscriber {

    private Link link;
    private ModelProvider localModelProvider;
    private WeakPropertyChangeListener listener;

    public EditLocalizedStringMultilinePanel(int gap, int lines) {
        localModelProvider = new ModelProvider();
        link = new Link(this);
        setLayout(new BorderLayout(gap, gap));

        JScrollPane areaScrollPane = new JScrollPane();
        areaScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        BnTextArea area = new BnTextArea();
        area.setModelProvider(localModelProvider);
        area.setPath(new Path("string"));
        area.setLineWrap(true);
        area.setRows(lines);
        area.setColumns(40);
        areaScrollPane.setViewportView(area);
        this.add(areaScrollPane, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(gap, gap));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        BnTextAreaGrey localizedArea = new BnTextAreaGrey();
        scrollPane.setViewportView(localizedArea);
        localizedArea.setLineWrap(true);
        localizedArea.setEditable(false);

        JScrollPane listScrollPane = new JScrollPane();
        listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(listScrollPane, BorderLayout.WEST);

        BnList list = new BnList();
        listScrollPane.setViewportView(list);
        list.setModelProvider(localModelProvider);
        Path stringsPath = new Path("strings");
        list.setPath(stringsPath);
        list.setCellConfig(new CellConfig(new Path("locale")));
        this.setPrototypeWidth(list);

        localizedArea.setFont(list.getFont());
        area.setFont(list.getFont());

        listener = event -> {
            ITextPM selected = getPresentationModel().getSelected();
            localizedArea.setPresentationModel(selected);
        };
    }

    @SuppressWarnings("unchecked")
    private void setPrototypeWidth(BnList list) {
        list.setPrototypeCellValue(new TextPM("mmmmmmmmmmmmm"));
    }

    @Override
    public LocalizedStringPM getPresentationModel() {
        return localModelProvider.getPresentationModel();
    }

    @Override
    public void setPresentationModel(LocalizedStringPM pModel) {
        LocalizedStringPM oldModel = getPresentationModel();
        if (oldModel != null) {
            oldModel.removePropertyChangeListener("strings", listener);
        }
        localModelProvider.setPresentationModel(pModel);
        if (pModel != null) {
            pModel.addPropertyChangeListener("strings", listener);
        }
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
}
