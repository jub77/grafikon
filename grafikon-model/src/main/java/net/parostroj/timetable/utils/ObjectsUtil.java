package net.parostroj.timetable.utils;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ObjectsUtil {

    private ObjectsUtil() {
    }

    /**
     * Returns only first line of the text.
     *
     * @param str string
     * @return first line of the string
     */
    public static String getFirstLine(String str) {
        // keep only the first line
        int index = str.indexOf('\n');
        if (index != -1) {
            str = str.substring(0, index);
        }
        return str;
    }

    /**
     * Checks string and if it is not null, it trims it and transforms empty string to null,
     * otherwise it returns it trimmed.
     *
     * @param str string
     * @return checked and trimmed string
     */
    public static String checkAndTrim(String str) {
        if (str != null) {
            str = str.trim();
            if ("".equals(str)) {
                str = null;
            }
        }
        return str;
    }

    /**
     * Trims non-null string. If it is null, it returns empty string.
     *
     * @param str string
     * @return trimmed string
     */
    public static String trimNonEmpty(String str) {
        return (str == null) ? "" : str.trim();
    }

    /**
     * Returns if the instances are equal or not.
     *
     * @param o1 first object
     * @param o2 second object
     * @return equality
     */
    public static boolean compareWithNull(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }

    /**
     * Returns <code>true</code> if the string is empty or null.
     *
     * @param str string
     * @return if it is null or empty
     */
    public static boolean isEmpty(String str) {
        return null == checkAndTrim(str);
    }

    /**
     * Returns new list of given type. It checks if the source collection contains elements of
     * given type.
     *
     * @param orig source collection
     * @param clazz type of elements
     * @return list
     */
    public static <T> List<T> copyToList(Collection<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            return orig.stream().map(o -> clazz.cast(o)).collect(Collectors.toList());
        }
    }

    /**
     * Returns checked read-only collection. It checks if the elements are of given type.
     *
     * @param orig source collection
     * @param clazz element type
     * @return checked collection
     */
    public static <T> Collection<T> checkedCollection(Collection<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            checkClass(orig, clazz);
            return Collections2.transform(orig, o -> clazz.cast(o));
        }
    }

    /**
     * Returns checked read-only list. It checks if the elements are of given type.
     *
     * @param orig source list
     * @param clazz element type
     * @return checked list
     */
    public static <T> List<T> checkedList(List<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            checkClass(orig, clazz);
            return Lists.transform(orig, o -> clazz.cast(o));
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> checkedMap(Map<?, ?> orig, Class<K> keyClazz, Class<V> valueClazz) {
        if (orig == null) {
            return null;
        } else {
            checkClass(orig.keySet(), keyClazz);
            checkClass(orig.values(), valueClazz);
            return (Map<K, V>) ImmutableMap.copyOf(orig);
        }
    }

    private static <T> void checkClass(Collection<?> orig, Class<T> clazz) {
        if (orig.stream().anyMatch(o -> !clazz.isInstance(o))) {
            throw new ClassCastException("Wrong class");
        }
    }
}
