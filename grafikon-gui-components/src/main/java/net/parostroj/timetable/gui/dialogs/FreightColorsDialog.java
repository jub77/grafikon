package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.parostroj.timetable.gui.components.FreightColorsPanel;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.FreightColor;

import javax.swing.border.EmptyBorder;

public class FreightColorsDialog extends JDialog {

    private final FreightColorsPanel panel;
    private boolean ok;

    public FreightColorsDialog(Window owner) {
        super(owner, ModalityType.APPLICATION_MODAL);

        getContentPane().setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok = true;
                setVisible(false);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setBorder(new EmptyBorder(3, 5, 0, 5));
        getContentPane().add(wrapperPanel, BorderLayout.NORTH);
        wrapperPanel.setLayout(new BorderLayout());

        panel = new FreightColorsPanel();
        wrapperPanel.add(panel);

        pack();
        setResizable(false);
    }

    public List<FreightColor> showDialog(Collection<FreightColor> colors) {
        ok = false;
        panel.set(colors);
        this.setVisible(true);
        return ok ? panel.get() : null;
    }
}
