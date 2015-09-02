package net.parostroj.timetable.utils;

import java.util.*;
import java.util.stream.Collectors;

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

    @SuppressWarnings("unchecked")
    public static <T> Collection<T> checkCollection(Collection<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            for (Object o : orig) {
                if (!clazz.isInstance(o)) {
                    throw new ClassCastException("Wrong class: " + o.getClass());
                }
            }
            return (Collection<T>) orig;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> checkList(List<?> orig, Class<T> clazz) {
        if (orig == null) {
            return null;
        } else {
            for (Object o : orig) {
                if (!clazz.isInstance(o)) {
                    throw new ClassCastException("Wrong class: " + o.getClass());
                }
            }
            return (List<T>) orig;
        }
    }
}
