package net.parostroj.timetable.gui.views;

import java.util.HashMap;
import java.util.Map;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Wrapper for combo box.
 *
 * @author jub
 */
public class NodeTypeWrapper {

    private NodeType type;

    private String text;
    
    private static Map<NodeType,NodeTypeWrapper> wrappers = new HashMap<NodeType,NodeTypeWrapper>();
    
    static {
        for (NodeType type : NodeType.values()) {
            wrappers.put(type, new NodeTypeWrapper(type));
        }
    }
    
    private NodeTypeWrapper(NodeType type) {
        this.type = type;
    }

    public NodeType getType() {
        return type;
    }

    public String getText() {
        if (text == null) {
            text = ResourceLoader.getString("node." + type.getKey() + ".text");
            if (text == null)
                text = "<unknown>";
        }
        return text;
    }

    @Override
    public String toString() {
        return this.getText();
    }
    
    public static NodeTypeWrapper getWrapper(NodeType type) {
        return wrappers.get(type);
    }
}
