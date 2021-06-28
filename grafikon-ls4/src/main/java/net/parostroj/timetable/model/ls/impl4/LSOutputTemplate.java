package net.parostroj.timetable.model.ls.impl4;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import net.parostroj.timetable.model.Attachment;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.PartFactory;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Storage for output templates.
 *
 * @author jub
 */
@XmlRootElement(name = "output_template")
@XmlType(name = "output_template", propOrder = {"id", "name", "template", "script", "attachments", "attributes"})
public class LSOutputTemplate {

    private String id;
    // obsolote - loading old saves -> mapped to key
    private String name;
    private LSTextTemplate template;
    private LSScript script;
    private List<LSAttachment> attachments;
    private LSAttributes attributes;

    public LSOutputTemplate() {
    }

    public LSOutputTemplate(OutputTemplate template) {
        this(template, (attachment, attachments) -> {
            LSAttachment lsAttachment = new LSAttachment(attachment.getName(), attachment.getType().name());
            switch (attachment.getType()) {
                case BINARY: lsAttachment.setBinaryData(attachment.getBinary()); break;
                case TEXT: lsAttachment.setTextData(attachment.getText()); break;
            }
            attachments.add(lsAttachment);
        });
    }

    public LSOutputTemplate(OutputTemplate template, FileLoadSaveAttachments flsAttachments) {
        this(template, (attachment, attachments) -> {
            String reference = flsAttachments.createReference(attachment);
            LSAttachment lsAttachment = new LSAttachment(attachment.getName(), attachment.getType().name(), reference);
            attachments.add(lsAttachment);
        });
    }

    private LSOutputTemplate(OutputTemplate template, BiConsumer<Attachment, List<LSAttachment>> attachmentConverter) {
        this.id = template.getId();
        if (template.getTemplate() != null) {
            this.template = new LSTextTemplate(template.getTemplate());
        }
        this.attributes = new LSAttributes(template.getAttributes());
        this.script = template.getScript() != null ? new LSScript(template.getScript()) : null;
        // attachments
        this.attachments = new ArrayList<>();
        for (Attachment attachment : template.getAttachments()) {
            attachmentConverter.accept(attachment, this.attachments);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LSTextTemplate getTemplate() {
        return template;
    }

    public void setTemplate(LSTextTemplate template) {
        this.template = template;
    }

    public LSScript getScript() {
        return script;
    }

    public void setScript(LSScript script) {
        this.script = script;
    }

    @XmlElement(name = "attachment")
    public List<LSAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<LSAttachment> attachments) {
        this.attachments = attachments;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public OutputTemplate createOutputTemplate(PartFactory partFactory, Function<String, ObjectWithId> mapping) throws LSException {
        return this.createOutputTemplate(partFactory, mapping, null);
    }

    public OutputTemplate createOutputTemplate(PartFactory partFactory, Function<String, ObjectWithId> mapping, FileLoadSaveAttachments flsAttachments) throws LSException {
        OutputTemplate outputTemplate = partFactory.createOutputTemplate(id);
        if (name != null) {
            // name mapped to key
            outputTemplate.setKey(name);
        }
        if (this.template != null) {
            outputTemplate.setTemplate(this.template.createTextTemplate());
        }
        if (this.script != null) {
            outputTemplate.setScript(this.script.createScript());
        }
        outputTemplate.getAttributes().add(attributes.createAttributes(mapping));
        // process attachments
        if (attachments != null) {
            for (LSAttachment attachment : attachments) {
                if (attachment.getRef() != null) {
                    if (flsAttachments == null) {
                        throw new LSException("Attachment loader cannot be null");
                    }
                    flsAttachments.addForLoad(attachment, outputTemplate);
                } else {
                    // process inline data
                    if (attachment.getBinaryData() != null) {
                        outputTemplate.getAttachments()
                                .add(new Attachment(attachment.getName(), attachment.getBinaryData()));
                    } else if (attachment.getTextData() != null) {
                        outputTemplate.getAttachments()
                                .add(new Attachment(attachment.getName(), attachment.getTextData()));
                    }
                }
            }
        }
        return outputTemplate;
    }
}
