package net.parostroj.timetable.gui.pm;

import java.lang.ref.WeakReference;

import org.beanfabrics.model.*;
import org.beanfabrics.support.Operation;

import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * View model of company.
 *
 * @author jub
 */
public class CompanyPM extends AbstractPM {

    final TextPM abbr = new TextPM();
    final TextPM name = new TextPM();

    final OperationPM ok = new OperationPM();

    private WeakReference<Company> companyRef;

    public CompanyPM() {
        PMManager.setup(this);
        abbr.setEditable(false);
    }

    public void init(Company company) {
        this.companyRef = new WeakReference<Company>(company);
        this.abbr.setText(company.getAbbr());
        this.name.setText(company.getName());
    }

    @Operation(path = "ok")
    public boolean ok() {
        Company company = companyRef.get();
        if (company != null) {
            // write back
            company.setName(ObjectsUtil.checkAndTrim(name.getText()));
        }
        return true;
    }
}
