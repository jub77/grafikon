package net.parostroj.timetable.loader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.parostroj.timetable.model.LocalizedString;

import java.io.IOException;
import java.util.Map;

public class LStringDeserializer extends StdDeserializer<LocalizedString> {

    private static final long serialVersionUID = 1L;

    public LStringDeserializer() {
        super(LocalizedString.class);
    }

    @Override
    public LocalizedString deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode treeNode = jsonParser.readValueAsTree();
        if (treeNode instanceof ObjectNode objectNode) {
            LocalizedString.Builder builder = LocalizedString.newBuilder();
            for (Map.Entry<String, JsonNode> entry : objectNode.properties()) {
                if (entry.getKey().equals("default")) {
                    builder.setDefaultString(entry.getValue().textValue());
                } else {
                    builder.addStringWithLocale(entry.getValue().textValue(), entry.getKey());
                }
            }
            return builder.build();
        } else {
            return null;
        }
    }
}
