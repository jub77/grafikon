package net.parostroj.timetable.gui.pm;

import org.beanfabrics.model.PMManager;
import org.beanfabrics.model.TextPM;
import org.beanfabrics.support.OnChange;
import org.beanfabrics.support.Validation;

import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TimeConverter.Rounding;

public class TimePM extends TextPM {

    private TimeConverter converter;
    private boolean valid;

    public TimePM() {
        PMManager.setup(this);
    }

    public void setConverter(TimeConverter converter) {
        this.converter = converter;
    }

    public TimeConverter getConverter() {
        return converter;
    }

    public void setTime(int time) {
        checkConverter();
        setText(converter.convertIntToText(time));
    }

    public int getTime() {
        checkConverter();
        return converter.convertTextToInt(getText());
    }

    private void checkConverter() {
        if (converter == null) {
            // default converter supports only the whole minutes
            converter = new TimeConverter(Rounding.MINUTE);
        }
    }

    @OnChange(path = "text")
    public void initiateValid() {
        boolean oldValid = valid;
        valid = getTime() != -1;
        if (oldValid != valid) {
            getPropertyChangeSupport().firePropertyChange("valid", oldValid, valid);
        }
    }

    @Validation
    public boolean isValidTime() {
        return valid;
    }
}
