package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.*;

import net.parostroj.timetable.gui.dialogs.LanguagesDialog;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.*;

/**
 * View for localization.
 *
 * @author jub
 */
public class LocalizationView extends JPanel {

    private final JTable table;
    private TrainDiagram diagram;

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
        addMissingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collection<String> texts = extractTextsForLocalization();
                for (String text : texts) {
                    diagram.getLocalization().addKey(text);
                }
                initModel();
            }
        });
        panel.add(addMissingButton);

        JButton removeNoExistingButton = new JButton(ResourceLoader.getString("localization.remove.non.existing"));
        removeNoExistingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Collection<String> texts = extractTextsForLocalization();
                Set<String> keys = new HashSet<String>(diagram.getLocalization().getKeys());
                keys.removeAll(texts);
                for (String key : keys) {
                    diagram.getLocalization().removeKey(key);
                }
                initModel();
            }
        });
        panel.add(removeNoExistingButton);

        JButton languagesButton = new JButton(ResourceLoader.getString("localization.languages") + "...");
        languagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LanguagesDialog dialog = new LanguagesDialog(null);
                Set<Locale> newSelection = dialog.showDialog(LocalizationView.this, diagram.getLocalization().getLocales());
                if (newSelection != null) {
                    // add
                    for (Locale l : newSelection) {
                        diagram.getLocalization().addLocale(l);
                    }
                    // remove
                    Set<Locale> current = new HashSet<Locale>(diagram.getLocalization().getLocales());
                    current.removeAll(newSelection);
                    for (Locale l : current) {
                        diagram.getLocalization().removeLocale(l);
                    }
                    initModel();
                }
            }
        });
        panel.add(languagesButton);
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        initModel();
    }

    private Collection<String> extractTextsForLocalization() {
        Collection<String> texts = new ArrayList<String>();

        // comments for train intervals
        for (Train train : diagram.getTrains()) {
            for (TimeInterval interval : train.getTimeIntervalList()) {
                String comment = interval.getAttribute(TimeInterval.ATTR_COMMENT, String.class);
                if (comment != null) {
                    texts.add(comment);
                }
            }
        }
        // comments for circulation items
        for (TrainsCycle cycle : diagram.getCycles()) {
            for (TrainsCycleItem item : cycle) {
                if (item.getComment() != null) {
                    texts.add(item.getComment());
                }
            }
        }
        return texts;
    }

    private void initModel() {
        table.setModel(new LocalizationViewModel(diagram.getLocalization()));
    }
}
