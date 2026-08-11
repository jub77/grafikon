package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.ls.ModelVersion;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.StringNode;

public class MVDeserializer extends StdDeserializer<ModelVersion> {

    public MVDeserializer() {
        super(ModelVersion.class);
    }

    @Override
    public ModelVersion deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        TreeNode treeNode = jsonParser.readValueAsTree();
        if (treeNode instanceof StringNode textNode) {
            return ModelVersion.parseModelVersion(textNode.stringValue());
        } else {
            return null;
        }
    }
}
