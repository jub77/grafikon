import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.utils.IdGenerator
import net.parostroj.timetable.gui.dialogs.ElementSelectionDialog
import net.parostroj.timetable.model.*

def templates = diagram.outputTemplates

def select = new ElementSelectionDialog(parent, true)
select.setLocationRelativeTo(parent)
templates = select.selectElements(templates)

def outputs = []
for (template in templates) {
    def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
    def name = template.name
    output.name = name == null ? LocalizedString.fromString(template.key) : LocalizedString.fromString(name.translate())
    output.template = template
    output.key = template.key
    outputs.add(output)
}
diagram.outputs.addAll(outputs)
