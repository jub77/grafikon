package net.parostroj.timetable.gui;

import com.github.zafarkhaja.semver.Version;

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
        this.model = model;
    }

    public Version getVersionWithoutBuild() {
        Version completeVersion = model.getVersionInfo().getVersion();
        Version version = new Version.Builder().setNormalVersion(completeVersion.getNormalVersion().toString())
                .setPreReleaseVersion(completeVersion.getPreReleaseVersion().toString()).build();
        return version;
    }

    public String getTitleString(boolean changedModel) {
        String title = FRAME_TITLE;
        String version = getVersionWithoutBuild().toString();
        if (version != null) {
            title += " (" + version + ")";
        }
        if (model != null && model.getDiagram() != null) {
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
