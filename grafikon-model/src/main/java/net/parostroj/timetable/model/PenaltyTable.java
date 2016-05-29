package net.parostroj.timetable.model;

/**
 * Penalty table.
 *
 * @author jub
 */
public class PenaltyTable {

    private PenaltyTable() {
    }

    public static int getDecPenalty(Train train, int speed) {
        PenaltyTableRow row = train.getType() != null && train.getType().getCategory() != null
                ? train.getType().getCategory().getRowForSpeed(speed) : null;
        return row != null ? row.getDeceleration() : 0;
    }

    public static int getAccPenalty(Train train, int speed) {
        PenaltyTableRow row = train.getType() != null && train.getType().getCategory() != null
                ? train.getType().getCategory().getRowForSpeed(speed) : null;
        return row != null ? row.getAcceleration() : 0;
    }
}
