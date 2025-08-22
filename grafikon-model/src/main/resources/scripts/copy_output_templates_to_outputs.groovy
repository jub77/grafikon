import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.model.ObjectReference
import net.parostroj.timetable.utils.IdGenerator

def outputs = []
def storage = diagram.runtimeInfo.templateStorage
if (storage != null) {
    def outputTemplates = binding.hasVariable("category")
            ? storage.templatesByCategory[binding.getVariable("category")]
            : binding.hasVariable("templates")
                    ? binding.getVariable("templates")
                    : storage.templates
    for (template in outputTemplates) {
        def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
        def name = template.name
        output.name = name == null ? LocalizedString.fromString(template.key) : name
        output.templateRef = ObjectReference.create(template)
        output.key = template.key
        outputs.add(output)
    }
    diagram.outputs.addAll(outputs)
}
