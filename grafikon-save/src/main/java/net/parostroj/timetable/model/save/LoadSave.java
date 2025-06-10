/*
 * LoadSave.java
 *
 * Created on 12.10.2007, 10:20:14
 */
package net.parostroj.timetable.model.save;

import java.nio.charset.StandardCharsets;
import net.parostroj.timetable.model.RuntimeInfo;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.*;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import net.parostroj.timetable.actions.AfterLoadCheck;
import net.parostroj.timetable.model.TrainDiagram;

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
        loadFilters = List.of(
                new TrainsNamesLoadFilter(),
                new LineTypeLoadFilter(),
                new WeightFilter(),
                new RouteFilter(),
                new LocalizationFilter(),
                new RecalculateFilter(),
                new NodeFilter());
        saveFilters = List.of();
    }

    @Override
    public TrainDiagram load(LSSource source, LSFeature... features) throws LSException {
        try (source) {
            TrainDiagram diagram;

            // load metadata
            LSSource.Item item = source.nextItem();
            Properties metadata = new Properties();
            if (item.name().equals(METADATA)) {
                // load metadata
                metadata.load(item.stream());
                item = source.nextItem();
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
            InputStream isTypes;
            boolean types = false;
            if (item.name().equals(TRAIN_TYPES_NAME)) {
                isTypes = item.stream();
                types = true;
            } else {
                isTypes = DefaultTrainTypeListSource.getDefaultTypesInputStream();
            }
            LSTrainTypeSerializer tts = LSTrainTypeSerializer.getLSTrainTypeSerializer();
            LSTrainTypeList trainTypeList = tts.load(new InputStreamReader(isTypes, StandardCharsets.UTF_8));

            if (types) {
                item = source.nextItem();
            }

            // load model
            if (!item.name().equals(TRAIN_DIAGRAM_NAME)) {
                throw new LSException("Model not found.");
            }
            diagram = this.loadTrainDiagram(modelVersion,
                    new InputStreamReader(item.stream(), StandardCharsets.UTF_8),
                    trainTypeList, getDiagramType(features));
            diagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE_VERSION, modelVersion);

            // load images
            LoadSaveImages lsImages = new LoadSaveImages();
            lsImages.loadTimetableImages(diagram, source);

            return diagram;
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    @Override
    public void save(TrainDiagram diagram, LSSink sink) throws LSException {
        try (sink) {
            // save metadata
            this.createMetadata().store(sink.nextItem(METADATA), null);

            // save train types
            LSTrainTypeList trainTypeList = new LSTrainTypeList(diagram.getTrainTypes(), diagram.getTrainsData());
            LSTrainTypeSerializer tts = LSTrainTypeSerializer.getLSTrainTypeSerializer();
            tts.save(new OutputStreamWriter(sink.nextItem(TRAIN_TYPES_NAME), StandardCharsets.UTF_8), trainTypeList);

            // save diagram
            this.saveTrainDiagram(new OutputStreamWriter(sink.nextItem(TRAIN_DIAGRAM_NAME), StandardCharsets.UTF_8), diagram, trainTypeList);

            // save images
            LoadSaveImages lsImages = new LoadSaveImages();
            lsImages.saveTimetableImages(diagram, sink);
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

    private TrainDiagramType getDiagramType(LSFeature[] features) {
        return Stream.of(features)
                .filter(feature -> LSFeature.RAW_DIAGRAM == feature)
                .findAny()
                .map(f -> TrainDiagramType.RAW)
                .orElse(TrainDiagramType.NORMAL);
    }
}
