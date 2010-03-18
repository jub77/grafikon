/*
 * TranTimetablesList.java
 * 
 * Created on 8.9.2007, 16:38:10
 */
package net.parostroj.timetable.output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.parostroj.timetable.actions.TrainComparator;
import net.parostroj.timetable.actions.TrainSort;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.Pair;

/**
 * List of timetables for list of trains.
 * 
 * @author jub
 */
public class TrainTimetablesList {
    
    private List<Page> pages;
    
    private TrainTimetablesListTemplates templates;
    
    private static final int A5_PAGE_LENGTH = 185;
    
    public enum Binding {
        BOOK, RING;
    }

    public TrainTimetablesList(TrainDiagram diagram, Collection<Train> aTrainList, List<TimetableImage> images, Binding binding, boolean alreadySorted) {
        pages = new LinkedList<Page>();
        List<Pair<Train,Page>> index = new LinkedList<Pair<Train, Page>>();
        templates = new TrainTimetablesListTemplates();
        
        Collection<Train> sorted = null;
        if (!alreadySorted) {
            // sort list
            TrainSort sort = new TrainSort(new TrainComparator(TrainComparator.Type.ASC, diagram.getTrainsData().getTrainSortPattern()));
            sorted = sort.sort(aTrainList);
        } else {
            sorted = aTrainList;
        }

        TrainTimetablesPage page = this.createNewPage();
        
        // go through all trains
        for (Train train : sorted) {
            TrainTimetable timetable = new TrainTimetable(train, templates, diagram);
            if (!page.addTrainTimetable(timetable)) {
                page = this.createNewPage();
                page.addTrainTimetable(timetable);
            }
            index.add(new Pair<Train, Page>(train, page));
        }
        
        // create index pages
        int position = 0;
        while (index.size() > 0) {
            Page indexPage = new TrainTimetablesIndexPage(A5_PAGE_LENGTH,index,templates);
            pages.add(position++, indexPage);
        }
        
        NodesDescriptionPage ndp = new NodesDescriptionPage(A5_PAGE_LENGTH);
        // add pages with images
        for (TimetableImage image : images) {
            if (!ndp.addImage(image)) {
                pages.add(ndp);
                ndp = new NodesDescriptionPage(A5_PAGE_LENGTH);
                ndp.addImage(image);
            }
        }
        
        if (ndp.getActualLength() > 0) {
            pages.add(ndp);
        }

        // add empty pages
        for (int i = 0; i < (pages.size() % 4); i++) {
            pages.add(new EmptyPage());
        }

        // set page numbers
        int i = 1;
        for (Page item : pages) {
            item.setNumber(i++);
        }
        
        pages = this.reorder(pages,binding);
    }
    
    private List<Page> reorder(List<Page> pages, Binding binding) {
        List<Page> result = new LinkedList<Page>();

        switch (binding) {
            case BOOK: case RING:
                // reorder pages for printing (book)
                int left = pages.size();
                int right = 1;

                while (right < (pages.size() /2)) {
                    result.add(pages.get(left-1));
                    result.add(pages.get(right-1));
                    left -= 2;
                    right +=2;
                }

                left = pages.size() / 2;
                right = (pages.size() / 2) + 1;

                while (right < pages.size()) {
                    result.add(pages.get(left-1));
                    result.add(pages.get(right-1));
                    left -= 2;
                    right +=2;
                }
                break;
        }
        
        return result;
    }
    
    private TrainTimetablesPage createNewPage() {
        TrainTimetablesPage page = new TrainTimetablesPage(A5_PAGE_LENGTH, templates);
        pages.add(page);
        return page;
    }

    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);
        
        // html header
        writer.write(String.format(templates.getHtmlHeader(),TrainTimetablesListTemplates.getString("train.timetable")));
        
        
        Iterator<Page> i = pages.iterator();
        // write timetables
        while (i.hasNext()) {
            Page pl = i.next();
            Page pr = i.next();
            f.format(templates.getPage2a5Header(),Integer.toString(pl.getNumber()),Integer.toString(pr.getNumber()));
            pl.writeTo(writer);
            writer.write(templates.getPage2a5Middle());
            pr.writeTo(writer);
            writer.write(templates.getPage2a5Footer());
        }

        // html footer
        writer.write(templates.getHtmlFooter());
    }
    
    public void saveImages(Collection<TimetableImage> images, File directory) throws IOException {
        for (TimetableImage image : images) {
            if (image.getImageFile() != null) {
                File outputFile = new File(directory, image.getFilename());
                FileChannel ic = new FileInputStream(image.getImageFile()).getChannel();
                FileChannel oc = new FileOutputStream(outputFile).getChannel();
                ic.transferTo(0, ic.size(), oc);
                ic.close();
                oc.close();
            }
        }
    }
}
