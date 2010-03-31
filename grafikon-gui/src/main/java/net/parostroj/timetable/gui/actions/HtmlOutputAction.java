package net.parostroj.timetable.gui.actions;

import java.io.File;
import java.io.OutputStream;

/**
 * Html action interface.
 *
 * @author jub
 */
public interface HtmlOutputAction {

    public void write(OutputStream stream) throws Exception;

    public void writeToDirectory(File directory) throws Exception;
}
