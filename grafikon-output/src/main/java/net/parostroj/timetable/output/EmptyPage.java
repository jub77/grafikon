/*
 * EmptyPage.java
 * 
 * Created on 11.9.2007, 16:31:51
 */

package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;

/**
 * Empty page.
 * 
 * @author jub
 */
public class EmptyPage implements Page {
    
    private int number;

    public EmptyPage() {
    }

    public int getNumber() {
        return number;
    }

    public void writeTo(Writer writer) throws IOException {
        // do nothing (page is empty)
    }

    public void setNumber(int number) {
        this.number = number;
    }

}
