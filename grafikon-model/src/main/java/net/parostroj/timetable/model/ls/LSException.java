package net.parostroj.timetable.model.ls;

public class LSException extends Exception {

    private static final long serialVersionUID = 1L;

    public LSException() {}

    public LSException(String message, Throwable cause) {
        super(message, cause);
    }

    public LSException(String message) {
        super(message);
    }

    public LSException(Throwable cause) {
        super(cause);
    }
}
