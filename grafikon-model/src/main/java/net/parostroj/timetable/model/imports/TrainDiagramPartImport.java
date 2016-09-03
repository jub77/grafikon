package net.parostroj.timetable.model.imports;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.imports.Import.ImportError;

public class TrainDiagramPartImport {

    private Map<ImportComponent, Import> imports;
    private TrainDiagram diagram;
    private ImportMatch match;
    private boolean overwrite;

    public TrainDiagramPartImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        this.diagram = diagram;
        this.match = match;
        this.overwrite = overwrite;
        this.imports = new EnumMap<>(ImportComponent.class);
    }

    public ObjectWithId importPart(ObjectWithId object) {
        Import importPart = getImportPart(object);
        return importPart.importObject(object);
    }

    public List<ImportError> getErrors(ImportComponent component) {
        if (imports.containsKey(component)) {
            return imports.get(component).getErrors();
        } else {
            return Collections.emptyList();
        }
    }

    private Import getImportPart(ObjectWithId object) {
        ImportComponent component = ImportComponent.getByComponentClass(object.getClass());
        if (component == null) {
            throw new IllegalArgumentException("Unknown type of object for import: " + object.getClass().getName());
        }
        if (!imports.containsKey(component)) {
            Import importPart = Import.getInstance(component, diagram, match, overwrite);
            imports.put(component, importPart);
        }
        return imports.get(component);
    }
}
