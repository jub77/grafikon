package net.parostroj.timetable.gui.dialogs;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;

import javax.swing.*;

import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Dialog with output from script.
 *
 * @author jub
 */
public class ScriptOutputDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private final JTextArea textArea;

    public void setText(String text) {
        textArea.setText(text);
    }

    public ScriptOutputDialog(Window parent, boolean modal) {
        super(parent, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        panel.add(okButton);
        okButton.addActionListener(e -> setVisible(false));

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
