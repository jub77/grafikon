package net.parostroj.timetable.model.ls.impl3;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.*;

/**
 * Implementation of FileLoadSave for model versions 3.x.
 *
 * @author jub
 */
public class FileLoadSaveImpl implements LSFile {

    private static final String METADATA = "metadata.properties";
    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final String METADATA_MODEL_VERSION = "3.1";
    private static final String DATA_TRAIN_DIAGRAM = "train_diagram.xml";
    private static final String DATA_NET = "net.xml";
    private static final String DATA_ROUTES = "routes/";
    private static final String DATA_TRAIN_TYPES = "train_types/";
    private static final String DATA_TRAINS = "trains/";
    private static final String DATA_ENGINE_CLASSES = "engine_classes/";
    private static final String DATA_TRAINS_CYCLES = "trains_cycles/";
    private static final String DATA_IMAGES = "images/";
    private final LSSerializer lss;
    private static final List<ModelVersion> VERSIONS;

    static {
        VERSIONS = List.of(
                new ModelVersion(3, 0),
                new ModelVersion(3, 1));
    }

    private final Map<String, Object> properties;

    public FileLoadSaveImpl() throws LSException {
        lss = new LSSerializer(true);
        properties = new HashMap<>();
    }

    private void save(LSSink sink, String itemName, Object saved) throws LSException, IOException {
        lss.save(sink.nextItem(itemName), saved);
    }

    private String createEntryName(String prefix, int cnt) {
        return String.format("%s%06d.%s", prefix, cnt, "xml");
    }

    private Properties createMetadata() {
        Properties metadata = new Properties();
        metadata.setProperty(METADATA_KEY_MODEL_VERSION, METADATA_MODEL_VERSION);
        return metadata;
    }

    private ModelVersion checkVersion(Properties props) throws LSException {
        ModelVersion current = ModelVersion.parseModelVersion(METADATA_MODEL_VERSION);
        ModelVersion loaded = ModelVersion.parseModelVersion(props.getProperty(METADATA_KEY_MODEL_VERSION));
        if (current.compareTo(loaded) < 0) {
            throw new LSException(String.format("Current version [%s] is older than the version of loaded file [%s].", current, loaded));
        }
        return loaded;
    }

    @Override
    public TrainDiagram load(LSSource source, LSFeature... features) throws LSException {
        try {
            LSSource.Item item;
            TrainDiagramBuilder builder = null;
            FileLoadSaveImages loadImages = new FileLoadSaveImages(DATA_IMAGES);
            ModelVersion version = (ModelVersion) properties.get(VERSION_PROPERTY);
            while ((item = source.nextItem()) != null) {
                if (item.name().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(item.stream());
                    version = checkVersion(props);
                    continue;
                }
                if (item.name().equals(DATA_TRAIN_DIAGRAM)) {
                    LSTrainDiagram lstd = lss.load(item.stream(), LSTrainDiagram.class);
                    builder = new TrainDiagramBuilder(lstd, getDiagramType(features));
                }
                // test diagram
                if (builder == null) {
                    throw new LSException("Train diagram builder has to be first entry: " + item.name());
                }
                if (item.name().equals(DATA_NET)) {
                    builder.setNet(lss.load(item.stream(), LSNet.class));
                } else if (item.name().startsWith(DATA_ROUTES)) {
                    builder.setRoute(lss.load(item.stream(), LSRoute.class));
                } else if (item.name().startsWith(DATA_TRAIN_TYPES)) {
                    builder.setTrainType(lss.load(item.stream(), LSTrainType.class));
                } else if (item.name().startsWith(DATA_TRAINS)) {
                    builder.setTrain(lss.load(item.stream(), LSTrain.class));
                } else if (item.name().startsWith(DATA_ENGINE_CLASSES)) {
                    builder.setEngineClass(lss.load(item.stream(), LSEngineClass.class));
                } else if (item.name().startsWith(DATA_TRAINS_CYCLES)) {
                    builder.setTrainsCycle(lss.load(item.stream(), LSTrainsCycle.class));
                } else if (item.name().startsWith(DATA_IMAGES)) {
                    if (item.name().endsWith(".xml")) {
                        builder.addImage(lss.load(item.stream(), LSImage.class));
                    } else {
                        builder.addImageFile(new File(item.name()).getName(), loadImages.loadTimetableImage(item.stream()));
                    }
                }
            }
            TrainDiagram trainDiagram = Objects.requireNonNull(builder).getTrainDiagram();
            new LoadFilter().checkDiagram(trainDiagram, version);
            trainDiagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE_VERSION, version);
            return trainDiagram;
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    @Override
    public void save(TrainDiagram diagram, LSSink sink) throws LSException {
        try (sink) {
            // save metadata
            this.createMetadata().store(sink.nextItem(METADATA), null);

            // save train diagram
            this.save(sink, DATA_TRAIN_DIAGRAM, new LSTrainDiagram(diagram));
            // save net
            this.save(sink, DATA_NET, new LSNet(diagram.getNet()));
            int cnt = 0;
            // save routes
            for (Route route : diagram.getRoutes()) {
                this.save(sink, this.createEntryName(DATA_ROUTES, cnt++), new LSRoute(route));
            }
            cnt = 0;
            // save train types
            for (TrainType trainType : diagram.getTrainTypes()) {
                this.save(sink, this.createEntryName(DATA_TRAIN_TYPES, cnt++), new LSTrainType(trainType));
            }
            cnt = 0;
            // save trains
            for (Train train : diagram.getTrains()) {
                this.save(sink, this.createEntryName(DATA_TRAINS, cnt++), new LSTrain(train));
            }
            cnt = 0;
            // save engine classes
            for (EngineClass engineClass : diagram.getEngineClasses()) {
                this.save(sink, this.createEntryName(DATA_ENGINE_CLASSES, cnt++), new LSEngineClass(engineClass));
            }
            cnt = 0;
            // save trains cycles
            for (TrainsCycle cycle : diagram.getCycles()) {
                this.save(sink, this.createEntryName(DATA_TRAINS_CYCLES, cnt++), new LSTrainsCycle(cycle));
            }

            // save images
            cnt = 0;
            FileLoadSaveImages saveImages = new FileLoadSaveImages(DATA_IMAGES);
            for (TimetableImage image : diagram.getImages()) {
                this.save(sink, createEntryName(DATA_IMAGES, cnt++), new LSImage(image));
                saveImages.saveTimetableImage(image, sink);
            }

        } catch (IOException ex) {
            throw new LSException(ex);
        }
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
        return properties.get(key);
    }

    @Override
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    private TrainDiagramType getDiagramType(LSFeature[] features) {
        return Stream.of(features)
                .filter(feature -> LSFeature.RAW_DIAGRAM == feature)
                .findAny()
                .map(f -> TrainDiagramType.RAW)
                .orElse(TrainDiagramType.NORMAL);
    }
}
