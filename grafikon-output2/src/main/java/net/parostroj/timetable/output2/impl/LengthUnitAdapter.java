package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.parostroj.timetable.model.units.LengthUnit;

/**
 * Adapter for length unit.
 *
 * @author jub
 */
public class LengthUnitAdapter extends XmlAdapter<String, LengthUnit> {

    @Override
    public LengthUnit unmarshal(String v) throws Exception {
        return LengthUnit.getByKey(v);
    }

    @Override
    public String marshal(LengthUnit v) throws Exception {
        return v.getKey();
    }
}
