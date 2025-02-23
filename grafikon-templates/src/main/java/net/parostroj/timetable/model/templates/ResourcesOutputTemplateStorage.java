package net.parostroj.timetable.model.templates;

import net.parostroj.timetable.loader.DataItem;
import net.parostroj.timetable.loader.DataItemList;
import net.parostroj.timetable.model.OutputTemplate;
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

public class ResourcesOutputTemplateStorage implements OutputTemplateStorage {

    private static final Logger log = LoggerFactory.getLogger(ResourcesOutputTemplateStorage.class);
    private final Map<String, OutputTemplate> outputTemplates;

    public ResourcesOutputTemplateStorage() {
        Map<String, OutputTemplate> templates;
        try {
            DataItemList list = OutputsLoader.getDefault().loadList();
            templates = list.toItemCollection().stream()
                    .map(ResourcesOutputTemplateStorage::getItems)
                    .flatMap(Collection::stream)
                    .map(LibraryItem::getObject)
                    .map(object -> (OutputTemplate) object)
                    .collect(Collectors.toMap(OutputTemplate::getId, Function.identity()));
        } catch (LSException e) {
            log.warn("Error loading template list", e);
            templates = Map.of();
        }
        outputTemplates = templates;
    }

    private static Collection<LibraryItem> getItems(DataItem item) {
        try {
            return OutputsLoader.getDefault().loadItem(item).getItems().get(LibraryItemType.OUTPUT_TEMPLATE);
        } catch (LSException e) {
            log.warn("Error loading templates", e);
            return List.of();
        }
    }

    @Override
    public Collection<OutputTemplate> getTemplates() {
        return outputTemplates.values();
    }

    @Override
    public OutputTemplate getTemplateById(String id) {
        return outputTemplates.get(id);
    }
}
