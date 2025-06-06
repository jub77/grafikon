package net.parostroj.timetable.loader;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import net.parostroj.timetable.model.ls.ModelVersion;

import java.io.IOException;

public class MVDeserializer extends StdDeserializer<ModelVersion> {

    private static final long serialVersionUID = 1L;

    public MVDeserializer() {
        super(ModelVersion.class);
    }

    @Override
    public ModelVersion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode treeNode = jsonParser.readValueAsTree();
        if (treeNode instanceof TextNode textNode) {
            return ModelVersion.parseModelVersion(textNode.textValue());
        } else {
            return null;
        }
    }
}
