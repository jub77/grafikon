package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;
import java.util.*;

import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.utils.ObjectsUtil;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;

/**
 * View model of company.
 *
 * @author jub
 */
public class CompanyPM extends AbstractPM implements IPM<Company> {

    final TextPM abbr = new TextPM();
    final TextPM name = new TextPM();
    final TextPM part = new TextPM();
    final IEnumeratedValuesPM<Locale> locale;

    final OperationPM ok = new OperationPM();

    private WeakReference<Company> companyRef;

    public CompanyPM(Collection<Locale> locales) {
        locale = new EnumeratedValuesPM<Locale>(EnumeratedValuesPM.createValueMap(
                locales, l -> l.getDisplayName()), "-");
        abbr.setEditable(false);
        PMManager.setup(this);
    }

    public void init(Company company) {
        this.companyRef = new WeakReference<Company>(company);
        this.abbr.setText(company.getAbbr());
        this.name.setText(company.getName());
        this.part.setText(company.getAttribute(Company.ATTR_PART_NAME, String.class));
        this.locale.setValue(company.getLocale());
    }

    @Operation(path = "ok")
    public boolean ok() {
        Company company = companyRef.get();
        if (company != null) {
            // write back
            company.setName(ObjectsUtil.checkAndTrim(name.getText()));
            company.setAttribute(Company.ATTR_PART_NAME, ObjectsUtil.checkAndTrim(part.getText()));
            company.setLocale(locale.getValue());
        }
        return true;
    }
}
