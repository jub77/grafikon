package net.parostroj.timetable.actions.scripts;

import com.fasterxml.jackson.annotation.JsonInclude;
import net.parostroj.timetable.model.Script.Language;

/**
 * Description of script.
 *
 * @author jub
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScriptDescription(String id, String name, Language language, String location) {}
