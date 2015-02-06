package net.parostroj.timetable.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Window;

import javax.swing.JDialog;

import net.parostroj.timetable.gui.components.LocalizationView;
import net.parostroj.timetable.gui.utils.LanguageLoader;
import net.parostroj.timetable.model.TrainDiagram;

public class LocalizationDialog extends JDialog {

    private final LocalizationView localizationView;

    public LocalizationDialog(Window owner, boolean modal, LanguageLoader languageLoader) {
        super(owner, modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        localizationView = new LocalizationView(languageLoader);
        getContentPane().add(localizationView, BorderLayout.CENTER);
        pack();
    }

    public void showDialog(TrainDiagram diagram) {
        this.localizationView.setDiagram(diagram);
        this.pack();
        this.setVisible(true);
    }
}
