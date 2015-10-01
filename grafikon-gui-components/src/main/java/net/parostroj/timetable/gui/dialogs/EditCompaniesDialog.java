package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing companies.
 *
 * @author jub
 */
public class EditCompaniesDialog extends EditItemsDialog<Company, TrainDiagram> {

    private final Collection<Locale> locales;

    public EditCompaniesDialog(Window parent, boolean modal, Collection<Locale> locales) {
        super(parent, modal, false, true, true);
        this.locales = locales;
    }

    @Override
    protected Collection<Company> getList() {
        return element.getCompanies().toList();
    }

    @Override
    protected void add(Company item, int index) {
        element.getCompanies().add(item, index);
    }

    @Override
    protected void remove(Company item) {
        element.getCompanies().remove(item);
    }

    @Override
    protected void move(Company item, int oldIndex, int newIndex) {
        throw new IllegalStateException("Move not allowed");
    }

    @Override
    protected boolean deleteAllowed(Company item) {
        return true;
    }

    @Override
    protected Company createNew(String name) {
        Company newCompany = element.createCompany(element.createId());
        newCompany.setAbbr(name);
        return newCompany;
    }

    @Override
    protected void edit(Company company) {
        EditCompanyDialog dialog = new EditCompanyDialog(this, true, locales);
        dialog.setLocationRelativeTo(this);
        String companyAbbr = company.getAbbr();
        dialog.showDialog(company);
        dialog.dispose();
        if (!ObjectsUtil.compareWithNull(companyAbbr, company.getAbbr())) {
            this.refreshAll();
        }
    }
}
