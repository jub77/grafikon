package net.parostroj.timetable.output2;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Output factory.
 *
 * @author jub
 */
public abstract class OutputFactory {

    private Map<String, Object> parameters = new HashMap<String, Object>();

    private static final ServiceLoader<OutputFactory> loader = ServiceLoader.load(OutputFactory.class);
    private static final Map<String, Class<? extends OutputFactory>> cache = new ConcurrentHashMap<String, Class<? extends OutputFactory>>();

    /**
     * creates factory.
     *
     * @param type type of the factory
     * @param locale locale
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
                return clazz.newInstance();
            } catch (Exception e) {
                Logger.getLogger(OutputFactory.class.getName()).log(Level.SEVERE, "Cannot create instance: " + clazz.getName(), e);
            }
        }
        throw new IllegalArgumentException("Unknown output factory type: " + type);
    }

    public abstract Output createOutput(String type);

    public abstract String getType();

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }
}
