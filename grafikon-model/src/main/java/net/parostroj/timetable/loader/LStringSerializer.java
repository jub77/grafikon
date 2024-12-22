package net.parostroj.timetable.loader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.parostroj.timetable.model.LocalizedString;

import java.io.IOException;

public class LStringSerializer extends StdSerializer<LocalizedString> {

    public LStringSerializer() {
        super(LocalizedString.class);
    }

    @Override
    public void serialize(LocalizedString localizedString, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("default", localizedString.getDefaultString());
        for (LocalizedString.StringWithLocale stringWithLocale : localizedString.getLocalizedStrings()) {
            jsonGenerator.writeStringField(stringWithLocale.getLocale().toLanguageTag(), stringWithLocale.getString());
        }
        jsonGenerator.writeEndObject();
    }
}
