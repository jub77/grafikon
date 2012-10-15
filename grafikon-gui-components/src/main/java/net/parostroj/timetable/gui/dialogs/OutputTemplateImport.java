package net.parostroj.timetable.gui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.*;

public class OutputTemplateImport extends Import {

    private static final Logger LOG = LoggerFactory.getLogger(OutputTemplateImport.class);

    public OutputTemplateImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
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
            String message = "Output template already exists: " + checkedOutputTemplate;
            this.addError(importedOutputTemplate, message);
            LOG.trace(message);
            return null;
        }

        // create new output template
        OutputTemplate outputTemplate = new OutputTemplate(this.getId(importedOutputTemplate), this.getDiagram());
        outputTemplate.setName(importedOutputTemplate.getName());
        outputTemplate.setAttributes(new Attributes(importedOutputTemplate.getAttributes()));
        outputTemplate.setTemplate(importedOutputTemplate.getTemplate());

        // add to diagram
        this.getDiagram().addOutputTemplate(outputTemplate);
        this.addImportedObject(outputTemplate);
        LOG.trace("Successfully imported output template: " + outputTemplate);
        return outputTemplate;
    }
}
