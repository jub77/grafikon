package net.parostroj.timetable.model;

import java.util.Map;
import java.util.Set;

public interface Permissions {

    boolean isAllowed(TextTemplate.Language language);
    boolean isAllowed(Script.Language language);
    boolean isOutputTemplateAllowed();

    default TextTemplate createTextTemplate(String template, TextTemplate.Language language) {
        if (isAllowed(language)) {
            return TextTemplate.create(template, language);
        } else {
            return null;
        }
    }

    default Script createScript(String sourceCode, Script.Language language) {
        if (isAllowed(language)) {
            return Script.create(sourceCode, language);
        } else {
            return null;
        }
    }

    static Permissions forType(TrainDiagramType type) {
        return PermissionsImpl.forType.get(type);
    }
}

class PermissionsImpl implements Permissions {

     static final Map<TrainDiagramType, Permissions> forType = Map.of(
            TrainDiagramType.NORMAL, new PermissionsImpl(
                    Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE),
                    Set.of(),
                    false),
            TrainDiagramType.RAW, new PermissionsImpl(
                    Set.of(TextTemplate.Language.PLAIN, TextTemplate.Language.SIMPLE, TextTemplate.Language.GROOVY),
                    Set.of(Script.Language.GROOVY),
                    true)
    );

    private final Set<TextTemplate.Language> templateAllowed;
    private final Set<Script.Language> scriptAllowed;
    private final boolean outputTemplateAllowed;

    PermissionsImpl(Set<TextTemplate.Language> templateAllowed, Set<Script.Language> scriptAllowed, boolean outputTemplateAllowed) {
        this.templateAllowed = templateAllowed;
        this.scriptAllowed = scriptAllowed;
        this.outputTemplateAllowed = outputTemplateAllowed;
    }

    @Override
    public boolean isAllowed(TextTemplate.Language language) {
        return templateAllowed.contains(language);
    }

    @Override
    public boolean isAllowed(Script.Language language) {
        return scriptAllowed.contains(language);
    }

    @Override
    public boolean isOutputTemplateAllowed() {
        return outputTemplateAllowed;
    }
}
