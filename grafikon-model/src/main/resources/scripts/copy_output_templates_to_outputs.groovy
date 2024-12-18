import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.model.ObjectReference
import net.parostroj.timetable.model.OutputTemplateSimpleStorage
import net.parostroj.timetable.utils.IdGenerator

def outputs = []
def outputTemplates = diagram.outputTemplates
def storage = diagram.runtimeInfo.templateStorage
def fromStorage = false
if (outputTemplates.empty && storage instanceof OutputTemplateSimpleStorage) {
    outputTemplates =  storage.templates
    fromStorage = true
}
for (template in outputTemplates) {
    def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
    def name = template.name
    output.name = name == null ? LocalizedString.fromString(template.key) : name
    if (fromStorage) {
        output.templateRef = ObjectReference.create(template)
    } else {
        output.template = template
    }
    output.key = template.key
    outputs.add(output)
}
diagram.outputs.addAll(outputs)
