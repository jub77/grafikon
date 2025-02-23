package net.parostroj.timetable.loader;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.parostroj.timetable.model.LocalizedString;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * List of data items.
 *
 * @author jub
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataItemList(String id, LocalizedString name, LocalizedString description, List<DataItem> items,
        List<DataItemList> categories) {
    public Stream<DataItem> toItemStream() {
        return categories == null || categories.isEmpty()
                ? items.stream()
                : Stream.concat(items.stream(), categories.stream().flatMap(DataItemList::toItemStream));
    }

    public Collection<DataItem> toItemCollection() {
        return toItemStream().collect(Collectors.toList());
    }
}
