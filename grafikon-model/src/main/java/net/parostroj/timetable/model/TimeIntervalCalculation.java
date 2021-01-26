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
    private final PenaltySolver penaltySolver;

    protected TimeIntervalCalculation(Train train, TimeInterval interval) {
        this.list = train.getIntervalList();
        this.interval = interval;
        this.penaltySolver = new PenaltySolver() {
            @Override
            public int getDecelerationPenalty(int speed) {
                return PenaltyTable.getDecPenalty(train, speed);
            }

            @Override
            public int getAccelerationPenalty(int speed) {
                return PenaltyTable.getAccPenalty(train, speed);
            }
        };
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
        Train train = interval.getTrain();
        TrainDiagram diagram = train.getDiagram();
        Script runningTimeScript = diagram.getTrainsData().getRunningTimeScript();
        return runningTimeScript != null
                ? this.computeRunningTimeScript(train, diagram, runningTimeScript, usedSpeed)
                : this.computeRunningTimeDefault(train, diagram, usedSpeed);
    }

    private int computeRunningTimeDefault(Train train, TrainDiagram diagram, int usedSpeed) {
        // default not straight speed
        int dnss = 40;

        TimeInterval ini = interval.getPreviousTrainInterval();
        TimeInterval ino = interval.getNextTrainInterval();
        TimeInterval ili = ini.getPreviousTrainInterval() != null ? ini.getPreviousTrainInterval() : interval;
        TimeInterval ilo = ino.getNextTrainInterval() != null ? ino.getNextTrainInterval() : interval;

        int s0 = ini.getCalculation().computeNodeSpeed(ili, false, dnss);
        int s4 = ino.getCalculation().computeNodeSpeed(ilo, false, dnss);
        int s2 = interval.getCalculation().computeLineSpeed();
        int s1 = ini.getCalculation().computeNodeSpeed(interval, false, dnss);
        int s3 = ino.getCalculation().computeNodeSpeed(interval, true, dnss);
        s4 = Math.min(s3, s4);
        s1 = Math.min(s0, s1);
        if (ini.isStop()) s0 = 0;
        if (ino.isStop()) s4 = 0;

        int li = ini.getOwnerAsNode().getLength() != null ? ini.getOwnerAsNode().getLength() : 0;
        int lo = ino.getOwnerAsNode().getLength() != null ? ino.getOwnerAsNode().getLength() : 0;
        int l = interval.getOwnerAsLine().getLength() - li / 2 + lo / 2 - li - lo;

        int time = 0;
        time += computePart(train, s1, s0, s2, li, diagram, interval);
        time += computePart(train, s2, s1, s3, l, diagram, interval);
        time += computePart(train, s3, s2, s4, lo, diagram, interval);

        time = diagram.getTimeConverter().round(time);
        return time;
    }

    // ------------ functions -------------
    int computePart(Train train, int s, int fs, int ts, int l, TrainDiagram diagram, TimeInterval interval) {
        int time = (int) Math.floor((3.6d * l * diagram.getScale().getRatio() * diagram.getTimeScale()) / (s * 1000));
        int penalty = 0;
        if (ts < s) {
            int penalty1 = train.getDecPenalty(s);
            int penalty2 = train.getDecPenalty(ts);
            penalty = penalty1 - penalty2;
        }
        if (fs < s) {
            int penalty1 = train.getAccPenalty(fs);
            int penalty2 = train.getAccPenalty(s);
            penalty = penalty + penalty2 - penalty1;
        }
        int adjPenalty = (int) (penalty * 0.18d * diagram.getTimeScale());
        time += adjPenalty;
        time += interval.getAddedTime();

        return time;
    }

    private int computeRunningTimeScript(Train train, TrainDiagram diagram, Script runningTimeScript, int usedSpeed) {
        Map<String, Object> binding = new HashMap<>();
        binding.put("speed", usedSpeed);
        binding.put("fromSpeed", this.computeFromSpeed());
        binding.put("toSpeed", this.computeToSpeed());
        binding.put("timeScale", diagram.getTimeScale());
        binding.put("scale", diagram.getScale().getRatio());
        binding.put("length", interval.getOwnerAsLine().getLength());
        binding.put("addedTime", this.interval.getAddedTime());
        binding.put("penaltySolver", penaltySolver);
        binding.put("train", train);
        binding.put("converter", train.getDiagram().getTimeConverter());
        binding.put("interval", interval);
        binding.put("diagram", train.getDiagram());
        binding.put("log", log);

        Object result = runningTimeScript.evaluate(binding);
        if (!(result instanceof Number)) {
            throw new IllegalStateException("Unexpected result: " + result);
        }

        return ((Number) result).intValue();
    }

    public interface PenaltySolver {
        int getDecelerationPenalty(int speed);
        int getAccelerationPenalty(int speed);
    }
}
