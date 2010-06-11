/*
 * TrainUnitCyclesListTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Templates for generating train unit cycles list (HTML).
 *
 * @author jub
 */
public class TrainUnitCyclesListTemplates extends Templates {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrainUnitCyclesListTemplates.class.getName());
    
    private static final String PROPERTIES = "tuc_texts";

    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String tucHeader;
    private String tucLine;
    private String tucFooter;

    public TrainUnitCyclesListTemplates() {
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/tuc_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/tuc_html_footer.template");
        tucHeader = readTextFile(TEMPLATE_LOCATION + "/tuc_list_header.template");
        tucLine = readTextFile(TEMPLATE_LOCATION + "/tuc_list_line.template");
        tucFooter = readTextFile(TEMPLATE_LOCATION + "/tuc_list_footer.template");
    }

    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getTucFooter() {
        return tucFooter;
    }

    public String getTucHeader() {
        return tucHeader;
    }

    public String getTucLine() {
        return tucLine;
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
