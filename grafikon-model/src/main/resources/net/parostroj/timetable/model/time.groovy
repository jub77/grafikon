import net.parostroj.timetable.model.TimeInterval
import net.parostroj.timetable.model.TrainDiagram

//  |  li   |  li   |            l                |  lo   |  lo   |
//  /-------\                                     /-------\
//  ---------------------------------------------------------------
//  \-------/                                     \-------/
//  |  s0   |  s1   |            s2               |  s3   |  s4   |

return compute(diagram, interval)

// ------------ computation -------------
def compute(TrainDiagram diagram, TimeInterval interval) {
    log.trace "## ${interval.train} > ${interval}"

    // default not straight speed
    def dnss = 40

    def ini = interval.previousTrainInterval
    def ino = interval.nextTrainInterval
    def ili = ini.previousTrainInterval != null ? ini.previousTrainInterval : interval
    def ilo = ino.nextTrainInterval != null ? ino.nextTrainInterval : interval

    def s0 = ini.calculation.computeNodeSpeed(ili, false, dnss)
    def s4 = ino.calculation.computeNodeSpeed(ilo, false, dnss)
    def s2 = interval.calculation.computeLineSpeed()
    def s1 = ini.calculation.computeNodeSpeed(interval, false, dnss)
    def s3 = ino.calculation.computeNodeSpeed(interval, true, dnss)
    s4 = min(s3, s4)
    s1 = min(s0, s1)
    if (ini.stop) s0 = 0
    if (ino.stop) s4 = 0

    def li = select(ini.ownerAsNode.length, 0)
    def lo = select(ino.ownerAsNode.length, 0)
    def l = interval.ownerAsLine.length - li / 2 + lo / 2 - li - lo

    def time = 0
    time += compute(s1, s0, s2, li, diagram, interval, "F")
    time += compute(s2, s1, s3, l, diagram, interval, "N")
    time += compute(s3, s2, s4, lo, diagram, interval, "T")
    time += interval.addedTime

    log.trace "Total        : ${time}"
    time = diagram.timeConverter.round(time)
    log.trace "Total (round): ${time}"
    return time
}

// ------------ functions -------------
def min(a, b) {
    Math.min(a, b)
}

def select(value1, value2) {
    return value1 == null ? value2 : value1
}

def compute(s, fs, ts, l, TrainDiagram diagram, TimeInterval interval, prefix) {
    int time = (int) Math.floor((((double) l) * diagram.scale.ratio * diagram.timeScale * 3.6) / (s * 1000))
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
    def adjPenalty = (int)Math.round(penalty * 0.18d * diagram.timeScale)
    time += adjPenalty

    log.trace "-- ${prefix} --------------"
    log.trace "length: ${l}"
    log.trace "speed : ${fs} -> ${s} -> ${ts}"
    log.trace "pen.  : ${penalty} -> ${adjPenalty}"
    log.trace "Time  : ${time}"
    return time
}
