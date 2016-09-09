package net.parostroj.timetable.gui.components;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.FluentIterable;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.imports.ImportComponent;
import net.parostroj.timetable.model.library.Library;

public interface ExportImportSelectionSource {

    Collection<ImportComponent> getTypes();
    Collection<ObjectWithId> getElementsForType(ImportComponent type);

    static ExportImportSelectionSource fromDiagramToLibrary(TrainDiagram diagram) {
        return new ExportImportSelectionSource() {
            @Override
            public Collection<ImportComponent> getTypes() {
                return FluentIterable.of(ImportComponent.values()).filter(item -> item.getLibraryItemType() != null).toList();
            }
            @Override
            public Collection<ObjectWithId> getElementsForType(ImportComponent type) {
                return type.getObjects(diagram);
            }
        };
    }

    static ExportImportSelectionSource fromLibraryToDiagram(final Library library) {
        return new ExportImportSelectionSource() {
            @Override
            public Collection<ImportComponent> getTypes() {
                return FluentIterable.of(ImportComponent.values()).filter(item -> item.getLibraryItemType() != null).toList();
            }
            @Override
            public Collection<ObjectWithId> getElementsForType(ImportComponent type) {
                return library.getItems().get(type.getLibraryItemType()).stream().map(item -> item.getObject()).collect(Collectors.toList());
            }
        };
    }

    static ExportImportSelectionSource fromDiagramToDiagram(TrainDiagram diagram) {
        return new ExportImportSelectionSource() {
            @Override
            public Collection<ImportComponent> getTypes() {
                return Arrays.asList(ImportComponent.values());
            }
            @Override
            public Collection<ObjectWithId> getElementsForType(ImportComponent type) {
                return type.getObjects(diagram);
            }
        };
    }

    static ExportImportSelectionSource fromDiagramSingleType(TrainDiagram diagram, ImportComponent component) {
        return new ExportImportSelectionSource() {
            @Override
            public Collection<ImportComponent> getTypes() {
                return Collections.singleton(component);
            }
            @Override
            public Collection<ObjectWithId> getElementsForType(ImportComponent type) {
                if (type != component) throw new IllegalArgumentException("Wrong type: " + type);
                return type.getObjects(diagram);
            }
        };
    }

    static ExportImportSelectionSource fromDiagramSingleTypeWithFilter(TrainDiagram diagram, ImportComponent component, Predicate<ObjectWithId> filter) {
        return new ExportImportSelectionSource() {
            @Override
            public Collection<ImportComponent> getTypes() {
                return Collections.singleton(component);
            }
            @Override
            public Collection<ObjectWithId> getElementsForType(ImportComponent type) {
                if (type != component) throw new IllegalArgumentException("Wrong type: " + type);
                return FluentIterable.from(type.getObjects(diagram)).filter(filter::test).toList();
            }
        };
    }

    static ExportImportSelectionSource empty() {
        return new ExportImportSelectionSource() {
            @Override
            public Collection<ImportComponent> getTypes() {
                return Collections.emptyList();
            }

            @Override
            public Collection<ObjectWithId> getElementsForType(ImportComponent type) {
                return Collections.emptyList();
            }
        };
    }
}
