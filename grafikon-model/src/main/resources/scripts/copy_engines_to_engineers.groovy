import net.parostroj.timetable.model.*

def ecs = diagram.getCycles(diagram.engineCycleType)

for (ec in ecs) {
    def dc = new TrainsCycle(diagram.createId(), diagram, ec.getName(), null, diagram.driverCycleType)

    for (ecItem in ec) {
        def dcItem = new TrainsCycleItem(dc, ecItem.getTrain(), null, ecItem.getFrom(), ecItem.getTo())
        dc.addItem(dcItem)
    }

    diagram.addCycle(dc)
}
