package net.parostroj.timetable.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class Attachment {

    public enum AttachmentType {
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

    public InputStream toStream() {
        return toStream(StandardCharsets.UTF_8);
    }

    public InputStream toStream(Charset encoding) {
        return new ByteArrayInputStream(toArray(encoding));
    }

    public byte[] toArray() {
        return toArray(StandardCharsets.UTF_8);
    }

    public byte[] toArray(Charset encoding) {
        return type == AttachmentType.BINARY ? binary : text.getBytes(encoding);
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%d)", name, type, type == AttachmentType.TEXT ? text.length() : binary.length);
    }
}
