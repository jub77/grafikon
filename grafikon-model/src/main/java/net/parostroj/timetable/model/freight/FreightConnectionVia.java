package net.parostroj.timetable.model.freight;

/**
 * Freight connection containing information about transport.
 *
 * @author jub
 */
public interface FreightConnectionVia extends FreightConnection {

    Transport getTransport();
}
