package net.parostroj.timetable.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.parostroj.timetable.actions.TrainsHelper;

/**
 * @author jub
 */
class TimeIntervalCalculation {

    private final List<TimeInterval> list;
    private final TimeInterval interval;

    protected TimeIntervalCalculation(Train train, TimeInterval interval) {
        this.list = train.getIntervalList();
        this.interval = interval;
    }

    public Integer computeSpeed() {
        return interval.isLineOwner() ? this.computeSpeed(interval.getOwnerAsLine(), interval.getTrain(), interval.getSpeedLimit()) : null;
    }

    private int computeSpeed(Line line, Train train, Integer prefferedSpeed) {
        int speed;
        if (prefferedSpeed != null && prefferedSpeed < 1)
            throw new IllegalArgumentException("Speed has to be greater than 0.");
        speed = train.getTopSpeed();
        if (prefferedSpeed != null) {
            speed = Math.min(prefferedSpeed, train.getTopSpeed());
        }

        // apply track speed limit
        if (line.getTopSpeed() != null) {
            speed = Math.min(speed, line.getTopSpeed());
        }

        // adjust (engine class influence)
        List<EngineClass> engineClasses = null;
        if (interval != null) {
            engineClasses = TrainsHelper.getEngineClasses(interval);
            for (EngineClass engineClass : engineClasses) {
                WeightTableRow row = engineClass.getWeigthTableRowWithMaxSpeed();
                if (row != null) {
                    speed = Math.min(speed, row.getSpeed());
                }
            }
        }

        // if there is a weight limit, engines and line class defined ...
        Integer weightLimit = train.getAttributes().get(Train.ATTR_WEIGHT_LIMIT, Integer.class);
        LineClass lineClass = line.getLineClass(interval.getDirection());
        if (!engineClasses.isEmpty() && weightLimit != null && lineClass != null) {
            Integer limitedSpeed = TrainsHelper.getSpeedForWeight(engineClasses, lineClass, weightLimit);
            if (limitedSpeed != null) {
                speed = Math.min(speed, limitedSpeed);
            }
        }

        return speed;
    }

    public int computeFromSpeed() {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Cannot find speed for node.");
        int i = list.indexOf(interval);
        if (i == -1)
            throw new IllegalArgumentException("Interval is not part of the list.");
        return this.computeFromSpeed(i);
    }

    public int computeToSpeed() {
        if (!interval.isLineOwner())
            throw new IllegalArgumentException("Cannot find speed for node.");
        int i = list.indexOf(interval);
        if (i == -1)
            throw new IllegalArgumentException("Interval is not part of the list.");
        return this.computeToSpeed(i);
    }

    public int computeFromSpeed(int i) {
        // previous node is stop - first node or node has not null time
        if ((i - 1) == 0 || list.get(i - 1).getLength() != 0) {
            return 0;
        } else {
            // check speed of previous line
            return list.get(i - 2).getCalculation().computeSpeed();
        }
    }

    public int computeToSpeed(int i) {
        // next node is stop - last node or node has not null time
        if ((i + 1) == (list.size() - 1) || list.get(i + 1).getLength() != 0) {
            return 0;
        } else {
            // check speed of previous line
            return list.get(i + 2).getCalculation().computeSpeed();
        }
    }

    /**
     * computes running time for this track.
     *
     * @param train train
     * @param speed speed
     * @param diagram train diagram
     * @param fromSpeed from speed
     * @param toSpeed to speed
     * @return pair running time and speed
     */
    public int computeRunningTime(int speed, int fromSpeed, int toSpeed, int addedTime) {
        final Train train = interval.getTrain();
        TrainDiagram diagram = train.getTrainDiagram();
        Scale scale = diagram.getAttribute(TrainDiagram.ATTR_SCALE, Scale.class);
        double timeScale = diagram.getAttribute(TrainDiagram.ATTR_TIME_SCALE, Double.class);
        final PenaltyTable penaltyTable = diagram.getPenaltyTable();
        PenaltySolver ps = new PenaltySolver() {

            @Override
            public int getDecelerationPenalty(int speed) {
                PenaltyTableRow row = train.getType() != null ? penaltyTable.getRowForSpeedAndCategory(train.getType().getCategory(), speed) : null;
                return row != null ? row.getDeceleration() : 0;
            }

            @Override
            public int getAccelerationPenalty(int speed) {
                PenaltyTableRow row = train.getType() != null ? penaltyTable.getRowForSpeedAndCategory(train.getType().getCategory(), speed) : null;
                return row != null ? row.getAcceleration() : 0;
            }
        };

        TimeConverter converter = train.getTrainDiagram().getTimeConverter();
        Map<String, Object> binding = new HashMap<String, Object>();
        binding.put("speed", speed);
        binding.put("fromSpeed", fromSpeed);
        binding.put("toSpeed", toSpeed);
        binding.put("timeScale", timeScale);
        binding.put("scale", scale.getRatio());
        binding.put("length", interval.getOwnerAsLine().getLength());
        binding.put("addedTime", addedTime);
        binding.put("penaltySolver", ps);
        binding.put("train", train);
        binding.put("converter", converter);
        binding.put("diagram", train.getTrainDiagram());

        Object result = diagram.getTrainsData().getRunningTimeScript().evaluate(binding);
        if (!(result instanceof Number))
            throw new IllegalStateException("Unexpected result: " + result);

        return ((Number)result).intValue();
    }

    public interface PenaltySolver {
        int getDecelerationPenalty(int speed);
        int getAccelerationPenalty(int speed);
    }
}
