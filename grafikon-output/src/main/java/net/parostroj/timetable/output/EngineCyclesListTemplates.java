/*
 * EngineCyclesListTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Templates for generating engine cycles list (HTML).
 *
 * @author jub
 */
public class EngineCyclesListTemplates extends Templates {

    private static final Logger LOGGER = Logger.getLogger(EngineCyclesListTemplates.class.getName());
    
    private static final String PROPERTIES = "ec_texts";

    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String rowHeader;
    private String rowFooter;
    private String rowMiddle;
    private String ecHeader;
    private String ecLine;
    private String ecLineSep;
    private String ecFooter;

    public EngineCyclesListTemplates() {
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/ec_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/ec_html_footer.template");
        rowHeader = readTextFile(TEMPLATE_LOCATION + "/ec_row_header.template");
        rowMiddle = readTextFile(TEMPLATE_LOCATION + "/ec_row_middle.template");
        rowFooter = readTextFile(TEMPLATE_LOCATION + "/ec_row_footer.template");
        ecHeader = readTextFile(TEMPLATE_LOCATION + "/ec_list_header.template");
        ecLine = readTextFile(TEMPLATE_LOCATION + "/ec_list_line.template");
        ecLineSep = readTextFile(TEMPLATE_LOCATION + "/ec_list_line_sep.template");
        ecFooter = readTextFile(TEMPLATE_LOCATION + "/ec_list_footer.template");
    }

    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getRowFooter() {
        return rowFooter;
    }

    public String getRowHeader() {
        return rowHeader;
    }

    public String getRowMiddle() {
        return rowMiddle;
    }

    public String getEcFooter() {
        return ecFooter;
    }

    public String getEcHeader() {
        return ecHeader;
    }

    public String getEcLine() {
        return ecLine;
    }

    public String getEcLineSep() {
        return ecLineSep;
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
