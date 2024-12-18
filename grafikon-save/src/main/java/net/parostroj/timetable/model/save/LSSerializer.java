package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.LSException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.ModelVersion;
import net.parostroj.timetable.model.save.version01.LSSerializerImpl1;
import net.parostroj.timetable.model.save.version02.LSSerializerImpl2;

public abstract class LSSerializer {

    private static final ModelVersion LATEST = ModelVersion.parseModelVersion("2.2");

    /**
     * Private constructor.
     */
    protected LSSerializer() {
    }

    /**
     * @return latest version of the model to be saved
     */
    public static ModelVersion getLatestVersion() {
        return LATEST;
    }

    /**
     * returns serializer for specified version.
     *
     * @param version version
     * @return serializer
     */
    public static LSSerializer getLSSerializer(ModelVersion version) throws LSException {
        if (version == null || version.getMajorVersion() == 1) {
            // the oldest
            return new LSSerializerImpl1();
        } else if (version.getMajorVersion() == 2) {
            return new LSSerializerImpl2();
        }
        return null;
    }

    /**
     * returns serializer for current version.
     *
     * @return serializer
     */
    public static LSSerializer getLatestLSSerializer() throws LSException {
        return getLSSerializer(getLatestVersion());
    }

    /**
     * loads train diagram from reader.
     *
     * @param reader reader
     * @param trainTypeList list
     * @param diagramType diagram type
     * @return train diagram
     */
    public abstract TrainDiagram load(Reader reader, LSTrainTypeList trainTypeList, TrainDiagramType diagramType) throws LSException;

    /**
     * writes train diagram into the writer.
     *
     * @param writer the writer
     * @param diagram train diagram to be saved
     * @param trainTypeList list
     */
    public abstract void save(Writer writer, TrainDiagram diagram, LSTrainTypeList trainTypeList) throws LSException;

    /**
     * writes train diagram into the output stream.
     *
     * @param out output stream
     * @param diagram train diagram
     * @param trainTypeList list
     */
    public void save(OutputStream out, TrainDiagram diagram, LSTrainTypeList trainTypeList) throws LSException {
        this.save(new OutputStreamWriter(out, StandardCharsets.UTF_8), diagram, trainTypeList);
    }
}
