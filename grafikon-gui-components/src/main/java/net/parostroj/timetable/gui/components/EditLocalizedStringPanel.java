package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.beanfabrics.IModelProvider;
import org.beanfabrics.Path;
import org.beanfabrics.swing.BnTextField;
import org.beanfabrics.swing.table.BnColumn;
import org.beanfabrics.swing.table.BnTable;

import net.parostroj.timetable.gui.utils.ResourceLoader;

public class EditLocalizedStringPanel extends JPanel {

    public EditLocalizedStringPanel(Path basePath, IModelProvider provider, int gap) {
        setLayout(new BorderLayout(gap, gap));
        JScrollPane scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);

        BnTable table = new BnTable();
        table.addColumn(new BnColumn(new Path("locale"), ResourceLoader.getString("localization.column.locale"), 110, true));
        table.addColumn(new BnColumn(new Path("string"), ResourceLoader.getString("localization.column.text")));
        table.setModelProvider(provider);
        table.setPath(Path.concat(basePath, new Path("strings")));
        table.setSortable(true);
        scrollPane.setViewportView(table);

        JPanel panel = new JPanel();
        this.add(panel, BorderLayout.NORTH);
        panel.setLayout(new BorderLayout(0, 0));

        BnTextField field = new BnTextField();
        field.setBackground(null);
        field.setModelProvider(provider);
        field.setPath(Path.concat(basePath, new Path("string")));
        panel.add(field, BorderLayout.NORTH);
    }
}
