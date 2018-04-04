package net.parostroj.timetable.gui.dialogs;

import java.awt.*;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.*;

import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog for zoom.
 *
 * @author jub
 */
public class GTViewZoomDialog extends JDialog {

    private static final long serialVersionUID = 1L;

	private boolean ok;
    private final JSlider slider;

    public GTViewZoomDialog(Window owner, boolean modal) {
        super(owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        slider = new JSlider();
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.setValue(20);
        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(2);
        slider.setMaximum(40);
        slider.setMinimum(10);
        slider.setLabelTable(this.createDictionary(slider));
        slider.setPaintLabels(true);
        Dimension prefSize = slider.getPreferredSize();
        prefSize.width = prefSize.width * 2;
        slider.setPreferredSize(prefSize);
        panel.add(slider, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(e -> {
            ok = true;
            setVisible(false);
        });
        buttonPanel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(e -> setVisible(false));
        buttonPanel.add(cancelButton);
        pack();
        setResizable(false);
    }

    private Dictionary<?, ?> createDictionary(JSlider slider) {
        Hashtable<?, ?> map = slider.createStandardLabels(2);
        for (int i = 5; i <= 20; i++) {
            ((JLabel) map.get(i * 2)).setText(Float.valueOf(i / (float) 10).toString());
        }
        return map;
    }

    public Float showDialog(Float value) {
        this.ok = false;
        this.setValue(value);
        this.setVisible(true);
        return this.ok ? this.getValue() : null;
    }

    private Float getValue() {
        return slider.getValue() / (float) 20;
    }

    private void setValue(Float value) {
        slider.setValue((int) (value * 20));
    }
}
