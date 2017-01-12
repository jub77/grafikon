package net.parostroj.timetable.gui.wrappers;

/**
 * Using wrapped conversion.
 *
 * @author jub
 *
 * @param <T> type of wrapper element
 */
public class WrapperDelegateAdapter<T> extends BasicWrapperDelegate<T> {

    private final WrapperConversion<? super T> conversion;
    private WrapperConversion<? super T> compareConversion;

    public WrapperDelegateAdapter(WrapperConversion<? super T> conversion) {
        this(conversion, conversion);
    }

    public WrapperDelegateAdapter(WrapperConversion<? super T> conversion, WrapperConversion<? super T> compareConversion) {
        this.conversion = conversion;
        this.compareConversion = compareConversion;
    }

    @Override
    public String toCompareString(T element) {
        return compareConversion.toString(element);
    }

    @Override
    public String toString(T element) {
        return conversion.toString(element);
    }
}
