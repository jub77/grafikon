package net.parostroj.timetable.model.ls.impl4;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.common.io.ByteStreams;

import net.parostroj.timetable.model.Attachment;
import net.parostroj.timetable.model.Attachment.AttachmentType;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.utils.Pair;

public class FileLoadSaveAttachments {

    private static final Charset TEXT_ENCODING = StandardCharsets.UTF_8;

    private int counter;
    private final String location;
    private final Map<String, Attachment> attachmentMap;
    private final Map<String, Pair<LSAttachment, OutputTemplate>> templateMap;

    public FileLoadSaveAttachments(String location) {
        this.counter = 0;
        this.attachmentMap = new HashMap<>();
        this.templateMap = new HashMap<>();
        this.location = location;
    }

    public String createReference(Attachment attachment) {
        String reference = String.format("%06d", counter++);
        attachmentMap.put(reference, attachment);
        return reference;
    }

    public void addForLoad(LSAttachment attachment, OutputTemplate template) {
        templateMap.put(attachment.getRef(), new Pair<>(attachment, template));
    }

    public void save(ZipOutputStream zipOutput) throws IOException {
        for (Map.Entry<String, Attachment> entry : attachmentMap.entrySet()) {
            String reference = entry.getKey();
            Attachment attachment = entry.getValue();
            ZipEntry zipEntry = new ZipEntry(location + reference);
            byte[] bytes = this.getBytes(attachment);
            zipEntry.setSize(bytes.length);
            zipOutput.putNextEntry(zipEntry);
            zipOutput.write(bytes);
        }
    }

    public void load(ZipInputStream zipInput, ZipEntry entry) throws IOException {
        String ref = entry.getName().substring(location.length());
        Pair<LSAttachment, OutputTemplate> pair = templateMap.get(ref);
        byte[] bytes = ByteStreams.toByteArray(zipInput);
        Attachment attachment = getAttachment(pair, bytes);
        pair.second.getAttachments().add(attachment);
    }

    private byte[] getBytes(Attachment attachment) {
        return attachment.toArray(TEXT_ENCODING);
    }

    private Attachment getAttachment(Pair<LSAttachment, OutputTemplate> pair, byte[] bytes) {
        AttachmentType type = AttachmentType.valueOf(pair.first.getType());
        Attachment attachment = null;
        if (type == AttachmentType.BINARY) {
            attachment = new Attachment(pair.first.getName(), bytes);
        } else {
            String text = new String(bytes, TEXT_ENCODING);
            attachment = new Attachment(pair.first.getName(), text);
        }
        return attachment;
    }
}
