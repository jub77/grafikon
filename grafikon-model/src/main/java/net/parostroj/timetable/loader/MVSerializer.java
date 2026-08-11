package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.ModelVersion;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class MVSerializer extends StdSerializer<ModelVersion> {

    public MVSerializer() {
        super(ModelVersion.class);
    }

    @Override
    public void serialize(ModelVersion modelVersion, JsonGenerator jsonGenerator, SerializationContext context) {
        jsonGenerator.writeString(modelVersion.getVersion());
    }
}
