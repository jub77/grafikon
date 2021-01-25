package net.parostroj.timetable.model;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.actions.TrainsHelper;

/**
 * @author jub
 */
public class TimeIntervalCalculation {

    private static final Logger log = LoggerFactory.getLogger(TimeIntervalCalculation.class);

    private static final int FALLBACK_SPEED = 10;

    private final List<TimeInterval> list;
    private final TimeInterval interval;

    protected TimeIntervalCalculation(Train train, TimeInterval interval) {
        this.list = train.getIntervalList();
        this.interval = interval;
    }

    public Integer computeLineSpeed() {
        if (!interval.isLineOwner()) {
            throw new IllegalStateException("Not allowed for node interval.");
        }
        Line line = interval.getOwnerAsLine();
        return this.computeLineSpeed(interval, min(interval.getSpeedLimit(), line.getTopSpeed()));
    }

    public Integer computeNodeSpeed(TimeInterval lineInterval, boolean from, Integer defaultNotStraightSpeed) {
        boolean straight = from ? interval.isFromStraight() : interval.isToStraight();
        Node node = interval.getOwnerAsNode();
        Line line = lineInterval.getOwnerAsLine();
        Integer speed = straight ? node.getSpeed() : node.getNotStraightSpeed();
        if (speed == null) {
            speed = straight ? line.getTopSpeed() : defaultNotStraightSpeed;
        }
        speed = min(lineInterval.getSpeedLimit(), speed);
        speed = this.computeLineSpeed(lineInterval, speed);
        return speed;
    }

    public Integer min(Integer... s) {
        Optional<Integer> min = Arrays.stream(s).filter(Objects::nonNull).min(Comparator.naturalOrder());
        return min.orElse(null);
    }

    private int computeLineSpeed(TimeInterval lineInterval, Integer speedLimit) {
        if (speedLimit != null && speedLimit < 1) {
            throw new IllegalArgumentException("Speed has to be greater than 0.");
        }
        Train train = lineInterval.getTrain();
        // apply speed limit
        Integer speed = min(train.getTopSpeed(), speedLimit);

        // adjust (engine class influence)
        List<EngineClass> engineClasses = TrainsHelper.getEngineClasses(lineInterval);
        for (EngineClass engineClass : engineClasses) {
            WeightTableRow row = engineClass.getWeigthTableRowWithMaxSpeed();
            if (row != null) {
                speed = min(speed, row.getSpeed());
            }
        }

        // if there is a weight limit, engines and line class defined ...
        Integer weightLimit = train.getAttributes().get(Train.ATTR_WEIGHT_LIMIT, Integer.class);
        LineClass lineClass = lineInterval.getLineClass();
        if (!engineClasses.isEmpty() && weightLimit != null && lineClass != null) {
            speed = min(speed, TrainsHelper.getSpeedForWeight(engineClasses, lineClass, weightLimit));
        }

        if (speed == null) {
            // fallback solution to default speed and log a warning
            speed = FALLBACK_SPEED;
            log.warn("Couldn't compute speed for interval {}", lineInterval);
        }

        return speed;
    }

    private int computeFromSpeed() {
        int i = this.list.indexOf(interval);
        // previous node is stop - first node or node has not null time
        if ((i - 1) == 0 || list.get(i - 1).getLength() != 0) {
            return 0;
        } else {
            // check speed of previous line
            return list.get(i - 2).getCalculation().computeLineSpeed();
        }
    }

    private int computeToSpeed() {
        int i = this.list.indexOf(interval);
        // next node is stop - last node or node has not null time
        if ((i + 1) == (list.size() - 1) || list.get(i + 1).getLength() != 0) {
            return 0;
        } else {
            // check speed of previous line
            return list.get(i + 2).getCalculation().computeLineSpeed();
        }
    }

    /**
     * computes running time.
     *
     * @param usedSpeed speed on line
     * @return pair running time and speed
     */
    public int computeRunningTime(int usedSpeed) {
        final Train train = interval.getTrain();
        final TrainDiagram diagram = train.getDiagram();
        PenaltySolver ps = new PenaltySolver() {

            @Override
            public int getDecelerationPenalty(int speed) {
                return PenaltyTable.getDecPenalty(train, speed);
            }

            @Override
            public int getAccelerationPenalty(int speed) {
                return PenaltyTable.getAccPenalty(train, speed);
            }
        };

        Map<String, Object> binding = new HashMap<>();
        binding.put("speed", usedSpeed);
        binding.put("fromSpeed", this.computeFromSpeed());
        binding.put("toSpeed", this.computeToSpeed());
        binding.put("timeScale", diagram.getTimeScale());
        binding.put("scale", diagram.getScale().getRatio());
        binding.put("length", interval.getOwnerAsLine().getLength());
        binding.put("addedTime", this.interval.getAddedTime());
        binding.put("penaltySolver", ps);
        binding.put("train", train);
        binding.put("converter", train.getDiagram().getTimeConverter());
        binding.put("interval", interval);
        binding.put("diagram", train.getDiagram());
        binding.put("log", log);

        Object result = diagram.getTrainsData().getRunningTimeScript().evaluate(binding);
        if (!(result instanceof Number)) {
            throw new IllegalStateException("Unexpected result: " + result);
        }

        return ((Number)result).intValue();
    }

    public interface PenaltySolver {
        int getDecelerationPenalty(int speed);
        int getAccelerationPenalty(int speed);
    }
}
