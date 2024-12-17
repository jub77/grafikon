package net.parostroj.timetable.model.ls;

public interface LSConfigurable {

    String VERSION_PROPERTY = "version";

    Object getProperty(String key);

    void setProperty(String key, Object value);


}
