package net.parostroj.timetable.gui.components;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.swing.list.BnList;
import org.beanfabrics.swing.list.CellConfig;

import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

public class EditLocalizedStringListPanel extends JPanel {

    public EditLocalizedStringListPanel(Path basePath, IModelProvider provider, int gap) {
        setLayout(new BorderLayout(gap, gap));

        JPanel localizedStringPanel = new EditLocalizedStringPanel(Path.concat(basePath, new Path("selected")),
                provider, gap);
        this.add(localizedStringPanel, BorderLayout.CENTER);

        BnList list = new BnList();
        list.setModelProvider(provider);
        list.setPath(Path.concat(basePath, new Path("list")));
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
}
