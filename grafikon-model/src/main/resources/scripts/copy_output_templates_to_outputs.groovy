import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.model.ObjectReference
import net.parostroj.timetable.model.OutputTemplateMapping
import net.parostroj.timetable.utils.IdGenerator

def outputs = []
def outputTemplates = diagram.outputTemplates
def mapping = diagram.runtimeInfo.templateMapping
def fromMapping = false
if (outputTemplates.empty && mapping instanceof OutputTemplateMapping) {
    outputTemplates =  mapping.templates
    fromMapping = true
}
for (template in outputTemplates) {
    def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
    def name = template.name
    output.name = name == null ? LocalizedString.fromString(template.key) : name
    if (fromMapping) {
        output.templateRef = ObjectReference.create(template)
    } else {
        output.template = template
    }
    output.key = template.key
    outputs.add(output)
}
diagram.outputs.addAll(outputs)
