package net.parostroj.timetable.model.save;

import java.io.FilterReader;
import java.io.Reader;

/**
 * Helper class, that skips close action on reader (needed when dealing
 * with JAXB and stream from zip file).
 *
 * @author jub
 */
public class NoCloseAllowedReader extends FilterReader {

    public NoCloseAllowedReader(Reader reader) {
        super(reader);
    }

    @Override
    public void close() {
        // close is not propagated
    }
}
