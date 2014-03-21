package net.parostroj.timetable.gui.components;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * All changes concetrated to one method.
 *
 * @author jub
 */
public abstract class ChangeDocumentListener implements DocumentListener {

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.change();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.change();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.change();
    }

    abstract protected void change();
}
