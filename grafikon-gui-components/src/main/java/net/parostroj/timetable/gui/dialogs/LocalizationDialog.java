package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;

import net.parostroj.timetable.gui.components.LocalizationView;
import net.parostroj.timetable.model.Localization;

public class LocalizationDialog extends JDialog {

    private final LocalizationView localizationView;

    public LocalizationDialog(Window owner, boolean modal) {
        super(owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        localizationView = new LocalizationView();
        getContentPane().add(localizationView, BorderLayout.CENTER);
        pack();
    }

    public void showDialog(Localization localization) {
        this.localizationView.setLocalization(localization);
        this.pack();
        this.setVisible(true);
    }
}
