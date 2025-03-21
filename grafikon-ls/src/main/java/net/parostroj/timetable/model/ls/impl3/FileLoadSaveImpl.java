package net.parostroj.timetable.model.ls.impl3;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.LSFeature;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;

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

    @Override
    public TrainDiagram load(File file, LSFeature... features) throws LSException {
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file))) {
            return this.load(inputStream, features);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    @Override
    public void save(TrainDiagram diagram, File file) throws LSException {
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file))) {
            this.save(diagram, outputStream);
        } catch (IOException ex) {
            throw new LSException(ex);
        }
    }

    private void save(ZipOutputStream zipOutput, String zipEntryName, Object saved) throws LSException, IOException {
        zipOutput.putNextEntry(new ZipEntry(zipEntryName));
        lss.save(zipOutput, saved);
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
    public TrainDiagram load(ZipInputStream zipInput, LSFeature... features) throws LSException {
        try {
            ZipEntry entry;
            TrainDiagramBuilder builder = null;
            FileLoadSaveImages loadImages = new FileLoadSaveImages(DATA_IMAGES);
            ModelVersion version = (ModelVersion) properties.get(VERSION_PROPERTY);
            while ((entry = zipInput.getNextEntry()) != null) {
                if (entry.getName().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(zipInput);
                    version = checkVersion(props);
                    continue;
                }
                if (entry.getName().equals(DATA_TRAIN_DIAGRAM)) {
                    LSTrainDiagram lstd = lss.load(zipInput, LSTrainDiagram.class);
                    builder = new TrainDiagramBuilder(lstd, getDiagramType(features));
                }
                // test diagram
                if (builder == null) {
                    throw new LSException("Train diagram builder has to be first entry: " + entry.getName());
                }
                if (entry.getName().equals(DATA_NET)) {
                    builder.setNet(lss.load(zipInput, LSNet.class));
                } else if (entry.getName().startsWith(DATA_ROUTES)) {
                    builder.setRoute(lss.load(zipInput, LSRoute.class));
                } else if (entry.getName().startsWith(DATA_TRAIN_TYPES)) {
                    builder.setTrainType(lss.load(zipInput, LSTrainType.class));
                } else if (entry.getName().startsWith(DATA_TRAINS)) {
                    builder.setTrain(lss.load(zipInput, LSTrain.class));
                } else if (entry.getName().startsWith(DATA_ENGINE_CLASSES)) {
                    builder.setEngineClass(lss.load(zipInput, LSEngineClass.class));
                } else if (entry.getName().startsWith(DATA_TRAINS_CYCLES)) {
                    builder.setTrainsCycle(lss.load(zipInput, LSTrainsCycle.class));
                } else if (entry.getName().startsWith(DATA_IMAGES)) {
                    if (entry.getName().endsWith(".xml")) {
                        builder.addImage(lss.load(zipInput, LSImage.class));
                    } else {
                        builder.addImageFile(new File(entry.getName()).getName(), loadImages.loadTimetableImage(zipInput));
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
    public void save(TrainDiagram diagram, ZipOutputStream zipOutput) throws LSException {
        try {
            // save metadata
            zipOutput.putNextEntry(new ZipEntry(METADATA));
            this.createMetadata().store(zipOutput, null);

            // save train diagram
            this.save(zipOutput, DATA_TRAIN_DIAGRAM, new LSTrainDiagram(diagram));
            // save net
            this.save(zipOutput, DATA_NET, new LSNet(diagram.getNet()));
            int cnt = 0;
            // save routes
            for (Route route : diagram.getRoutes()) {
                this.save(zipOutput, this.createEntryName(DATA_ROUTES, cnt++), new LSRoute(route));
            }
            cnt = 0;
            // save train types
            for (TrainType trainType : diagram.getTrainTypes()) {
                this.save(zipOutput, this.createEntryName(DATA_TRAIN_TYPES, cnt++), new LSTrainType(trainType));
            }
            cnt = 0;
            // save trains
            for (Train train : diagram.getTrains()) {
                this.save(zipOutput, this.createEntryName(DATA_TRAINS, cnt++), new LSTrain(train));
            }
            cnt = 0;
            // save engine classes
            for (EngineClass engineClass : diagram.getEngineClasses()) {
                this.save(zipOutput, this.createEntryName(DATA_ENGINE_CLASSES, cnt++), new LSEngineClass(engineClass));
            }
            cnt = 0;
            // save trains cycles
            for (TrainsCycle cycle : diagram.getCycles()) {
                this.save(zipOutput, this.createEntryName(DATA_TRAINS_CYCLES, cnt++), new LSTrainsCycle(cycle));
            }

            // save images
            cnt = 0;
            FileLoadSaveImages saveImages = new FileLoadSaveImages(DATA_IMAGES);
            for (TimetableImage image : diagram.getImages()) {
                this.save(zipOutput, createEntryName(DATA_IMAGES, cnt++), new LSImage(image));
                saveImages.saveTimetableImage(image, zipOutput);
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
