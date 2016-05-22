package net.parostroj.timetable.model.ls;

import java.util.List;

public interface LSVersions {

    List<ModelVersion> getLoadVersions();

    ModelVersion getSaveVersion();
}
