import net.parostroj.timetable.model.LocalizedString
import net.parostroj.timetable.utils.IdGenerator

def types = [
    'pdf.groovy':1,
    'draw':2,
    'groovy':3,
    'xml':4
]
def outputs = []
for (template in diagram.outputTemplates) {
    def output = diagram.partFactory.createOutput(IdGenerator.instance.id)
    def name = template.name
    output.name = name == null ? LocalizedString.fromString(template.key) : name 
    output.template = template
    output.key = template.key
    outputs.add(output)
}
def collator = java.text.Collator.instance
outputs.sort{a,b -> 
    def typeCmp = types[a.template.output] <=> types[b.template.output]
    if (typeCmp != 0) {
        return typeCmp
    } else {
        return collator.compare(a.name.translate(), b.name.translate())
    }
} 
diagram.outputs.addAll(outputs)
