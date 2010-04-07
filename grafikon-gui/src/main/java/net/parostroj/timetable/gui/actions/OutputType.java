package net.parostroj.timetable.gui.actions;

/**
 * Output type.
 *
 * @author jub
 */
public enum OutputType {

    HTML("html", "groovy", "html"), HTML_SELECT("html.select", "groovy", "html", "gsp"), XML("xml", "xml", "xml");

    private String outputType;
    private String outputFactoryType;
    private String templateSuffix;
    private String suffix;

    private OutputType(String outputType, String outputFactoryType, String suffix, String templateSuffix) {
        this(outputType, outputFactoryType, suffix);
        this.templateSuffix = templateSuffix;
    }

    private OutputType(String outputType, String outputFactoryType, String suffix) {
        this.outputType = outputType;
        this.outputFactoryType = outputFactoryType;
        this.suffix = suffix;
    }

    public String getOutputType() {
        return outputType;
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

    public static OutputType fromString(String string) {
        for (OutputType type : values()) {
            if (type.getOutputType().equals(string))
                return type;
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Type<%s,%s>", getOutputType(), getOutputFactoryType());
    }
}
