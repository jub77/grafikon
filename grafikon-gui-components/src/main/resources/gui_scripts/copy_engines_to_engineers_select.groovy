import net.parostroj.timetable.gui.dialogs.ElementSelectionDialog
import net.parostroj.timetable.model.*

def ecs = diagram.getCycles(diagram.engineCycleType)

def select = new ElementSelectionDialog(parent, true)
select.setLocationRelativeTo(parent)
ecs = select.selectElements(ecs)

for (ec in ecs) {
    def dc = new TrainsCycle(diagram.createId(), diagram, ec.getName(), null, diagram.driverCycleType)

    for (ecItem in ec) {
        def dcItem = new TrainsCycleItem(dc, ecItem.getTrain(), null, ecItem.getFrom(), ecItem.getTo())
        dc.addItem(dcItem)
    }

    diagram.addCycle(dc)
}
