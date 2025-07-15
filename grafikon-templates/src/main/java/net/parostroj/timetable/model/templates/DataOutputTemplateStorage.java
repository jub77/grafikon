package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItem;
import net.parostroj.timetable.loader.DataItemList;
import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItem;
import net.parostroj.timetable.model.library.LibraryItemType;
import net.parostroj.timetable.model.ls.LSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataOutputTemplateStorage implements OutputTemplateStorage {

    private static final Logger log = LoggerFactory.getLogger(DataOutputTemplateStorage.class);

    private final DataItemLoader<Library> loader;
    private final DataOutputTemplateStorage wrapped;
    private final String source;
    private Map<String, OutputTemplate> outputTemplates;

    private static final Category DEFAULT_CATEGORY = new Category("default", LocalizedString.fromString("Default"));

    public DataOutputTemplateStorage(DataItemLoader<Library> loader, String source) {
        this.loader = loader;
        this.source = source;
        this.wrapped = null;
        this.outputTemplates = Map.of();
    }

    public DataOutputTemplateStorage(DataItemLoader<Library> loader, String source, DataOutputTemplateStorage wrapped) {
        this.loader = loader;
        this.wrapped = wrapped;
        this.source = source;
        this.outputTemplates = Map.of();
    }

    public Map<String, OutputTemplate> loadTemplates() {
        Map<String, OutputTemplate> templates;
        try {
            DataItemList list = loader.loadList();
            templates = list.toItemStream()
                    .map(this::getItems)
                    .flatMap(Collection::stream)
                    .map(LibraryItem::getObject)
                    .map(object -> (OutputTemplate) object)
                    .peek(this::addSource)
                    .collect(Collectors.toMap(OutputTemplate::getId, Function.identity()));
        } catch (LSException e) {
            log.warn("Error loading template list ({})", e.getMessage());
            templates = wrapped == null ? Map.of() : wrapped.loadTemplates();
        }
        outputTemplates = Map.copyOf(templates);
        return outputTemplates;
    }

    private void addSource(OutputTemplate template) {
        template.setAttribute(OutputTemplate.ATTR_SOURCE, source);
    }

    private Collection<LibraryItem> getItems(DataItem item) {
        try {
            return loader.loadItem(item).getItems().get(LibraryItemType.OUTPUT_TEMPLATE);
        } catch (LSException e) {
            log.warn("Error loading templates ({})", e.getMessage());
            return List.of();
        }
    }

    @Override
    public Collection<OutputTemplate> getTemplates() {
        return outputTemplates.values();
    }

    @Override
    public Map<Category, Collection<OutputTemplate>> getTemplatesByCategory() {
        return Map.of(DEFAULT_CATEGORY, outputTemplates.values());
    }

    @Override
    public OutputTemplate getTemplateById(String id) {
        return outputTemplates.get(id);
    }
}
