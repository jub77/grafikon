package net.parostroj.timetable.gui.actions.impl;

import javax.swing.JFileChooser;

public class CloseableFileChooser extends JFileChooser implements AutoCloseable {

    private static final long serialVersionUID = 1L;

	public interface CloseAction {
        void close(CloseableFileChooser chooser);
    }

    private CloseAction closeAction;

    public void setCloseAction(CloseAction closeAction) {
        this.closeAction = closeAction;
    }

    @Override
    public void close() {
        if (closeAction != null) {
            closeAction.close(this);
            closeAction = null;
        }
    }
}
