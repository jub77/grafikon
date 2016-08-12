import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.utils.IdGenerator
import net.parostroj.timetable.utils.ObjectsUtil

for (template in diagram.outputTemplates) {
    def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
    def description = template.description
    def translatedDescription = description != null ? description.translate() : template.name
    String firstLine = ObjectsUtil.getFirstLine(translatedDescription);
    output.name = LocalizedString.fromString(firstLine)
    output.template = template
    diagram.outputs.add(output)
}
