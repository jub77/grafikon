package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItem;
import net.parostroj.timetable.loader.DataItemList;
import net.parostroj.timetable.loader.DataItemLoader;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.library.Library;
import net.parostroj.timetable.model.library.LibraryItemType;
import net.parostroj.timetable.model.ls.LSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataOutputTemplateStorage implements OutputTemplateStorage {

    private static final Logger log = LoggerFactory.getLogger(DataOutputTemplateStorage.class);

    private record CategoryTemplates(Category category, Collection<OutputTemplate> templates) {}

    private final DataItemLoader<Library> loader;
    private final DataOutputTemplateStorage wrapped;
    private final String source;
    private Map<String, OutputTemplate> outputTemplates;
    private Map<Category, Collection<OutputTemplate>> outputTemplatesByCategory;

    public DataOutputTemplateStorage(DataItemLoader<Library> loader, String source) {
        this.loader = loader;
        this.source = source;
        this.wrapped = null;
        this.outputTemplates = Map.of();
        this.outputTemplatesByCategory = Map.of();
    }

    public DataOutputTemplateStorage(DataItemLoader<Library> loader, String source, DataOutputTemplateStorage wrapped) {
        this.loader = loader;
        this.wrapped = wrapped;
        this.source = source;
        this.outputTemplates = Map.of();
        this.outputTemplatesByCategory = Map.of();
    }

    public Map<Category, Collection<OutputTemplate>> loadTemplates() {
        Map<Category, Collection<OutputTemplate>> templates;
        try {
            DataItemList list = loader.loadList();

            templates = list.toItemStream()
                    .map(this::getItems)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(CategoryTemplates::category, CategoryTemplates::templates));
        } catch (LSException e) {
            log.warn("Error loading template list ({})", e.getMessage());
            templates = wrapped == null ? Map.of() : wrapped.loadTemplates();
        }
        outputTemplatesByCategory = Map.copyOf(templates);
        outputTemplates = Map.copyOf(outputTemplatesByCategory.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(OutputTemplate::getId, Function.identity())));
        return outputTemplatesByCategory;
    }

    private void addSource(OutputTemplate template) {
        template.setAttribute(OutputTemplate.ATTR_SOURCE, source);
    }

    private CategoryTemplates getItems(DataItem item) {
        try {
            Category category = new Category(item.id(), item.name());
            return new CategoryTemplates(category, loader.loadItem(item).getItems().get(LibraryItemType.OUTPUT_TEMPLATE)
                    .stream()
                    .map(o -> (OutputTemplate) o.getObject())
                    .peek(this::addSource)
                    .toList());
        } catch (LSException e) {
            log.warn("Error loading templates ({})", e.getMessage());
            return null;
        }
    }

    @Override
    public Collection<OutputTemplate> getTemplates() {
        return outputTemplates.values();
    }

    @Override
    public Map<Category, Collection<OutputTemplate>> getTemplatesByCategory() {
        return outputTemplatesByCategory;
    }

    @Override
    public OutputTemplate getTemplateById(String id) {
        return outputTemplates.get(id);
    }
}
