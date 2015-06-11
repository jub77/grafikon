package net.parostroj.timetable.gui.actions.impl;

/**
 * Output type.
 *
 * @author jub
 */
public enum OutputCategory {

    HTML("html", "groovy", "html"), XML("xml", "xml", "xml"), PDF("pdf", "pdf.groovy", "pdf");

    private String outputCategory;
    private String outputFactoryType;
    private String suffix;

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

    public static OutputCategory fromString(String string) {
        for (OutputCategory category : values()) {
            if (category.getOutputCategory().equals(string)) {
                return category;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("OutputCategory<%s,%s>", getOutputCategory(), getOutputFactoryType());
    }
}
