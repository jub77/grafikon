import net.parostroj.timetable.model.imports.ImportMatch
import net.parostroj.timetable.model.imports.OutputTemplateImport

def outputs = []
def storage = diagram.runtimeInfo.templateStorage
if (storage != null) {
    def outputTemplates = storage.templates
    def otImport = new OutputTemplateImport(diagram, ImportMatch.ID, true)
    for (template in outputTemplates) {
        def imported = otImport.importObject(template)
        imported.removeAttribute("source")
    }
}
