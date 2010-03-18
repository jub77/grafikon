package net.parostroj.timetable.model.ls.impl4;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Implementation of FileLoadSave for model versions 3.x.
 *
 * @author jub
 */
public class FileLoadSaveImpl implements FileLoadSave {

    private static final String METADATA = "metadata.properties";
    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final ModelVersion METADATA_MODEL_VERSION = new ModelVersion(4, 0);
    private static final String DATA_TRAIN_DIAGRAM = "train_diagram.xml";
    private static final String DATA_PENALTY_TABLE = "penalty_table.xml";
    private static final String DATA_NET = "net.xml";
    private static final String DATA_ROUTES = "routes/";
    private static final String DATA_TRAIN_TYPES = "train_types/";
    private static final String DATA_TRAINS = "trains/";
    private static final String DATA_ENGINE_CLASSES = "engine_classes/";
    private static final String DATA_TRAINS_CYCLES = "trains_cycles/";
    private static final String DATA_IMAGES = "images/";
    private LSSerializer lss;
    private static final List<ModelVersion> VERSIONS;

    static {
        VERSIONS = Collections.singletonList(new ModelVersion(4, 0));
    }

    public FileLoadSaveImpl() throws LSException {
        lss = new LSSerializer(true);
    }

    @Override
    public TrainDiagram load(File file) throws LSException {
        try {
            return this.load(new ZipInputStream(new FileInputStream(file)));
        } catch (FileNotFoundException ex) {
            throw new LSException(ex);
        }
    }

    @Override
    public void save(TrainDiagram diagram, File file) throws LSException {
        try {
            this.save(diagram, new ZipOutputStream(new FileOutputStream(file)));
        } catch (FileNotFoundException ex) {
            throw new LSException(ex);
        }
    }

    private void save(ZipOutputStream zipOutput, String zipEntryName, Object saved) throws LSException, IOException {
        zipOutput.putNextEntry(new ZipEntry(zipEntryName));
        lss.save(zipOutput, saved);
    }

    private String createEntryName(String prefix, String suffix, int cnt) {
        return String.format("%s%06d.%s", prefix, cnt, suffix);
    }

    private Properties createMetadata() {
        Properties metadata = new Properties();
        metadata.setProperty(METADATA_KEY_MODEL_VERSION, METADATA_MODEL_VERSION.toString());
        return metadata;
    }

    private void checkVersion(Properties props) throws LSException {
        ModelVersion current = METADATA_MODEL_VERSION;
        ModelVersion loaded = new ModelVersion(props.getProperty(METADATA_KEY_MODEL_VERSION));
        if (current.compareTo(loaded) < 0)
            throw new LSException(String.format("Current version [%s] is older than the version of loaded file [%s].", current.toString(), loaded.toString()));
    }

    @Override
    public TrainDiagram load(ZipInputStream zipInput) throws LSException {
        try {
            ZipEntry entry = null;
            TrainDiagramBuilder builder = null;
            FileLoadSaveImages loadImages = new FileLoadSaveImages(DATA_IMAGES);
            while ((entry = zipInput.getNextEntry()) != null) {
                if (entry.getName().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(zipInput);
                    checkVersion(props);
                    continue;
                }
                if (entry.getName().equals(DATA_TRAIN_DIAGRAM)) {
                    LSTrainDiagram lstd = lss.load(zipInput, LSTrainDiagram.class);
                    builder = new TrainDiagramBuilder(lstd);
                }
                // test diagram
                if (builder == null)
                    throw new LSException("Train diagram builder has to be first entry: " + entry.getName());
                if (entry.getName().equals(DATA_PENALTY_TABLE)) {
                    builder.setPenaltyTable(lss.load(zipInput, LSPenaltyTable.class));
                } else if (entry.getName().equals(DATA_NET)) {
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
                    if (entry.getName().endsWith(".xml"))
                        builder.addImage(lss.load(zipInput, LSImage.class));
                    else
                        builder.addImageFile(new File(entry.getName()).getName(), loadImages.loadTimetableImage(zipInput, entry));
                }
            }
            return builder.getTrainDiagram();
        } catch (IOException e) {
            throw new LSException(e);
        } finally {
            try {
                if (zipInput != null)
                    zipInput.close();
            } catch (IOException e) {
                throw new LSException(e);
            }
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
            // save penalty table
            this.save(zipOutput, DATA_PENALTY_TABLE, new LSPenaltyTable(diagram.getPenaltyTable()));
            // save net
            this.save(zipOutput, DATA_NET, new LSNet(diagram.getNet()));
            int cnt = 0;
            // save routes
            for (Route route : diagram.getRoutes()) {
                this.save(zipOutput, this.createEntryName(DATA_ROUTES, "xml", cnt++), new LSRoute(route));
            }
            cnt = 0;
            // save train types
            for (TrainType trainType : diagram.getTrainTypes()) {
                this.save(zipOutput, this.createEntryName(DATA_TRAIN_TYPES, "xml", cnt++), new LSTrainType(trainType));
            }
            cnt = 0;
            // save trains
            for (Train train : diagram.getTrains()) {
                this.save(zipOutput, this.createEntryName(DATA_TRAINS, "xml", cnt++), new LSTrain(train));
            }
            cnt = 0;
            // save engine classes
            for (EngineClass engineClass : diagram.getEngineClasses()) {
                this.save(zipOutput, this.createEntryName(DATA_ENGINE_CLASSES, "xml", cnt++), new LSEngineClass(engineClass));
            }
            cnt = 0;
            // save trains cycles
            Map<TrainsCycleType, List<TrainsCycle>> cyclesMap = diagram.getCyclesMap();
            for (Map.Entry<TrainsCycleType, List<TrainsCycle>> entry : cyclesMap.entrySet()) {
                for (TrainsCycle cycle : entry.getValue()) {
                    this.save(zipOutput, this.createEntryName(DATA_TRAINS_CYCLES, "xml", cnt++), new LSTrainsCycle(cycle));
                }
            }

            // save images
            cnt = 0;
            FileLoadSaveImages saveImages = new FileLoadSaveImages(DATA_IMAGES);
            for (TimetableImage image : diagram.getImages()) {
                this.save(zipOutput, createEntryName(DATA_IMAGES, "xml", cnt++), new LSImage(image));
                saveImages.saveTimetableImage(image, zipOutput);
            }

        } catch (IOException ex) {
            throw new LSException(ex);
        } finally {
            try {
                if (zipOutput != null)
                    zipOutput.close();
            } catch (IOException ex) {
                throw new LSException(ex);
            }
        }
    }

    @Override
    public List<ModelVersion> getLoadVersions() {
        return VERSIONS;
    }

    @Override
    public ModelVersion getSaveVersion() {
        return METADATA_MODEL_VERSION;
    }
}
