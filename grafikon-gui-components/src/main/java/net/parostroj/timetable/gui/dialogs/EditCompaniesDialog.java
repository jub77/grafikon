package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;

import net.parostroj.timetable.model.Company;

/**
 * Dialog for editing companies.
 *
 * @author jub
 */
public class EditCompaniesDialog extends EditItemsDialog<Company> {

    public EditCompaniesDialog(Window parent, boolean modal) {
        super(parent, modal, false, true);
    }

    @Override
    protected Collection<Company> getList() {
        return diagram.getCompanies().get();
    }

    @Override
    protected void add(Company item, int index) {
        diagram.getCompanies().add(item, index);
    }

    @Override
    protected void remove(Company item) {
        diagram.getCompanies().remove(item);
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
        Company newCompany = diagram.createCompany(diagram.createId());
        newCompany.setAbbr(name);
        return newCompany;
    }

    @Override
    protected void edit(Company company) {
        EditCompanyDialog dialog = new EditCompanyDialog(this, true);
        dialog.setLocationRelativeTo(this);
        dialog.showDialog(company);
        dialog.dispose();
    }
}
