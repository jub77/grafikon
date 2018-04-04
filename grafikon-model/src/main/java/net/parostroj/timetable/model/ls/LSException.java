package net.parostroj.timetable.model.ls;

public class LSException extends Exception {

    private static final long serialVersionUID = 1L;

	/**
     * Default constructor.
     */
    public LSException() {
    }

    /**
     * @param message
     * @param cause
     */
    public LSException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public LSException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public LSException(Throwable cause) {
        super(cause);
    }
}
