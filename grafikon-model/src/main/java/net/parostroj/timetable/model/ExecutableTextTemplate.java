package net.parostroj.timetable.model;

import java.io.*;
import java.util.Map;

/**
 * Common interface for execution of text template.
 *
 * @author jub
 */
public interface ExecutableTextTemplate {

    String evaluateWithException(Map<String, Object> binding) throws GrafikonException;

    String evaluate(Map<String, Object> binding);

    void evaluate(OutputStream output, Map<String, Object> binding, String encoding) throws GrafikonException;

    public abstract void evaluate(Writer output, Map<String, Object> binding) throws GrafikonException;
}
