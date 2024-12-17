package net.parostroj.timetable.model.ls;

import java.io.File;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.TrainDiagram;

/**
 * Interface for saving train diagrams.
 *
 * @author jub
 */
public interface LSFile extends LSVersions, LSConfigurable {

    String METADATA_KEY_MODEL_VERSION = "model.version";
    String METADATA = "metadata.properties";

    TrainDiagram load(File file) throws LSException;

    TrainDiagram load(ZipInputStream is) throws LSException;

    void save(TrainDiagram diagram, File file) throws LSException;

    void save(TrainDiagram diagram, ZipOutputStream os) throws LSException;
}
