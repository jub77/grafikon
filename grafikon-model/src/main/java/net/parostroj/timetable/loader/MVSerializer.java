package net.parostroj.timetable.loader;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import net.parostroj.timetable.model.ls.ModelVersion;

import java.io.IOException;

public class MVSerializer extends StdSerializer<ModelVersion> {

    private static final long serialVersionUID = 1L;

    public MVSerializer() {
        super(ModelVersion.class);
    }

    @Override
    public void serialize(ModelVersion modelVersion, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(modelVersion.getVersion());
    }
}
