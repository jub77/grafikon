package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;

import org.beanfabrics.model.AbstractPM;
import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;

import net.parostroj.timetable.model.TrackConnector;

/**
 * Presentation model for {@link TrackConnector}.
 *
 * @author jub
 */
public class TrackConnectorPM extends AbstractPM implements IPM<TrackConnector> {

    TextPM number;

    private WeakReference<TrackConnector> reference;

    public TrackConnectorPM() {
        this.number = new TextPM();
        this.number.setMandatory(true);
        PMManager.setup(this);
    }

    public TrackConnectorPM(TrackConnector connector) {
        this();
        this.init(connector);
    }

    @Override
    public void init(TrackConnector connector) {
        this.reference = new WeakReference<>(connector);
        this.number.setText(connector.getNumber());
    }

    public void writeResult() {
        TrackConnector connector = this.reference != null ? this.reference.get() : null;
        if (connector != null) {
            // TODO write logic
        }
    }
}
