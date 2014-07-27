package net.parostroj.timetable.gui.dialogs;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.*;

import javax.swing.JCheckBox;

import net.parostroj.timetable.gui.utils.LanguagesUtil;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.utils.Pair;

/**
 * Dialog of displayed languages.
 *
 * @author jub
 */
public class LanguagesDialog extends javax.swing.JDialog {

    private static final int COLUMNS = 1;

    private final Map<Locale, JCheckBox> localeMap;
    private Set<Locale> currentLocales;
    private javax.swing.JPanel localesPanel;

    public LanguagesDialog(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);
        initComponents();
        List<Pair<String, Locale>> locales = LanguagesUtil.getLocales();
        localesPanel.setLayout(new GridLayout(locales.size() / COLUMNS, COLUMNS));
        localeMap = new HashMap<Locale, JCheckBox>();
        for (Pair<String, Locale> locale : locales) {
            JCheckBox checkBox = new JCheckBox(locale.first);
            localeMap.put(locale.second, checkBox);
            checkBox.setSelected(false);
            localesPanel.add(checkBox);
        }
        this.pack();
    }

    /**
     *
     */
    private void initComponents() {
        localesPanel = new javax.swing.JPanel();
        javax.swing.JPanel okPanel = new javax.swing.JPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        getContentPane().add(localesPanel, java.awt.BorderLayout.CENTER);

        okPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentLocales.clear();
                for (Map.Entry<Locale, JCheckBox> entry : localeMap.entrySet()) {
                    if (entry.getValue().isSelected()) {
                        currentLocales.add(entry.getKey());
                    }
                }
                setVisible(false);
            }
        });
        okPanel.add(okButton);

        cancelButton.setText(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                currentLocales = null;
                setVisible(false);
            }
        });
        okPanel.add(cancelButton);

        getContentPane().add(okPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }

    public Set<Locale> showDialog(Component component, Collection<Locale> current) {
        currentLocales = new HashSet<Locale>(current);
        for (Map.Entry<Locale, JCheckBox> entry : localeMap.entrySet()) {
            entry.getValue().setSelected(currentLocales.contains(entry.getKey()));
        }
        this.setLocationRelativeTo(component);
        this.setVisible(true);
        return currentLocales;
    }
}
