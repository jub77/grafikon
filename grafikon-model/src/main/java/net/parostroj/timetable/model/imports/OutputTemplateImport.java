package net.parostroj.timetable.model.imports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;

public class OutputTemplateImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(OutputTemplateImport.class);

    public OutputTemplateImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof OutputTemplate))
            return null;
        OutputTemplate importedOutputTemplate = (OutputTemplate) o;

        // check existence
        OutputTemplate checkedOutputTemplate = this.getOutputTemplate(importedOutputTemplate);
        if (checkedOutputTemplate != null) {
            if (overwrite) {
                this.getDiagram().getOutputTemplates().remove(checkedOutputTemplate);
            } else {
                String message = "output template already exists";
                this.addError(importedOutputTemplate, message);
                log.debug("{}: {}", message, checkedOutputTemplate);
                return null;
            }
        }

        // create new output template
        OutputTemplate outputTemplate = this.getDiagram().getPartFactory()
                .createOutputTemplate(this.getId(importedOutputTemplate));
        outputTemplate.setName(importedOutputTemplate.getName());
        outputTemplate.getAttributes().add(new Attributes(importedOutputTemplate.getAttributes()));
        outputTemplate.setTemplate(importedOutputTemplate.getTemplate());
        outputTemplate.setScript(importedOutputTemplate.getScript());

        // add to diagram
        this.getDiagram().getOutputTemplates().add(outputTemplate);
        this.addImportedObject(outputTemplate);
        log.trace("Successfully imported output template: " + outputTemplate);
        return outputTemplate;
    }
}
