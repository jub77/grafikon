import groovy.transform.CompileStatic
import net.parostroj.timetable.model.TimeInterval
import net.parostroj.timetable.model.TrainDiagram

//  |  li   |  li   |            l                |  lo   |  lo   |
//  /-------\                                     /-------\
//  ---------------------------------------------------------------
//  \-------/                                     \-------/
//  |  s0   |  s1   |            s2               |  s3   |  s4   |

return compute(diagram, interval)

// ------------ computation -------------
@CompileStatic
static int compute(TrainDiagram diagram, TimeInterval interval) {
    // default not straight speed
    int dnss = 40

    TimeInterval ini = interval.previousTrainInterval
    TimeInterval ino = interval.nextTrainInterval
    TimeInterval ili = ini.previousTrainInterval != null ? ini.previousTrainInterval : interval
    TimeInterval ilo = ino.nextTrainInterval != null ? ino.nextTrainInterval : interval

    int s0 = ini.calculation.computeNodeSpeed(ili, false, dnss)
    int s4 = ino.calculation.computeNodeSpeed(ilo, false, dnss)
    int s2 = interval.calculation.computeLineSpeed()
    int s1 = ini.calculation.computeNodeSpeed(interval, false, dnss)
    int s3 = ino.calculation.computeNodeSpeed(interval, true, dnss)
    s4 = s3 < s4 ? s3 : s4
    s1 = s0 < s1 ? s0 : s1
    if (ini.stop) s0 = 0
    if (ino.stop) s4 = 0

    int li = ini.ownerAsNode.length ?: 0
    int lo = ino.ownerAsNode.length ?: 0
    int l = interval.ownerAsLine.length - li.intdiv(2) + lo.intdiv(2) - li - lo

    int time = 0
    time += compute(s1, s0, s2, li, diagram, interval)
    time += compute(s2, s1, s3, l, diagram, interval)
    time += compute(s3, s2, s4, lo, diagram, interval)

    time = diagram.timeConverter.round(time)
    return time
}

// ------------ functions -------------
@CompileStatic
static int compute(int s, int fs, int ts, int l, TrainDiagram diagram, TimeInterval interval) {
    int time = (int) ((3.6d * l * diagram.scale.ratio * diagram.timeScale) / (s * 1000)).trunc()
    int penalty = 0
    if (ts < s) {
        int penalty1 = interval.train.getDecPenalty(s)
        int penalty2 = interval.train.getDecPenalty(ts)
        penalty = penalty1 - penalty2
    }
    if (fs < s) {
        int penalty1 = interval.train.getAccPenalty(fs)
        int penalty2 = interval.train.getAccPenalty(s)
        penalty = penalty + penalty2 - penalty1
    }
    int adjPenalty = (int) (penalty * 0.18d * diagram.timeScale).round()
    time += adjPenalty
    time += interval.addedTime

    return time
}
