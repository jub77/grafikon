/*
 * NodeTimetableListTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Templates for generating timetable list (HTML).
 *
 * @author jub
 */
public class NodeTimetablesListTemplates extends Templates {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeTimetablesListTemplates.class.getName());
    
    private static final String PROPERTIES = "n_timetable_texts";

    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String timetableHeader;
    private String timetableFooter;
    private String timetableLine;
    
    public NodeTimetablesListTemplates() {
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/n_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/n_html_footer.template");
        timetableHeader = readTextFile(TEMPLATE_LOCATION + "/n_timetable_header.template");
        timetableLine = readTextFile(TEMPLATE_LOCATION + "/n_timetable_line.template");
        timetableFooter = readTextFile(TEMPLATE_LOCATION + "/n_timetable_footer.template");
    }

    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getTimetableFooter() {
        return timetableFooter;
    }

    public String getTimetableHeader() {
        return timetableHeader;
    }

    public String getTimetableLine() {
        return timetableLine;
    }

    /**
     * returns localized text for given key.
     * 
     * @param key key
     * @return localized text
     */
    public String getString(String key) {
        try {
            return ResourceBundle.getBundle(PROPERTIES, getLocaleForTemplate()).getString(key);
        } catch (MissingResourceException e) {
            LOGGER.warn("Error getting text for key: {}", key);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
