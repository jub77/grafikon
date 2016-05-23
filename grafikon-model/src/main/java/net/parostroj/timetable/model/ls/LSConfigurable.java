package net.parostroj.timetable.model.ls;

public interface LSConfigurable {

    Object getProperty(String key);

    void setProperty(String key, Object value);


}
