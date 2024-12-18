package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;

/**
 * Interface for saving train diagrams.
 *
 * @author jub
 */
public interface LSFile extends LSVersions, LSConfigurable {

    String METADATA_KEY_MODEL_VERSION = "model.version";
    String METADATA = "metadata.properties";

    default TrainDiagram load(File file) throws LSException {
        return load(TrainDiagramType.NORMAL, file);
    }

    default TrainDiagram load(ZipInputStream is) throws LSException {
        return load(TrainDiagramType.NORMAL, is);
    }

    TrainDiagram load(TrainDiagramType diagramType, File file) throws LSException;

    TrainDiagram load(TrainDiagramType diagramType, ZipInputStream is) throws LSException;

    void save(TrainDiagram diagram, File file) throws LSException;

    void save(TrainDiagram diagram, ZipOutputStream os) throws LSException;
}
