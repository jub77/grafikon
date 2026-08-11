package net.parostroj.timetable.loader;

import net.parostroj.timetable.model.LocalizedString;
import tools.jackson.core.JsonParser;
import tools.jackson.core.TreeNode;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.node.ObjectNode;

import java.util.Map;

public class LStringDeserializer extends StdDeserializer<LocalizedString> {

    public LStringDeserializer() {
        super(LocalizedString.class);
    }

    @Override
    public LocalizedString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) {
        TreeNode treeNode = jsonParser.readValueAsTree();
        if (treeNode instanceof ObjectNode objectNode) {
            LocalizedString.Builder builder = LocalizedString.newBuilder();
            for (Map.Entry<String, JsonNode> entry : objectNode.properties()) {
                if (entry.getKey().equals("default")) {
                    builder.setDefaultString(entry.getValue().stringValue());
                } else {
                    builder.addStringWithLocale(entry.getValue().stringValue(), entry.getKey());
                }
            }
            return builder.build();
        } else {
            return null;
        }
    }
}
