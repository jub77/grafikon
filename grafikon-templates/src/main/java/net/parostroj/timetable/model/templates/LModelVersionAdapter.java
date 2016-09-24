package net.parostroj.timetable.model.templates;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.parostroj.timetable.model.ls.ModelVersion;

public class LModelVersionAdapter extends XmlAdapter<String, ModelVersion> {

    @Override
    public ModelVersion unmarshal(String v) throws Exception {
        return v != null ? ModelVersion.parseModelVersion(v) : null;
    }

    @Override
    public String marshal(ModelVersion v) throws Exception {
        return v != null ? v.getVersion() : null;
    }

}
