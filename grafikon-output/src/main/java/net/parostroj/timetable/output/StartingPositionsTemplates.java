/*
 * StartingPositionsTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Templates for generating starting positions (HTML).
 *
 * @author jub
 */
public class StartingPositionsTemplates extends Templates {

    private static final Logger LOGGER = Logger.getLogger(StartingPositionsTemplates.class.getName());
    
    private static final String PROPERTIES = "sp_texts";

    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String spSection;
    private String spSectionFooter;
    private String spLine;

    public StartingPositionsTemplates() {
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/sp_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/sp_html_footer.template");
        spSection = readTextFile(TEMPLATE_LOCATION + "/sp_section_header.template");
        spSectionFooter = readTextFile(TEMPLATE_LOCATION + "/sp_section_footer.template");
        spLine = readTextFile(TEMPLATE_LOCATION + "/sp_line.template");
    }

    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getSpLine() {
        return spLine;
    }

    public String getSpSection() {
        return spSection;
    }

    public String getSpSectionFooter() {
        return spSectionFooter;
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
