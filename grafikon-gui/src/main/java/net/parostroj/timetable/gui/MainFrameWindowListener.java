package net.parostroj.timetable.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.parostroj.timetable.gui.actions.ExitAction;
import net.parostroj.timetable.gui.actions.execution.ActionHandler;
import net.parostroj.timetable.gui.actions.execution.ModelAction;

/**
 * Window listener for the MainFrame.
 *
 * @author jub
 */
public class MainFrameWindowListener extends WindowAdapter {

    private final MainFrame parent;
    private final ApplicationModel model;

    public MainFrameWindowListener(ApplicationModel model, MainFrame parent) {
        this.parent = parent;
        this.model = model;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        super.windowClosing(e);
        ModelAction action = ExitAction.getExitAction(parent, model, false);
        ActionHandler.getInstance().execute(action);
    }

    @Override
    public void windowClosed(WindowEvent e) {
        super.windowClosed(e);
        System.exit(0);
    }
}
