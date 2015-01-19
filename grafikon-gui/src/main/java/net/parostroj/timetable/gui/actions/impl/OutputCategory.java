package net.parostroj.timetable.gui.actions.impl;

/**
 * Output type.
 *
 * @author jub
 */
public enum OutputCategory {

    HTML("html", "groovy", "html"), HTML_SELECT("html.select", "groovy", "html", "gsp"),
    XML("xml", "xml", "xml"), PDF("pdf", "pdf", "pdf");

    private String outputCategory;
    private String outputFactoryType;
    private String templateSuffix;
    private String suffix;
    private boolean templateSelect;

    private OutputCategory(String outputCategory, String outputFactoryType, String suffix, String templateSuffix) {
        this(outputCategory, outputFactoryType, suffix);
        this.templateSuffix = templateSuffix;
        this.templateSelect = true;
    }

    private OutputCategory(String outputCategory, String outputFactoryType, String suffix) {
        this.outputCategory = outputCategory;
        this.outputFactoryType = outputFactoryType;
        this.suffix = suffix;
    }

    public String getOutputCategory() {
        return outputCategory;
    }

    public String getOutputFactoryType() {
        return outputFactoryType;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getTemplateSuffix() {
        return templateSuffix;
    }

    public boolean isTemplateSelect() {
        return templateSelect;
    }

    public static OutputCategory fromString(String string) {
        for (OutputCategory category : values()) {
            if (category.getOutputCategory().equals(string))
                return category;
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("OutputCategory<%s,%s>", getOutputCategory(), getOutputFactoryType());
    }
}
