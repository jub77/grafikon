package net.parostroj.timetable.model.ls.impl4;

import java.io.IOException;
import java.io.Reader;

/**
 * Helper class, that skips close action on reader (needed when dealing
 * with JAXB and stream from zip file).
 * 
 * @author jub
 */
public class NoCloseAllowedReader extends Reader {
    
    private Reader reader;
    
    public NoCloseAllowedReader(Reader reader) {
        super(reader);
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        // close is not propagated
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        return reader.read(cbuf, off, len);
    }
}