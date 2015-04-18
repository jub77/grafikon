package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
     * @throws net.parostroj.timetable.model.save.LSException
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
     * @throws net.parostroj.timetable.model.save.LSException
     */
    public static LSSerializer getLatestLSSerializer() throws LSException {
        return getLSSerializer(getLatestVersion());
    }

    /**
     * loads train diagram from reader.
     *
     * @param reader reader
     * @param trainTypeList list
     * @return train diagram
     * @throws net.parostroj.timetable.model.save.LSException
     */
    public abstract TrainDiagram load(Reader reader, LSTrainTypeList trainTypeList) throws LSException;

    /**
     * writes train diagram into the writer.
     *
     * @param writer the writer
     * @param diagram train diagram to be saved
     * @param trainTypeList list
     * @throws net.parostroj.timetable.model.save.LSException
     */
    public abstract void save(Writer writer, TrainDiagram diagram, LSTrainTypeList trainTypeList) throws LSException;

    /**
     * writes train diagram into the output stream.
     *
     * @param out output stream
     * @param diagram train diagram
     * @param trainTypeList list
     * @throws net.parostroj.timetable.model.save.LSException
     */
    public void save(OutputStream out, TrainDiagram diagram, LSTrainTypeList trainTypeList) throws LSException {
        try {
            this.save(new OutputStreamWriter(out,"utf-8"), diagram, trainTypeList);
        } catch (UnsupportedEncodingException e) {
            throw new LSException(e);
        }
    }
}
