package net.parostroj.timetable.utils;

import net.parostroj.timetable.model.AttributesHolder;
import net.parostroj.timetable.model.TranslatedString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Simple string substitution from binding.
 *
 * @author jub
 */
public class SimpleSubstitutedString implements SubstitutedString {

    private final List<SubstitutedString> stringParts;

    private SimpleSubstitutedString(List<SubstitutedString> stringParts) {
        this.stringParts = List.copyOf(stringParts);
    }

    @Override
    public String substitute(Map<String, Object> binding) {
        return stringParts.stream().map(str -> str.substitute(binding)).collect(Collectors.joining());
    }

    public static SimpleSubstitutedString parse(String string) {
        return StringSubstitutionParser.parse(string);
    }

    static class StringSubstitutionParser {

        private enum State { TEXT, VAR }

        static SimpleSubstitutedString parse(String string) {
            StringTokenizer tokenizer = new StringTokenizer(string, "${}", true);
            State state = State.TEXT;
            StringBuilder data = new StringBuilder();
            List<SubstitutedString> strings = new ArrayList<>();
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                State newState = switch (state) {
                    case TEXT -> handleText(token, tokenizer, data);
                    case VAR -> handleVar(token, data);
                };
                if (newState != state) {
                    handleState(state, data, strings);
                    data = new StringBuilder();
                }
                state = newState;
            }
            handleState(state, data, strings);
            return new SimpleSubstitutedString(strings);
        }

        private static State handleText(String token, StringTokenizer tokenizer, StringBuilder data) {
            if ("$".equals(token)) {
                if (tokenizer.hasMoreTokens() && ("{".equals(token = tokenizer.nextToken()))) {
                    return State.VAR;
                } else {
                    data.append("$");
                }
            }
            data.append(token);
            return State.TEXT;
        }

        private static State handleVar(String token, StringBuilder data) {
            if ("}".equals(token)) {
                return State.TEXT;
            }
            data.append(token);
            return State.VAR;
        }

        private static void handleState(State state, StringBuilder data, List<SubstitutedString> strings) {
            if (!data.isEmpty()) {
                switch (state) {
                    case TEXT -> {
                        String value = data.toString();
                        strings.add(binding -> value);
                    }
                    case VAR -> strings.add(processModuleOrVariable(data.toString()));
                }
            }
        }

        private static SubstitutedString processModuleOrVariable(String variableOrModule) {
            if (variableOrModule.contains(":")) {
                String[] module = variableOrModule.split(":");
                return switch (module[0]) {
                    case "default" -> defaultModule(module);
                    case "prefix" -> prefixModule(module);
                    case "suffix" -> suffixModule(module);
                    case "if" -> ifModule(module);
                    default -> binding -> "";
                };
            } else {
                Function<Map<String, Object>, Object> variableAccess = processVariableObject(variableOrModule);
                return binding -> {
                    Object value = variableAccess.apply(binding);
                    return adaptValue(value);
                };
            }
        }

        private static SubstitutedString defaultModule(String[] module) {
            Function<Map<String, Object>, Object> variableAccess = processVariableObject(module[1]);
            return  binding -> {
                Object value = variableAccess.apply(binding);
                if (value == null || ((value instanceof String valueStr) && valueStr.isBlank())) {
                    return module[2];
                } else {
                    return adaptValue(value);
                }
            };
        }

        private static SubstitutedString prefixModule(String[] module) {
            Function<Map<String, Object>, Object> variableAccess = processVariableObject(module[2]);
            return binding -> {
                Object value = variableAccess.apply(binding);
                if (value == null || ((value instanceof String valueStr) && valueStr.isBlank())) {
                    return "";
                } else {
                    return module[1] + adaptValue(value);
                }
            };
        }

        private static SubstitutedString suffixModule(String[] module) {
            Function<Map<String, Object>, Object> variableAccess = processVariableObject(module[2]);
            return binding -> {
                Object value = variableAccess.apply(binding);
                if (value == null || ((value instanceof String valueStr) && valueStr.isBlank())) {
                    return "";
                } else {
                    return adaptValue(value) + module[1];
                }
            };
        }

        private static SubstitutedString ifModule(String[] module) {
            Function<Map<String, Object>, Object> variableAccess = processVariableObject(module[1]);
            return binding -> {
                Object value = variableAccess.apply(binding);
                if (value != null && ((value instanceof Boolean valueBool) && valueBool)) {
                    return module[2];
                } else {
                    return module.length < 4 ? "" : module[3];
                }
            };
        }

        private static String adaptValue(Object value) {
            if (value instanceof TranslatedString ts) {
                return ts.getDefaultString();
            }
            return value == null ? "" : value.toString();
        }

        private static Function<Map<String, Object>, Object> processVariableObject(String variable) {
            if (variable.contains(".")) {
                String[] splitVariable = variable.split("\\.");
                return m -> {
                    Object o = m.get(splitVariable[0]);
                    int i = 1;
                    while (i < splitVariable.length) {
                        String prop = splitVariable[i++];
                        o = getProperty(o, prop);
                    }
                    return o;
                };
            } else {
                return m -> m.get(variable);
            }
        }

        private static Object getProperty(Object object, String property) {
            if (object == null) {
                return null;
            }
            Object value = null;
            if (object instanceof AttributesHolder ah) {
                value = ah.getAttribute(property, Object.class);
                if (value == null) {
                    // try to get getter
                    String met = property;
                    met = "get" + met.substring(0, 1).toUpperCase() + met.substring(1);
                    try {
                        Method method = object.getClass().getMethod(met);
                        value = method.invoke(object);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        // ignore (value is null)
                    }
                }
            }
            return value;
        }
    }
}
