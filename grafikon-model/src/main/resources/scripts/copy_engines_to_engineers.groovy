import net.parostroj.timetable.model.*

def ecs = diagram.getCycles(TrainsCycleType.ENGINE_CYCLE)

for (ec in ecs) {
    def dc = new TrainsCycle(diagram.createId(), ec.getName(), null, diagram.getCyclesType(TrainsCycleType.DRIVER_CYCLE))

    for (ecItem in ec) {
        def dcItem = new TrainsCycleItem(dc, ecItem.getTrain(), null, ecItem.getFrom(), ecItem.getTo(), null)
        dc.addItem(dcItem)
    }

    diagram.addCycle(dc)
}
