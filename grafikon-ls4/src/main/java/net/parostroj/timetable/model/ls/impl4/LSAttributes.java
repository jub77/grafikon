package net.parostroj.timetable.model.ls.impl4;

import java.util.*;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.ls.LSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Storage class for attributes.
 *
 * @author jub
 */
@XmlRootElement(name = "attributes")
public class LSAttributes {

    private static final Logger log = LoggerFactory.getLogger(LSAttributes.class);

    private List<LSAttributesItem> attributes;

    /**
     * Default constructor.
     */
    public LSAttributes() {
    }

    public LSAttributes(Attributes attributes, String... ignore) {
        this.attributes = new LinkedList<>();
        Set<String> ignoreMap = this.getMap(ignore);
        this.addAttributes(attributes.getAttributesMap(), null, ignoreMap);
        for (String category : attributes.getCategories()) {
            this.addAttributes(attributes.getAttributesMap(category), category, ignoreMap);
        }
    }

    private void addAttributes(Map<String, Object> map, String category, Set<String> ignoreMap) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != null && !ignoreMap.contains(entry.getKey())) {
                this.attributes.add(new LSAttributesItem(entry.getKey(), entry.getValue(), category));
            }
        }
    }

    private Set<String> getMap(String[] ignore) {
        if (ignore.length == 0) {
            return Collections.<String>emptySet();
        } else {
            return new HashSet<>(Arrays.asList(ignore));
        }
    }

    @XmlElement(name = "attribute")
    public List<LSAttributesItem> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<LSAttributesItem> attributes) {
        this.attributes = attributes;
    }

    public Attributes createAttributes() throws LSException {
        return this.createAttributes(null);
    }

    public Attributes createAttributes(LSContext context) throws LSException {
        Attributes lAttributes = new Attributes();
        if (this.attributes != null) {
            for (LSAttributesItem lItem : this.attributes) {
                Object value = lItem.convertValue(context);
                if (value != null) {
                    lAttributes.set(lItem.getCategory(), lItem.getKey(), value);
                } else {
                    log.debug("Null value for attribute: {}, value: {}", lItem.getKey(), lItem.getValues());
                }
            }
        }
        return lAttributes;
    }
}
