package net.parostroj.timetable.gui;

import java.util.Objects;

import net.parostroj.timetable.utils.ResourceLoader;

/**
 * Handling of frame title.
 *
 * @author jub
 */
public class FrameTitle {

    private static final String FRAME_TITLE = "Grafikon";

    private final ApplicationModel model;

    public FrameTitle(ApplicationModel model) {
        this.model = Objects.requireNonNull(model, "Model cannot be null");
    }

    public String getTitleString(boolean changedModel) {
        String title = FRAME_TITLE;
        String version = model.getVersionInfo().getVersionWithoutBuild().toString();
        if (version != null) {
            title += " (" + version + ")";
        }
        if (model.getDiagram() != null) {
            if (model.getOpenedFile() == null) {
                title += " - " + ResourceLoader.getString("title.new");
            } else {
                title += " - " + model.getOpenedFile().getName();
            }
            if (changedModel) {
                title += " *";
            }
        }
        return title;
    }
}
