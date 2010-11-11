/*
 * IndexPage.java
 * 
 * Created on 11.9.2007, 16:33:58
 */
package net.parostroj.timetable.output;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.utils.Pair;
import net.parostroj.timetable.utils.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page with indexes to trains.
 *
 * @author jub
 */
public class TrainTimetablesIndexPage implements Page {
    
    private static final Logger LOG = LoggerFactory.getLogger(TrainTimetablesIndexPage.class.getName());

    private int number;
    
    private int availableSize;

    private List<Pair<Train,Page>> index;
    
    private TrainTimetablesListTemplates templates;

    public TrainTimetablesIndexPage(int pageLength, List<Pair<Train,Page>> index, TrainTimetablesListTemplates templates) {
        this.index = new ArrayList<Pair<Train,Page>>();
        this.templates = templates;
        availableSize = (pageLength - templates.getIndexFooterHeight() - templates.getIndexHeaderHeight()) / templates.getIndexLineHeight();
        int maxCount = availableSize * 3;
        for (int i = 0; i < maxCount && !index.isEmpty(); i++) {
            Pair<Train,Page> item = index.remove(0);
            this.index.add(item);
        }
    }

    @Override
    public int getNumber() {
        return number;
    }
    
    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public void writeTo(Writer writer) throws IOException {
        Formatter f = new Formatter(writer);

        try {
            f.format(templates.getIndexHeader(),
                    TrainTimetablesListTemplates.getString("train.list"),
                    TrainTimetablesListTemplates.getString("index.train"),
                    TrainTimetablesListTemplates.getString("index.page"));
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
        }
        
        int i = 0;
        while (i < availableSize && i < index.size()) {
            Tuple<String> item1 = this.getIndex(i);
            Tuple<String> item2 = this.getIndex(i + availableSize);
            Tuple<String> item3 = this.getIndex(i + 2 * availableSize);
            f.format(templates.getIndexLine(), item1.first, item1.second, item2.first, item2.second, item3.first, item3.second);
            i++;
        }
        
        writer.write(templates.getIndexFooter());
    }
    
    private Tuple<String> getIndex(int i) {
        if (i < index.size()) {
            Pair<Train,Page> pair = index.get(i);
            String t = pair.first.getName();
            String p = Integer.toString(pair.second.getNumber());
            return new Tuple<String>(t,p);
        } else {
            return new Tuple<String>("&nbsp;","&nbsp;");
        }
    }
}
