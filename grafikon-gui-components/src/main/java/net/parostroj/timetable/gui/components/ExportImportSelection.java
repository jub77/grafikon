package net.parostroj.timetable.gui.components;

import java.util.Collection;
import java.util.Map;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.imports.ImportMatch;

public class ExportImportSelection {

    private Map<ImportComponent, Collection<ObjectWithId>> objects;
    private boolean importOverwrite;
    private ImportMatch importMatch;

    public ExportImportSelection(Map<ImportComponent, Collection<ObjectWithId>> objects) {
        this.objects = objects;
    }

    public Map<ImportComponent, Collection<ObjectWithId>> getObjects() {
        return objects;
    }

    public void setObjects(Map<ImportComponent, Collection<ObjectWithId>> objects) {
        this.objects = objects;
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
}
