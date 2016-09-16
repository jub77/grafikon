package net.parostroj.timetable.model.ls;

public interface LSConfigurable {

    public static final String VERSION_PROPERTY = "version";

    Object getProperty(String key);

    void setProperty(String key, Object value);


}
