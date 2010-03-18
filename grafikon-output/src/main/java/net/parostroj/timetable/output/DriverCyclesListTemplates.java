/*
 * DriverCyclesListTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Templates for generating driver cycles list (HTML).
 *
 * @author jub
 */
public class DriverCyclesListTemplates extends Templates {

    private static final Logger LOGGER = Logger.getLogger(DriverCyclesListTemplates.class.getName());
    
    private static final String PROPERTIES = "dc_texts";
    
    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String dcLine;
    private String dcLineMove;
    private String dcHeader;
    private String dcFooter;
    private String dcHeaderTrains;
    private String dcFooterTrains;

    public DriverCyclesListTemplates() {
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/dc_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/dc_html_footer.template");
        dcHeader = readTextFile(TEMPLATE_LOCATION + "/dc_list_header.template");
        dcFooter = readTextFile(TEMPLATE_LOCATION + "/dc_list_footer.template");
        dcLine = readTextFile(TEMPLATE_LOCATION + "/dc_list_line.template");
        dcLineMove = readTextFile(TEMPLATE_LOCATION + "/dc_list_line_move.template");
        dcHeaderTrains = readTextFile(TEMPLATE_LOCATION + "/dc_list_header_trains.template");
        dcFooterTrains = readTextFile(TEMPLATE_LOCATION + "/dc_list_footer_trains.template");
    }

    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getDcLine() {
        return dcLine;
    }

    public String getDcLineMove() {
        return dcLineMove;
    }

    public String getDcFooter() {
        return dcFooter;
    }

    public String getDcHeader() {
        return dcHeader;
    }

    public String getDcFooterTrains() {
        return dcFooterTrains;
    }

    public String getDcHeaderTrains() {
        return dcHeaderTrains;
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
            LOGGER.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
