package net.parostroj.timetable.output2.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import net.parostroj.timetable.model.FreightColor;

/**
 * Adapter for freight colors.
 *
 * @author jub
 */
public class FreightColorAdapter extends XmlAdapter<String, FreightColor> {

    @Override
    public FreightColor unmarshal(String v) {
        return FreightColor.getByKey(v);
    }

    @Override
    public String marshal(FreightColor v) {
        return v.getKey();
    }
}
