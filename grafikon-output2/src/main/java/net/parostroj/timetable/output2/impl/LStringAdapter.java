package net.parostroj.timetable.output2.impl;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import net.parostroj.timetable.model.LocalizedString;

public class LStringAdapter extends XmlAdapter<LString, LocalizedString> {

    @Override
    public LocalizedString unmarshal(LString v) {
        return v == null ? null : v.createLocalizedString();
    }

    @Override
    public LString marshal(LocalizedString v) {
        return v == null ? null : new LString(v);
    }

}
