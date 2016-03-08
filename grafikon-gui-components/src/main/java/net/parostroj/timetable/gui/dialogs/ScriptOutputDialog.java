package net.parostroj.timetable.gui.dialogs;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog with output from script.
 *
 * @author jub
 */
public class ScriptOutputDialog extends javax.swing.JDialog {

    private final JTextArea textArea;

    public void setText(String text) {
        textArea.setText(text);
    }

    public ScriptOutputDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        panel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        textArea = new JTextArea();
        textArea.setColumns(60);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        textArea.setRows(25);
        scrollPane.setViewportView(textArea);

        pack();
    }
}
