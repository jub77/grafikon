package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.Company;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports companies.
 *
 * @author jub
 */
public class CompanyImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(CompanyImport.class);

    public CompanyImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof Company))
            return null;
        Company importedCompany = (Company)o;

        // check existence
        Company checkedCompany = this.getCompany(importedCompany);
        if (checkedCompany != null) {
            String message = "company already exists";
            this.addError(importedCompany, message);
            log.debug("{}: {}", message, checkedCompany);
            return null;
        }

        // create new company
        Company company = getDiagram().getPartFactory().createCompany(this.getId(importedCompany));
        company.getAttributes().add(this.importAttributes(importedCompany.getAttributes()));

        // add to diagram
        this.getDiagram().getCompanies().add(company);
        this.addImportedObject(company);
        log.trace("Successfully imported company: " + company);
        return company;
    }
}
