package net.parostroj.timetable.model;

public class Attachment {

    public static enum AttachmentType {
        TEXT, BINARY
    }

    private final String name;
    private final AttachmentType type;
    private final String text;
    private final byte[] binary;

    public Attachment(String name, String text) {
        this(name, AttachmentType.TEXT, text, null);
    }

    public Attachment(String name, byte[] binary) {
        this(name, AttachmentType.BINARY, null, binary);
    }

    private Attachment(String name, AttachmentType type, String text, byte[] binary) {
        this.name = name;
        this.type = type;
        this.text = text;
        this.binary = binary;
    }

    public String getName() {
        return name;
    }

    public AttachmentType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public byte[] getBinary() {
        return binary;
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%d)", name, type, type == AttachmentType.TEXT ? text.length() : binary.length);
    }
}
