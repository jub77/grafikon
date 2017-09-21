package net.parostroj.timetable.utils;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ObjectsUtil {

    private static final Logger log = LoggerFactory.getLogger(ObjectsUtil.class);

    private ObjectsUtil() {
    }

    /**
     * Returns only first line of the text.
     *
     * @param str string
     * @return first line of the string
     */
    public static String getFirstLine(String str) {
        String line = str;
        // keep only the first line
        int index = line.indexOf('\n');
        if (index != -1) {
            line = line.substring(0, index);
        }
        return line;
    }

    /**
     * Checks string and if it is not null, it trims it and transforms empty string to null,
     * otherwise it returns it trimmed.
     *
     * @param str string
     * @return checked and trimmed string
     */
    public static String checkAndTrim(String str) {
        String checkedStr = str;
        if (checkedStr != null) {
            checkedStr = checkedStr.trim();
            if ("".equals(checkedStr)) {
                checkedStr = null;
            }
        }
        return checkedStr;
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
     * Checks if the two collections intersects.
     *
     * @param a first collection
     * @param b second collection
     * @return if the two collections intersect
     */
    public static boolean intersects(Collection<?> a, Collection<?> b) {
        return a.stream().anyMatch(b::contains);
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
            return orig.stream().map(clazz::cast).collect(Collectors.toList());
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
            return Collections2.transform(orig, clazz::cast);
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
            return Lists.transform(orig, clazz::cast);
        }
    }

    public static <T> Set<T> checkedSet(Set<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            checkClass(orig, clazz);
            return orig.stream().map(clazz::cast).collect(Collectors.toSet());
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

    public static <T> Collection<T> checkEmpty(Collection<T> collection) {
        return collection == null ? null : (collection.isEmpty() ? null : collection);
    }

    public static <T> List<T> checkEmpty(List<T> list) {
        return list == null ? null : (list.isEmpty() ? null : list);
    }

    public static <K, V> Map<K, V> checkEmpty(Map<K, V> map) {
        return map == null ? null : (map.isEmpty() ? null : map);
    }

    private static <T> void checkClass(Collection<?> orig, Class<T> clazz) {
        if (orig.stream().anyMatch(o -> !checkInstanceOf(clazz, o))) {
            throw new ClassCastException("Wrong class");
        }
    }

    private static <T> boolean checkInstanceOf(Class<T> clazz, Object o) {
        boolean isInstanceOf = clazz.isInstance(o);
        if (!isInstanceOf) {
            if (o != null) {
                log.error("Wrong class: {} (expected: {})", o.getClass(), clazz);
            } else {
                log.error("Object is null");
            }
        }
        return isInstanceOf;
    }
}
