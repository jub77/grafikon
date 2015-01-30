package net.parostroj.timetable.gui.pm;

import net.parostroj.timetable.model.TimeConverter;
import net.parostroj.timetable.model.TimeConverter.Rounding;

import org.beanfabrics.model.TextPM;

public class TimePM extends TextPM {

    private TimeConverter converter;

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
}
