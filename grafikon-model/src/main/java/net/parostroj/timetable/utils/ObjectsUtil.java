package net.parostroj.timetable.utils;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public class ObjectsUtil {

    public static String checkAndTrim(String str) {
        if (str != null) {
            str = str.trim();
            if ("".equals(str)) {
                str = null;
            }
        }
        return str;
    }

    public static String trimNonEmpty(String str) {
        return (str == null) ? "" : str.trim();
    }

    public static boolean compareWithNull(Object o1, Object o2) {
        if (o1 == null && o2 == null) {
            return true;
        } else if (o1 != null && o1.equals(o2)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isEmpty(String str) {
        return null == checkAndTrim(str);
    }

    public static <T> List<T> copyToList(Collection<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            return orig.stream().map(o -> clazz.cast(o)).collect(Collectors.toList());
        }
    }

    public static <T> Collection<T> checkedCollection(Collection<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            checkClass(orig, clazz);
            return Collections2.transform(orig, o -> clazz.cast(o));
        }
    }

    public static <T> List<T> checkedList(List<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            checkClass(orig, clazz);
            return Lists.transform(orig, o -> clazz.cast(o));
        }
    }

    private static <T> void checkClass(Collection<?> orig, Class<T> clazz) {
        if (orig.stream().anyMatch(o -> !clazz.isInstance(o))) {
            throw new ClassCastException("Wrong class");
        }
    }
}
