/*
 * TrainTimetableListTemplates.java
 *
 * Created on 11.9.2007, 9:37:59
 */
package net.parostroj.timetable.output;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Templates for generating timetable list (HTML).
 *
 * @author jub
 */
public class TrainTimetablesListTemplates extends Templates {
    
    private static final Logger LOGGER = Logger.getLogger(TrainTimetablesListTemplates.class.getName());
    
    private static final String PROPERTIES = "t_timetable_texts";

    private static final String TEMPLATE_LOCATION = "/templates";

    private String htmlHeader;
    private String htmlFooter;
    private String page2a5Header;
    private String page2a5Footer;
    private String page2a5Middle;

    private String timetableHeader;
    private String timetableHeaderWeightLine;
    private String timetableHeaderLengthLine;
    private String timetableHeaderWeightLineOneCell;
    private String timetableFooter;
    private String timetableSummary;
    private String timetableComment;
    private String timetableLine;

    private String timetableHeaderD3;
    private String timetableHeaderWeightLineD3;
    private String timetableFooterD3;
    private String timetableSummaryD3;
    private String timetableCommentD3;
    private String timetableLineD3;
    
    private String timetableSeparator;
    
    private String indexHeader;
    private String indexLine;
    private String indexFooter;

    public TrainTimetablesListTemplates() {
        htmlHeader = readTextFile(TEMPLATE_LOCATION + "/t_html_header.template");
        htmlFooter = readTextFile(TEMPLATE_LOCATION + "/t_html_footer.template");
        page2a5Header = readTextFile(TEMPLATE_LOCATION + "/t_page_2a5_header.template");
        page2a5Middle = readTextFile(TEMPLATE_LOCATION + "/t_page_2a5_middle.template");
        page2a5Footer = readTextFile(TEMPLATE_LOCATION + "/t_page_2a5_footer.template");
        timetableHeader = readTextFile(TEMPLATE_LOCATION + "/t_timetable_header.template");
        timetableHeaderWeightLine = readTextFile(TEMPLATE_LOCATION + "/t_timetable_header_weight_line.template");
        timetableHeaderWeightLineOneCell = readTextFile(TEMPLATE_LOCATION + "/t_timetable_header_weight_line_one_cell.template");
        timetableHeaderLengthLine = readTextFile(TEMPLATE_LOCATION + "/t_timetable_header_length_line.template");
        timetableLine = readTextFile(TEMPLATE_LOCATION + "/t_timetable_line.template");
        timetableSummary = readTextFile(TEMPLATE_LOCATION + "/t_timetable_summary.template");
        timetableComment = readTextFile(TEMPLATE_LOCATION + "/t_timetable_comment.template");
        timetableFooter = readTextFile(TEMPLATE_LOCATION + "/t_timetable_footer.template");
        timetableHeaderD3 = readTextFile(TEMPLATE_LOCATION + "/t_timetable_header_d3.template");
        timetableHeaderWeightLineD3 = readTextFile(TEMPLATE_LOCATION + "/t_timetable_header_weight_line_d3.template");
        timetableLineD3 = readTextFile(TEMPLATE_LOCATION + "/t_timetable_line_d3.template");
        timetableSummaryD3 = readTextFile(TEMPLATE_LOCATION + "/t_timetable_summary_d3.template");
        timetableCommentD3 = readTextFile(TEMPLATE_LOCATION + "/t_timetable_comment_d3.template");
        timetableFooterD3 = readTextFile(TEMPLATE_LOCATION + "/t_timetable_footer_d3.template");
        timetableSeparator = readTextFile(TEMPLATE_LOCATION + "/t_timetable_separator.template");
        indexHeader = readTextFile(TEMPLATE_LOCATION + "/t_index_header.template");
        indexLine = readTextFile(TEMPLATE_LOCATION + "/t_index_line.template");
        indexFooter = readTextFile(TEMPLATE_LOCATION + "/t_index_footer.template");
    }

    public String getIndexFooter() {
        return indexFooter;
    }

    public String getIndexHeader() {
        return indexHeader;
    }

    public String getIndexLine() {
        return indexLine;
    }
    
    public int getIndexFooterHeight() {
        return 10;
    }

    public int getIndexHeaderHeight() {
        return 24;
    }

    public int getIndexLineHeight() {
        return 4;
    }
    
    public String getHtmlFooter() {
        return htmlFooter;
    }

    public String getHtmlHeader() {
        return htmlHeader;
    }

    public String getPage2a5Footer() {
        return page2a5Footer;
    }

    public String getPage2a5Header() {
        return page2a5Header;
    }

    public String getPage2a5Middle() {
        return page2a5Middle;
    }

    public String getTimetableFooterD3() {
        return timetableFooterD3;
    }

    public String getTimetableHeaderD3() {
        return timetableHeaderD3;
    }

    public String getTimetableLineD3() {
        return timetableLineD3;
    }

    public String getTimetableFooter() {
        return timetableFooter;
    }
    
    public int getTimetableFooterHeight() {
        return 14;
    }

    public String getTimetableHeader() {
        return timetableHeader;
    }

    public int getTimetableHeaderHeight() {
        return 13;
    }
    
    public int getTimetableHeaderRouteHeight() {
        return 4;
    }
    
    public int getTimetableHeaderWeightLineHeight() {
        return 4;
    }
    
    public int getTimetableIndexLineHeight() {
        return 5;
    }

    public String getTimetableLine() {
        return timetableLine;
    }

    public int getTimetableLineHeight() {
        return 5;
    }
    
    public int getTimetableCommentHeight() {
        return 5;
    }

    public String getTimetableSummary() {
        return timetableSummary;
    }

    public String getTimetableSummaryD3() {
        return timetableSummaryD3;
    }

    public String getTimetableComment() {
        return timetableComment;
    }

    public String getTimetableCommentD3() {
        return timetableCommentD3;
    }

    public String getTimetableHeaderWeightLine() {
        return timetableHeaderWeightLine;
    }

    public String getTimetableHeaderLengthLine() {
        return timetableHeaderLengthLine;
    }

    public String getTimetableHeaderWeightLineD3() {
        return timetableHeaderWeightLineD3;
    }

    public String getTimetableSeparator() {
        return timetableSeparator;
    }

    public String getTimetableHeaderWeightLineOneCell() {
        return timetableHeaderWeightLineOneCell;
    }

    /**
     * returns localized text for given key.
     * 
     * @param key key
     * @return localized text
     */
    public static String getString(String key) {
        try {
            return ResourceBundle.getBundle(PROPERTIES, getLocaleForTemplate()).getString(key);
        } catch (MissingResourceException e) {
            LOGGER.log(Level.WARNING, "Error getting text for key: " + key, e);
            return "MISSING STRING FOR KEY: " + key;
        }
    }
}
