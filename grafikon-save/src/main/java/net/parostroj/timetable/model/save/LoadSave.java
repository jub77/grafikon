/*
 * LoadSave.java
 *
 * Created on 12.10.2007, 10:20:14
 */
package net.parostroj.timetable.model.save;

import java.nio.charset.StandardCharsets;
import net.parostroj.timetable.model.RuntimeInfo;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.parostroj.timetable.actions.AfterLoadCheck;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.ls.LSFile;

/**
 * Helper class for loading/saving model and other parts.
 *
 * @author jub
 */
public class LoadSave implements LSFile {

    private static final String METADATA = "metadata.properties";
    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final String TRAIN_DIAGRAM_NAME = "train_diagram.xml";
    private static final String TRAIN_TYPES_NAME = "train_types.xml";

    private static final List<ModelVersion> VERSIONS;

    static {
        VERSIONS = List.of(
                new ModelVersion(1, 0),
                new ModelVersion(2, 0),
                new ModelVersion(2, 1),
                new ModelVersion(2, 2));
    }

    private final List<TrainDiagramFilter> loadFilters;
    private final List<TrainDiagramFilter> saveFilters;

    public LoadSave() {
        loadFilters = new LinkedList<>();
        loadFilters.add(new TrainsNamesLoadFilter());
        loadFilters.add(new LineTypeLoadFilter());
        loadFilters.add(new WeightFilter());
        loadFilters.add(new RouteFilter());
        loadFilters.add(new LocalizationFilter());
        loadFilters.add(new RecalculateFilter());
        loadFilters.add(new NodeFilter());
        saveFilters = List.of();
    }

    @Override
    public TrainDiagram load(TrainDiagramType diagramType, File file) throws LSException {
        try (ZipFile zip = new ZipFile(file)) {
            TrainDiagram diagram;

            // load metadata
            ZipEntry entry = zip.getEntry(METADATA);
            Properties metadata = new Properties();
            if (entry != null) {
                // load metadata
                metadata.load(zip.getInputStream(entry));
            }

            // set model version
            ModelVersion modelVersion;
            if (metadata.getProperty(METADATA_KEY_MODEL_VERSION) == null) {
                modelVersion = ModelVersion.parseModelVersion("1.0");
            } else {
                modelVersion = ModelVersion.parseModelVersion(metadata.getProperty(METADATA_KEY_MODEL_VERSION));
            }

            ModelVersion latest = LSSerializer.getLatestVersion();
            if (latest.getMajorVersion() < modelVersion.getMajorVersion() || (latest.getMajorVersion() == modelVersion.getMajorVersion() && latest.getMinorVersion() < modelVersion.getMinorVersion())) {
                throw new LSException("Cannot load newer model.");
            }
            // load train types
            entry = zip.getEntry(TRAIN_TYPES_NAME);
            InputStream isTypes;
            if (entry == null) {
                isTypes = DefaultTrainTypeListSource.getDefaultTypesInputStream();
            } else {
                isTypes = zip.getInputStream(entry);
            }
            LSTrainTypeSerializer tts = LSTrainTypeSerializer.getLSTrainTypeSerializer();
            LSTrainTypeList trainTypeList = tts.load(new InputStreamReader(isTypes, StandardCharsets.UTF_8));

            // load model
            entry = zip.getEntry(TRAIN_DIAGRAM_NAME);
            if (entry == null) {
                throw new LSException("Model not found.");
            }
            diagram = this.loadTrainDiagram(modelVersion,
                    new InputStreamReader(zip.getInputStream(entry), StandardCharsets.UTF_8),
                    trainTypeList, diagramType);
            diagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE_VERSION, modelVersion);

            // load images
            LoadSaveImages lsImages = new LoadSaveImages();
            lsImages.loadTimetableImages(diagram, zip);

            return diagram;
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    @Override
    public void save(TrainDiagram diagram, File file) throws LSException {
        try (ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(file))) {
            // save metadata
            zipOutput.putNextEntry(new ZipEntry(METADATA));
            this.createMetadata().store(zipOutput, null);

            // save train types
            LSTrainTypeList trainTypeList = new LSTrainTypeList(diagram.getTrainTypes(), diagram.getTrainsData());
            zipOutput.putNextEntry(new ZipEntry(TRAIN_TYPES_NAME));
            LSTrainTypeSerializer tts = LSTrainTypeSerializer.getLSTrainTypeSerializer();
            tts.save(new OutputStreamWriter(zipOutput, StandardCharsets.UTF_8), trainTypeList);

            // save diagram
            zipOutput.putNextEntry(new ZipEntry(TRAIN_DIAGRAM_NAME));
            this.saveTrainDiagram(new OutputStreamWriter(zipOutput, StandardCharsets.UTF_8), diagram, trainTypeList);

            // save images
            LoadSaveImages lsImages = new LoadSaveImages();
            lsImages.saveTimetableImages(diagram, zipOutput);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    private Properties createMetadata() {
        Properties metadata = new Properties();
        metadata.setProperty(METADATA_KEY_MODEL_VERSION, LSSerializer.getLatestVersion().getVersion());
        return metadata;
    }

    private TrainDiagram loadTrainDiagram(ModelVersion modelVersion, Reader reader, LSTrainTypeList types,
            TrainDiagramType diagramType) throws LSException, IOException {
        LSSerializer serializer = LSSerializer.getLSSerializer(modelVersion);
        if (serializer == null) {
            throw new LSException("Serializer not initialized");
        }
        TrainDiagram diagram = serializer.load(reader, types, diagramType);
        for (TrainDiagramFilter filter : loadFilters) {
            diagram = filter.filter(diagram, modelVersion);
        }
        (new AfterLoadCheck()).check(diagram);
        return diagram;
    }

    private void saveTrainDiagram(Writer writer, TrainDiagram diagram, LSTrainTypeList trainTypeList) throws LSException, IOException {
        LSSerializer serializer = LSSerializer.getLatestLSSerializer();
        ModelVersion version = LSSerializer.getLatestVersion();
        for (TrainDiagramFilter filter : saveFilters) {
            diagram = filter.filter(diagram, version);
        }
        serializer.save(writer, diagram, trainTypeList);
        writer.flush();
    }

    @Override
    public TrainDiagram load(TrainDiagramType diagramType, ZipInputStream is) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void save(TrainDiagram diagram, ZipOutputStream os) throws LSException {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public List<ModelVersion> getLoadVersions() {
        return VERSIONS;
    }

    @Override
    public ModelVersion getSaveVersion() {
        return null;
    }

    @Override
    public Object getProperty(String key) {
        // no properties available
        return null;
    }

    @Override
    public void setProperty(String key, Object value) {
        // no properties available
    }
}
