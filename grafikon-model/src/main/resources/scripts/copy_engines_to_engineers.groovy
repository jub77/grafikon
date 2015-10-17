import net.parostroj.timetable.model.*

def ecs = diagram.engineCycleType.cycles

for (ec in ecs) {
    def dc = new TrainsCycle(diagram.partFactory.createId(), diagram, ec.getName(), null, diagram.driverCycleType)

    for (ecItem in ec) {
        def dcItem = new TrainsCycleItem(dc, ecItem.getTrain(), null, ecItem.getFrom(), ecItem.getTo())
        dc.addItem(dcItem)
    }

    dc.type.cycles.add(dc)
}
