package net.parostroj.timetable.gui.pm;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.OperationPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.support.Operation;
import org.beanfabrics.support.Validation;
import org.beanfabrics.validation.ValidationState;

import com.google.common.collect.FluentIterable;

import net.parostroj.timetable.model.Line;
import net.parostroj.timetable.model.LineTrack;

/**
 * Presentation model for line between nodes.
 *
 * @author jub
 */
public class LinePM extends AbstractPM implements IPM<Line> {

    ItemListPM<LineTrackPM> tracks;

    OperationPM ok = new OperationPM();

    private Line reference;

    public LinePM() {
        tracks = new ItemListPM<>(() -> {
            LineTrackPM pm = new LineTrackPM();
            pm.getNumber().setText("1");
            return pm;
        });
        tracks.delete.getValidator().add(() -> {
            if (tracks.getSelection().size() == tracks.size()) {
                return ValidationState.create("At least one track");
            } else {
                Collection<LineTrackPM> trackPMs = tracks.getSelection().toCollection();
                for (LineTrackPM trackPM : trackPMs) {
                    if (trackPM.getReference() != null
                            && !trackPM.getReference().getTimeIntervalList().isEmpty()) {
                        return ValidationState.create("Not empty track");
                    }
                }
            }
            return null;
        });
        PMManager.setup(this);
    }

    public LinePM(Line line) {
        this();
        init(line);
    }

    @Validation(path = { "ok" })
    public boolean canWrite() {
        return isValid();
    }

    @Operation(path = "ok")
    public boolean ok() {
        writeResult();
        return true;
    }

    @Override
    public void init(Line line) {
        this.reference = line;
        tracks.clear();
        for (LineTrack track : line.getTracks()) {
            LineTrackPM lt = new LineTrackPM();
            lt.init(track);
            tracks.add(lt);
        }
    }

    public void writeResult() {
        if (reference != null) {
            // remove
            Set<LineTrack> keptTracks = FluentIterable.from(tracks)
                    .transform(LineTrackPM::getReference).filter(Objects::nonNull).toSet();
            Set<LineTrack> toBeDeletedTracks = new HashSet<>(reference.getTracks());
            toBeDeletedTracks.removeAll(keptTracks);
            reference.getTracks().removeAll(toBeDeletedTracks);
            // update
            int position = 0;
            for (LineTrackPM track : tracks) {
                track.writeResult(reference, position);
                position++;
            }
        }
    }
}
