package net.parostroj.timetable.model.library;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.ObjectWithId;

/**
 * Helper actions for library.
 *
 * @author jub
 */
class LibraryAddHandler {

    private static final Logger log = LoggerFactory.getLogger(LibraryAddHandler.class);

    public boolean checkIfValueContainsObjectWithId(Object value) {
        boolean remove = value instanceof ObjectWithId;
        if (!remove && value instanceof Collection) {
            remove = ((Collection<?>) value).stream().anyMatch(item -> item instanceof ObjectWithId);
        }
        return remove;
    }

    public void stripObjectIdAttributes(AttributesHolder holder) {
        this.stripObjectIdAttributes(holder, null);
        for (String category : holder.getAttributes().getCategories()) {
            this.stripObjectIdAttributes(holder, category);
        }
    }

    public void stripObjectIdAttributes(AttributesHolder holder, String category) {
        for (String key : ImmutableList.copyOf(holder.getAttributes().getAttributesMap(category).keySet())) {
            Object value = holder.getAttributes().get(category, key);
            if (checkIfValueContainsObjectWithId(value)) {
                log.debug("Removing key {} in category {}", key, category == null ? "<none>" : category);
                holder.removeAttribute(category, key);
            }
        }
    }

}
