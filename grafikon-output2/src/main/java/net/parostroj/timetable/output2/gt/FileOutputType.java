package net.parostroj.timetable.output2.gt;

public enum FileOutputType {
    SVG("svg"), PNG("png"), PDF("pdf");

    private final String extension;

    FileOutputType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static FileOutputType fromString(String text) {
        for (FileOutputType type : values()) {
            if (type.extension.equals(text)) {
                return type;
            }
        }
        return null;
    }
}
