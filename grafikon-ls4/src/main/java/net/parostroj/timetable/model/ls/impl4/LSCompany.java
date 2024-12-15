package net.parostroj.timetable.model.ls.impl4;

import jakarta.xml.bind.annotation.XmlType;
import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.ls.LSException;

/**
 * Class for storing company.
 *
 * @author jub
 */
@XmlType(propOrder = {"id", "attributes"})
public class LSCompany {

    private String id;
    private LSAttributes attributes;

    public LSCompany() {
    }

    public LSCompany(Company company) {
        this.setId(company.getId());
        this.setAttributes(new LSAttributes(company.getAttributes()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LSAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(LSAttributes attributes) {
        this.attributes = attributes;
    }

    public Company createCompany(LSContext context) throws LSException {
        Company company = context.getDiagram().getPartFactory().createCompany(id);
        company.getAttributes().add(attributes.createAttributes(context));
        return company;
    }
}
