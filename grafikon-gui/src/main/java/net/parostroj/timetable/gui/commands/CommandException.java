/*
 * CommandException.java
 *
 * Created on 4.9.2007, 10:03:13
 */
package net.parostroj.timetable.gui.commands;

/**
 * Command exception.
 *
 * @author jub
 */
public class CommandException extends Exception {

    private static final long serialVersionUID = 1L;

	public CommandException() {
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }
}
