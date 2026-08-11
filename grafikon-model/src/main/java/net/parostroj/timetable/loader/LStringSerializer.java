package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.LocalizedString;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class LStringSerializer extends StdSerializer<LocalizedString> {

    public LStringSerializer() {
        super(LocalizedString.class);
    }

    @Override
    public void serialize(LocalizedString localizedString, JsonGenerator jsonGenerator, SerializationContext serializationContext) {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringProperty("default", localizedString.getDefaultString());
        for (LocalizedString.StringWithLocale stringWithLocale : localizedString.getLocalizedStrings()) {
            jsonGenerator.writeStringProperty(stringWithLocale.getLocale().toLanguageTag(), stringWithLocale.getString());
        }
        jsonGenerator.writeEndObject();
    }
}
