/*
 * EndPositionsTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Templates for generating end positions (HTML).
 *
 * @author jub
 */
public class EndPositionsTemplates extends Templates {

    private static final Logger LOGGER = Logger.getLogger(EndPositionsTemplates.class.getName());
    
    private static final String PROPERTIES = "ep_texts";

    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String epSection;
    private String epSectionFooter;
    private String epLine;

    public EndPositionsTemplates() {
        // uses the same templates as starting positions
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/sp_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/sp_html_footer.template");
        epSection = readTextFile(TEMPLATE_LOCATION + "/sp_section_header.template");
        epSectionFooter = readTextFile(TEMPLATE_LOCATION + "/sp_section_footer.template");
        epLine = readTextFile(TEMPLATE_LOCATION + "/sp_line.template");
    }

    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getEpLine() {
        return epLine;
    }

    public String getEpSection() {
        return epSection;
    }

    public String getEpSectionFooter() {
        return epSectionFooter;
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
