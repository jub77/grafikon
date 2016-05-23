package net.parostroj.timetable.model.library;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.Attributes;
import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.CopyFactory;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeTrack;
import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.OutputTemplate;

public class Library implements AttributesHolder {

    private static final Logger log = LoggerFactory.getLogger(Library.class);

    private final Collection<LibraryItem> items;
    private final Attributes attributes;

    Library() {
        items = new ArrayList<>();
        attributes = new Attributes();
    }

    public Collection<LibraryItem> getItems() {
        return items;
    }

    @Override
    public Attributes getAttributes() {
        return attributes;
    }

    public LibraryItem add(OutputTemplate template) {
        OutputTemplate templateCopy = CopyFactory.getInstance().copy(template);
        this.stripObjectIdAttributes(templateCopy);

        // create item and add it to library
        return addImpl(templateCopy, LibraryItemType.OUTPUT_TEMPLATE);
    }

    public LibraryItem add(Node node) {
        Node nodeCopy = CopyFactory.getInstance().copy(node);
        this.stripObjectIdAttributes(nodeCopy);
        for (NodeTrack track : nodeCopy.getTracks()) {
            this.stripObjectIdAttributes(track);
        }

        // create item and add it to library
        return addImpl(nodeCopy, LibraryItemType.NODE);
    }

    private LibraryItem addImpl(ObjectWithId object, LibraryItemType type) {
        LibraryItemDescription description = new LibraryItemDescription(type);
        LibraryItem item = new LibraryItem(description, object);
        items.add(item);
        return item;
    }

    private void stripObjectIdAttributes(AttributesHolder holder) {
        this.stripObjectIdAttributes(holder, null);
        for (String category : holder.getAttributes().getCategories()) {
            this.stripObjectIdAttributes(holder, category);
        }
    }

    private void stripObjectIdAttributes(AttributesHolder holder, String category) {
        for (String key : ImmutableList.copyOf(holder.getAttributes().getAttributesMap(category).keySet())) {
            Object value = holder.getAttributes().get(category, key);
            boolean remove = value instanceof ObjectWithId;
            if (!remove && value instanceof Collection) {
                for (Object item : (Collection<?>) value) {
                    if (item instanceof ObjectWithId) {
                        remove = true;
                        break;
                    }
                }
            }
            if (remove) {
                log.debug("Removing key {} in category {}", key, category == null ? "<none>" : category);
                holder.removeAttribute(category, key);
            }
        }
    }
}
