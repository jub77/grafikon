package net.parostroj.timetable.gui.components;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.ImportMatch;

public class ExportImportSelection {

    private ListMultimap<ImportComponent, ObjectWithId> objects;
    private boolean importOverwrite;
    private ImportMatch importMatch;

    public ExportImportSelection() {
        this(Collections.emptyMap());
    }

    public ExportImportSelection(Map<ImportComponent, Collection<ObjectWithId>> map) {
        this.objects = LinkedListMultimap.create(ImportComponent.values().length);
        map.entrySet().stream().forEach(entry -> objects.putAll(entry.getKey(), entry.getValue()));
    }

    public Map<ImportComponent, Collection<ObjectWithId>> getObjectMap() {
        return objects.asMap();
    }

    public ListMultimap<ImportComponent, ObjectWithId> getObjects() {
        return objects;
    }

    public void addItems(ImportComponent component, Iterable<? extends ObjectWithId> iterable) {
        objects.putAll(component, iterable);
    }

    public boolean isImportOverwrite() {
        return importOverwrite;
    }

    public void setImportOverwrite(boolean overwrite) {
        this.importOverwrite = overwrite;
    }

    public ImportMatch getImportMatch() {
        return importMatch;
    }

    public void setImportMatch(ImportMatch importMatch) {
        this.importMatch = importMatch;
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s", importMatch, importOverwrite, objects);
    }
}
