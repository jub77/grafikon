package net.parostroj.timetable.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Result list.
 * 
 * @author jub
 */
public class ResultList<T> {
    
    private T singleValue;
    private List<T> multipleValues;
    
    public void add(T value) {
        if (multipleValues != null)
            multipleValues.add(value);
        else {
            if (singleValue == null)
                singleValue = value;
            else {
                multipleValues = new LinkedList<T>();
                multipleValues.add(singleValue);
                multipleValues.add(value);
                singleValue = null;
            }
        }
    }
    
    public List<T> get() {
        List<T> result = multipleValues != null ? multipleValues : (singleValue != null ? Collections.singletonList(singleValue) : Collections.<T>emptyList());
        singleValue = null;
        multipleValues = null;
        return result;
    }
}
