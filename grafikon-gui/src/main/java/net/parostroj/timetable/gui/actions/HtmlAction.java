package net.parostroj.timetable.gui.actions;

import java.io.File;
import java.io.Writer;

/**
 * Html action interface.
 *
 * @author jub
 */
public interface HtmlAction {

    public void write(Writer writer) throws Exception;

    public void writeToDirectory(File directory) throws Exception;
}
