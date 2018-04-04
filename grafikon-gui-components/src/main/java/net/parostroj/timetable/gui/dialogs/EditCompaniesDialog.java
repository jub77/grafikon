package net.parostroj.timetable.gui.dialogs;

import java.awt.Window;
import java.util.Collection;
import java.util.Locale;

import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramPartFactory;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Dialog for editing companies.
 *
 * @author jub
 */
public class EditCompaniesDialog extends EditItemsDialog<Company, TrainDiagram> {

    private static final long serialVersionUID = 1L;

	private Collection<Locale> locales;

    public EditCompaniesDialog(Window parent, boolean modal, boolean move, boolean edit, boolean newByName,
            boolean copy, boolean multiple) {
        super(parent, modal, move, edit, newByName, copy, multiple);
    }

    public static EditCompaniesDialog newInstance(Window parent, boolean modal, Collection<Locale> locales) {
        EditCompaniesDialog dialog = newBuilder(EditCompaniesDialog.class).setEdit(true).setNewByName(true).build(parent, modal);
        dialog.setLocales(locales);
        return dialog;
    }

    private void setLocales(Collection<Locale> locales) {
        this.locales = locales;
    }

    @Override
    protected Collection<Company> getList() {
        return element.getCompanies();
    }

    @Override
    protected void add(Company item, int index) {
        // ignore index (no move allowed)
        element.getCompanies().add(item);
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
        TrainDiagramPartFactory factory = element.getPartFactory();
        Company newCompany = factory.createCompany(factory.createId());
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
