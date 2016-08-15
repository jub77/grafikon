import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.utils.IdGenerator

for (template in diagram.outputTemplates) {
    def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
    def name = template.name
    output.name = name == null ? LocalizedString.fromString(template.key) : name 
    output.template = template
    diagram.outputs.add(output)
}
