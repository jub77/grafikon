package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;
import java.util.Collection;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;

import net.parostroj.timetable.gui.components.ExportImportSelectionSource;
import net.parostroj.timetable.gui.components.ExportImportSelectionPanel;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.imports.ImportComponent;

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

        cancelButton.addActionListener(event -> {
            setSelectionSource(ExportImportSelectionSource.empty());
            cancelled = true;
            setVisible(false);
        });

        exportImportSelectionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        pack();
    }

    @Override
    public void setVisible(boolean b) {
        cancelled = false;
        super.setVisible(b);
    }

    public void setSelectionSource(ExportImportSelectionSource source) {
        exportImportSelectionPanel.setSelectionSource(source);
    }

    public Map<ImportComponent, Collection<ObjectWithId>> getSelection() {
        return exportImportSelectionPanel.getSelection();
    }

    public boolean isCancelled() {
        return cancelled;
    }
}
