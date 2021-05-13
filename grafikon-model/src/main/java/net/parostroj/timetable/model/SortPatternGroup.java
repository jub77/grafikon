package net.parostroj.timetable.model;

/**
 * Row for sort pattern. Holds information about group and comparison type.
 *
 * @author jub
 */
public class SortPatternGroup {

    public enum Type {

        STRING("string"), NUMBER("number");
        private final String id;

        Type(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public static Type fromId(String id) {
            for (Type type : values()) {
                if (type.getId().equals(id)) {
                    return type;
                }
            }
            return null;
        }
    }

    private final int group;
    private final Type type;

    public SortPatternGroup(int group, Type type) {
        this.group = group;
        this.type = type;
    }

    public int getGroup() {
        return group;
    }

    public Type getType() {
        return type;
    }
}
