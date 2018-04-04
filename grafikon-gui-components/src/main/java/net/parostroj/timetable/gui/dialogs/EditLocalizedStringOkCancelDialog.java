package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;
import java.util.Locale;

import javax.swing.JPanel;

import net.parostroj.timetable.gui.pm.LocalizedStringPM;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.LocalizedString;

import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.FlowLayout;

public class EditLocalizedStringOkCancelDialog extends EditLocalizedStringDialog {

    private static final long serialVersionUID = 1L;

	private boolean ok = false;

    public EditLocalizedStringOkCancelDialog(Window owner) {
        super(owner, true);

        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton okButton = new JButton(ResourceLoader.getString("button.ok"));
        okButton.addActionListener(evt -> {
            ok = true;
            setVisible(false);
        });
        panel.add(okButton);

        JButton cancelButton = new JButton(ResourceLoader.getString("button.cancel"));
        cancelButton.addActionListener(evt -> setVisible(false));
        panel.add(cancelButton);
    }

    public LocalizedString edit(LocalizedString string, Collection<Locale> locales) {
        ok = false;
        LocalizedStringPM model = new LocalizedStringPM();
        model.init(string, locales);
        setPresentationModel(model);
        setVisible(true);
        return ok ? model.getCurrentEdit().get() : null;
    }
}
