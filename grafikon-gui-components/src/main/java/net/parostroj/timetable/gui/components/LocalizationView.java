package net.parostroj.timetable.gui.components;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.ScrollPaneConstants;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.TrainDiagram;

import javax.swing.JButton;

import java.awt.FlowLayout;

/**
 * View for localization.
 *
 * @author jub
 */
public class LocalizationView extends JPanel {

    private final JTable table;

    public LocalizationView() {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollPane);

        table = new JTable();
        scrollPane.setViewportView(table);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        add(panel, BorderLayout.SOUTH);

        JButton addMissingButton = new JButton(ResourceLoader.getString("localization.add.missing"));
        panel.add(addMissingButton);

        JButton removeNoExistingButton = new JButton(ResourceLoader.getString("localization.remove.non.existing"));
        panel.add(removeNoExistingButton);
    }

    public void setDiagram(TrainDiagram diagram) {
        table.setModel(new LocalizationViewModel(diagram.getLocalization()));
    }
}
