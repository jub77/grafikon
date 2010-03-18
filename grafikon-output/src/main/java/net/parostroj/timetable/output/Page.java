/*
 * Page.java
 * 
 * Created on 11.9.2007, 16:29:46
 */

package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;

/**
 * Interface for pages.
 * 
 * @author jub
 */
public interface Page {

    public int getNumber();
    
    public void setNumber(int number);
    
    public void writeTo(Writer writer) throws IOException;
}
