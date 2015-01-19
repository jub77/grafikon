package net.parostroj.timetable.output2;

/**
 * Output exception.
 *
 * @author jub
 */
public class OutputException extends Exception {

    public OutputException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutputException(String message) {
        super(message);
    }

    public OutputException() {
    }

    public OutputException(Throwable cause) {
        super(cause);
    }
}
