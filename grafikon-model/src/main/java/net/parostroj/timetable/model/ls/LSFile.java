package net.parostroj.timetable.model.ls;

import net.parostroj.timetable.model.TrainDiagram;

/**
 * Interface for saving train diagrams.
 *
 * @author jub
 */
public interface LSFile extends LSVersions, LSConfigurable, LS<TrainDiagram> {

    String METADATA_KEY_MODEL_VERSION = "model.version";
    String METADATA = "metadata.properties";
}
