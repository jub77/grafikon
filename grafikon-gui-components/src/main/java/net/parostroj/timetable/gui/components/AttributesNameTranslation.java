package net.parostroj.timetable.gui.components;

import java.util.function.Function;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.LocalizedString;

public class AttributesNameTranslation implements Function<String, String> {

    private Attributes attributes;
    private String category;

    public AttributesNameTranslation(Attributes attributes, String category) {
        this.attributes = attributes;
        this.category = category;
    }

    @Override
    public String apply(String name) {
        LocalizedString lString = attributes.get(category, name, LocalizedString.class);
        return lString == null ? name : lString.translate();
    }
}
