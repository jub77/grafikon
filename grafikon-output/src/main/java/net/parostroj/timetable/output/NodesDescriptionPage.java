/*
 * NodesDescriptionsPage.java
 * 
 * Created on 22.9.2007, 23:23:49
 */

package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.model.TimetableImage;

/**
 * Page with descriptions of nodes.
 * 
 * @author jub
 */
public class NodesDescriptionPage implements Page {
    
    private int number;
    
    private int pageLength;

    private int actualLength;
    
    private List<TimetableImage> images;
    
    public NodesDescriptionPage(int pageLength) {
        this.pageLength = pageLength;
        this.actualLength = 0;
        this.images = new LinkedList<TimetableImage>();
    }

    public boolean addImage(TimetableImage image) {
        if (image.getHeight() + actualLength > pageLength)
            return false;
        
        images.add(image);
        actualLength += image.getHeight();
        
        return true;
    }
    
    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    public int getActualLength() {
        return actualLength;
    }
    
    @Override
    public void writeTo(Writer writer) throws IOException {
        for (TimetableImage image : images) {
            writer.write("<div align=\"center\"><img src=\"");
            writer.write(image.getFilename());
            writer.write("\" style=\"height: ");
            writer.write(Integer.toString(image.getHeight()));
            writer.write("mm\"></div>");
        }
    }
}
