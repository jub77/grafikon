package net.parostroj.timetable.output2.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import net.parostroj.timetable.model.TranslatedString;

public class TStringListAdapter extends XmlAdapter<List<LString>, List<TranslatedString>> {

    @Override
    public List<TranslatedString> unmarshal(List<LString> v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return v.stream().map(item -> item.createLocalizedString()).collect(Collectors.toList());
        }
    }

    @Override
    public List<LString> marshal(List<TranslatedString> v) throws Exception {
        if (v == null) {
            return null;
        } else {
            return v.stream().map(item -> new LString(item)).collect(Collectors.toList());
        }
    }
}
