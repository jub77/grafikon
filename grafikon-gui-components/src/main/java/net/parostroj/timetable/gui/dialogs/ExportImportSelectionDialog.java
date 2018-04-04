package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import javax.swing.JLabel;

import net.parostroj.timetable.gui.components.ExportImportSelection;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.gui.wrappers.Wrapper;
import net.parostroj.timetable.model.imports.ImportMatch;

import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.JCheckBox;

public class ExportImportSelectionDialog extends ExportImportSelectionBaseDialog {

    private static final long serialVersionUID = 1L;

	private JComboBox<Wrapper<ImportMatch>> matchComboBox;
    private JCheckBox overwriteCheckBox;

    public ExportImportSelectionDialog(Window parent, boolean modal) {
        super(parent, modal);

        exportImportSelectionPanel.getLeftPanel().add(Box.createHorizontalStrut(5));

        JLabel matchLabel = new JLabel(ResourceLoader.getString("import.match"));
        exportImportSelectionPanel.getLeftPanel().add(matchLabel);

        matchComboBox = new JComboBox<>();
        exportImportSelectionPanel.getLeftPanel().add(matchComboBox);

        exportImportSelectionPanel.getLeftPanel().add(Box.createHorizontalStrut(5));

        overwriteCheckBox = new JCheckBox(ResourceLoader.getString("import.overwrite"));
        exportImportSelectionPanel.getLeftPanel().add(overwriteCheckBox);

        // initialize combobox for matching
        matchComboBox.addItem(Wrapper.getWrapper(ImportMatch.NAME));
        matchComboBox.addItem(Wrapper.getWrapper(ImportMatch.ID));
    }

    public ImportMatch getImportMatch() {
        return (ImportMatch) ((Wrapper<?>) matchComboBox.getSelectedItem()).getElement();
    }

    public boolean isImportOverwrite() {
        return overwriteCheckBox.isSelected();
    }

    @Override
    public ExportImportSelection getSelection() {
        ExportImportSelection selection = super.getSelection();
        selection.setImportMatch(getImportMatch());
        selection.setImportOverwrite(isImportOverwrite());
        return selection;
    }
}
