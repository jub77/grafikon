package net.parostroj.timetable.model.imports;

public enum ImportMatch {

    NAME("import.match.name"),
    ID("import.match.id");
    private String key;

    private ImportMatch(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
