package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;

import net.parostroj.timetable.gui.components.ExportImportSelection;
import net.parostroj.timetable.gui.components.ExportImportSelectionPanel;
import net.parostroj.timetable.gui.components.ExportImportSelectionSource;
import net.parostroj.timetable.gui.utils.ResourceLoader;

public class ExportImportSelectionBaseDialog extends JDialog {

    protected ExportImportSelectionPanel exportImportSelectionPanel;
    protected boolean cancelled;

    public ExportImportSelectionBaseDialog(Window parent, boolean modal) {
        super(parent, modal ? JDialog.DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        exportImportSelectionPanel = new ExportImportSelectionPanel();
        getContentPane().add(exportImportSelectionPanel, BorderLayout.CENTER);

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        exportImportSelectionPanel.getRightPanel().add(okButton);
        okButton.addActionListener(event -> setVisible(false));

        exportImportSelectionPanel.getRightPanel().add(Box.createHorizontalStrut(5));

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        exportImportSelectionPanel.getRightPanel().add(cancelButton);

        ActionListener closeAction = event -> {
            setSelectionSource(ExportImportSelectionSource.empty());
            cancelled = true;
            setVisible(false);
        };
        cancelButton.addActionListener(closeAction);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeAction.actionPerformed(null);
            }
        });

        exportImportSelectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pack();
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            cancelled = false;
        }
        super.setVisible(b);
    }

    public void setSelectionSource(ExportImportSelectionSource source) {
        exportImportSelectionPanel.setSelectionSource(source);
    }

    public ExportImportSelection getSelection() {
        return exportImportSelectionPanel.getSelection();
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
