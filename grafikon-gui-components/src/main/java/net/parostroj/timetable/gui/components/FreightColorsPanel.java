package net.parostroj.timetable.gui.components;

import java.awt.GridLayout;
import java.awt.Panel;
import java.util.*;

import javax.swing.JCheckBox;

import net.parostroj.timetable.model.FreightColor;

/**
 * Panel with selection of freight colors.
 *
 * @author jub
 */
public class FreightColorsPanel extends Panel {

    private final Map<FreightColor, JCheckBox> checks = new LinkedHashMap<FreightColor, JCheckBox>();

    public FreightColorsPanel() {
        setLayout(new GridLayout(0, 1));
        for (FreightColor c : FreightColor.values()) {
            JCheckBox checkBox = new JCheckBox(c.getName());
            add(checkBox);
            checks.put(c, checkBox);
        }
    }

    private void clear() {
        for (JCheckBox box : checks.values()) {
            box.setSelected(false);
        }
    }

    public void set(Collection<FreightColor> colors) {
        clear();
        for (FreightColor c : colors) {
            checks.get(c).setSelected(true);
        }
    }

    public List<FreightColor> get() {
        List<FreightColor> result = new ArrayList<FreightColor>();
        for (Map.Entry<FreightColor, JCheckBox> entry : checks.entrySet()) {
            if (entry.getValue().isSelected()) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
}
