package net.parostroj.timetable.output2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.LoggerFactory;

/**
 * Output factory.
 *
 * @author jub
 */
public abstract class OutputFactory {

    private final Map<String, Object> parameters = new HashMap<>();

    private static final ServiceLoader<OutputFactory> loader = ServiceLoader.load(OutputFactory.class);
    private static final Map<String, Class<? extends OutputFactory>> cache = new ConcurrentHashMap<>();

    /**
     * @return list of output factory types
     */
    public static Collection<String> getTypes() {
        List<String> result = new ArrayList<>();
        synchronized(loader) {
            for (OutputFactory factory : loader) {
                result.add(factory.getType());
            }
        }
        return result;
    }

    /**
     * creates factory.
     *
     * @param type type of the factory
     * @return factory
     */
    public static OutputFactory newInstance(String type) {
        Class<? extends OutputFactory> clazz = cache.get(type);
        if (clazz == null) {
            synchronized (loader) {
                for (OutputFactory factory : loader) {
                    if (factory.getType().equals(type)) {
                        cache.put(type, factory.getClass());
                        return factory;
                    }
                }
            }
        } else {
            try {
                // create new instance
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                LoggerFactory.getLogger(OutputFactory.class).error("Cannot create instance.", e);
            }
        }
        throw new IllegalArgumentException("Unknown output factory type: " + type);
    }

    public abstract Output createOutput(String type) throws OutputException;

    public abstract String getType();

    public abstract Collection<String> getOutputTypes();

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }
}
