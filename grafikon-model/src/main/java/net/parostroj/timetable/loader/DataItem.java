package net.parostroj.timetable.loader;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.ls.ModelVersion;

import java.util.Map;

/**
 * Data item information.
 *
 * @author jub
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DataItem(String id, String filename, Map<String, String> settings,
        @JsonDeserialize(using = MVDeserializer.class)
        @JsonSerialize(using = MVSerializer.class)
        ModelVersion version,
        @JsonDeserialize(using = LStringDeserializer.class)
        @JsonSerialize(using = LStringSerializer.class)
        LocalizedString name,
        @JsonDeserialize(using = LStringDeserializer.class)
        @JsonSerialize(using = LStringSerializer.class)
        LocalizedString description) {}
