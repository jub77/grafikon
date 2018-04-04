package net.parostroj.timetable.gui;

/**
 * Application starter exception.
 *
 * @author jub
 */
public class ApplicationStarterException extends Exception {

    private static final long serialVersionUID = 1L;

	public ApplicationStarterException(Throwable cause) {
        super(cause);
    }

    public ApplicationStarterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationStarterException(String message) {
        super(message);
    }

    public ApplicationStarterException() {
    }

}
