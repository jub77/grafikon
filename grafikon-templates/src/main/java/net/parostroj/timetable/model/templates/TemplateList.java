package net.parostroj.timetable.model.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.parostroj.timetable.model.LocalizedString;

/**
 * List of templates. Loaded by JAXB.
 *
 * @author jub
 */
@XmlRootElement(name = "template_list")
@XmlType(propOrder = { "id", "name", "description", "templates", "categories" })
public class TemplateList {

    private String id;
    private LocalizedString name;
    private LocalizedString description;
    private List<Template> templates;
    private List<TemplateList> categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getName() {
        return name;
    }

    public void setName(LocalizedString name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(type = LString.class, value = LStringAdapter.class)
    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    @XmlElement(name = "template")
    public List<Template> getTemplates() {
        if (templates == null) {
            templates = new ArrayList<>();
        }
        return templates;
    }

    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }

    @XmlElement(name = "category")
    public List<TemplateList> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    public void setCategories(List<TemplateList> categories) {
        this.categories = categories;
    }

    public Stream<Template> toTemplateStream() {
        Stream<Template> stream = getTemplates().stream();
        for (TemplateList category : getCategories()) {
            stream = Stream.concat(stream, category.toTemplateStream());
        }
        return stream;
    }

    public Collection<Template> toTemplateCollection() {
        return toTemplateStream().collect(Collectors.toList());
    }
}
