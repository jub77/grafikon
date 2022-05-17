package net.parostroj.timetable.model;

import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Object of the diagram with version.
 *
 * @author jub
 */
public interface ObjectWithVersion {

    ModelVersion getVersion();
}
