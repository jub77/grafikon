package net.parostroj.timetable.output2.gt;

public enum FileOutputType {
    SVG("svg"), PNG("png"), PDF("pdf");

    private String extension;

    private FileOutputType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
