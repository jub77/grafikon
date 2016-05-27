package net.parostroj.timetable.model.ls.impl4;

import com.google.common.collect.BiMap;
import com.google.common.collect.EnumHashBiMap;

import net.parostroj.timetable.model.library.LibraryItemType;

class LSLibraryTypeMapping {

    static BiMap<LibraryItemType, String> OUTPUT_DIR_MAPPING;

    static {
        OUTPUT_DIR_MAPPING = EnumHashBiMap.create(LibraryItemType.class);
        OUTPUT_DIR_MAPPING.put(LibraryItemType.NODE, "nodes");
        OUTPUT_DIR_MAPPING.put(LibraryItemType.OUTPUT_TEMPLATE, "output_templates");
        OUTPUT_DIR_MAPPING.put(LibraryItemType.ENGINE_CLASS, "engine_classes");
        OUTPUT_DIR_MAPPING.put(LibraryItemType.TRAIN_TYPE, "train_types");
    }

    public static String typeToDirectory(LibraryItemType itemType) {
        return OUTPUT_DIR_MAPPING.get(itemType);
    }

    public static LibraryItemType typeFromDirectory(String directory) {
        return OUTPUT_DIR_MAPPING.inverse().get(directory);
    }
}
