package net.parostroj.timetable.output2.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.parostroj.timetable.model.TranslatedString;

public class TStringAdapter extends XmlAdapter<LString, TranslatedString> {

    @Override
    public TranslatedString unmarshal(LString v) throws Exception {
        return v == null ? null : v.createLocalizedString();
    }

    @Override
    public LString marshal(TranslatedString v) throws Exception {
        return v == null ? null : new LString(v);
    }

}
