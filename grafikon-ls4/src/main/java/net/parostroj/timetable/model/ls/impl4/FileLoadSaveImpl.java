package net.parostroj.timetable.model.ls.impl4;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.zip.*;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.model.ls.FileLoadSave;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Implementation of FileLoadSave for model versions 4.x.
 *
 * @author jub
 */
public class FileLoadSaveImpl implements FileLoadSave {

    private static final String METADATA = "metadata.properties";
    private static final String METADATA_KEY_MODEL_VERSION = "model.version";
    private static final ModelVersion METADATA_MODEL_VERSION;
    private static final String DATA_TRAIN_DIAGRAM = "train_diagram.xml";
    private static final String DATA_PENALTY_TABLE = "penalty_table.xml";
    private static final String DATA_NET = "net.xml";
    private static final String FREIGHT_NET = "freight_net.xml";
    private static final String LOCALIZATION = "localization.xml";
    private static final String DATA_ROUTES = "routes/";
    private static final String DATA_TRAIN_TYPES = "train_types/";
    private static final String DATA_TRAINS = "trains/";
    private static final String DATA_TEXT_ITEMS = "text_items/";
    private static final String DATA_OUTPUT_TEMPLATES = "output_templates/";
    private static final String DATA_ENGINE_CLASSES = "engine_classes/";
    private static final String DATA_TRAINS_CYCLES = "trains_cycles/";
    private static final String DATA_IMAGES = "images/";
    private static final String DATA_CHANGES = "changes/";
    private final LSSerializer lss;
    private static final List<ModelVersion> VERSIONS;

    static {
        List<ModelVersion> versions = Arrays.asList(
                new ModelVersion(4, 0),
                new ModelVersion(4, 1),
                new ModelVersion(4, 2),
                new ModelVersion(4, 3),
                new ModelVersion(4, 4),
                new ModelVersion(4, 5),
                new ModelVersion(4, 6),
                new ModelVersion(4, 7),
                new ModelVersion(4, 8),
                new ModelVersion(4, 9),
                new ModelVersion(4, 10),
                new ModelVersion(4, 11),
                new ModelVersion(4, 12),
                new ModelVersion(4, 13),
                new ModelVersion(4, 14));
        VERSIONS = Collections.unmodifiableList(versions);
        METADATA_MODEL_VERSION = VERSIONS.get(VERSIONS.size() - 1);
    }

    public FileLoadSaveImpl() throws LSException {
        lss = new LSSerializer(true);
    }

    @Override
    public TrainDiagram load(File file) throws LSException {
        try (ZipInputStream inputStream = new ZipInputStream(new FileInputStream(file))) {
            return this.load(inputStream);
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

    private String createEntryName(String prefix, String suffix, int cnt) {
        return String.format("%s%06d.%s", prefix, cnt, suffix);
    }

    private Properties createMetadata() {
        Properties metadata = new Properties();
        metadata.setProperty(METADATA_KEY_MODEL_VERSION, METADATA_MODEL_VERSION.toString());
        return metadata;
    }

    private ModelVersion checkVersion(Properties props) throws LSException {
        ModelVersion current = METADATA_MODEL_VERSION;
        ModelVersion loaded = new ModelVersion(props.getProperty(METADATA_KEY_MODEL_VERSION));
        if (current.compareTo(loaded) < 0)
            throw new LSException(String.format("Current version [%s] is older than the version of loaded file [%s].", current.toString(), loaded.toString()));
        return loaded;
    }

    @Override
    public TrainDiagram load(ZipInputStream zipInput) throws LSException {
        try {
            ZipEntry entry = null;
            TrainDiagramBuilder builder = null;
            FileLoadSaveImages loadImages = new FileLoadSaveImages(DATA_IMAGES);
            ModelVersion version = null;
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
                } else if (entry.getName().startsWith(DATA_TEXT_ITEMS)) {
                    builder.setTextItem(lss.load(zipInput, LSTextItem.class));
                } else if (entry.getName().startsWith(DATA_OUTPUT_TEMPLATES)) {
                    builder.setOutputTemplate(lss.load(zipInput, LSOutputTemplate.class));
                } else if (entry.getName().startsWith(DATA_TRAINS)) {
                    builder.setTrain(lss.load(zipInput, LSTrain.class));
                } else if (entry.getName().startsWith(DATA_ENGINE_CLASSES)) {
                    builder.setEngineClass(lss.load(zipInput, LSEngineClass.class));
                } else if (entry.getName().startsWith(DATA_TRAINS_CYCLES)) {
                    builder.setTrainsCycle(lss.load(zipInput, LSTrainsCycle.class));
                } else if (entry.getName().startsWith(DATA_CHANGES)) {
                    builder.setDiagramChangeSet(lss.load(zipInput, LSDiagramChangeSet.class));
                } else if (entry.getName().startsWith(FREIGHT_NET)) {
                    builder.setFreightNet(lss.load(zipInput, LSFreightNet.class));
                } else if (entry.getName().startsWith(LOCALIZATION)) {
                    builder.setLocalization(lss.load(zipInput, LSLocalization.class));
                } else if (entry.getName().startsWith(DATA_IMAGES)) {
                    if (entry.getName().endsWith(".xml"))
                        builder.addImage(lss.load(zipInput, LSImage.class));
                    else
                        builder.addImageFile(new File(entry.getName()).getName(), loadImages.loadTimetableImage(zipInput, entry));
                }
            }
            TrainDiagram trainDiagram = builder.getTrainDiagram();
            new LoadFilter().checkDiagram(trainDiagram, version);
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
            // save text items
            for (TextItem item : diagram.getTextItems()) {
                this.save(zipOutput, this.createEntryName(DATA_TEXT_ITEMS, "xml", cnt++), new LSTextItem(item));
            }
            cnt = 0;
            // save output templates
            for (OutputTemplate template : diagram.getOutputTemplates()) {
                this.save(zipOutput, this.createEntryName(DATA_OUTPUT_TEMPLATES, "xml", cnt++), new LSOutputTemplate(template));
            }
            cnt = 0;
            // save diagram change sets
            for (String version : diagram.getChangesTracker().getVersions()) {
                DiagramChangeSet set = diagram.getChangesTracker().getChangeSet(version);
                if (!set.getChanges().isEmpty())
                    this.save(zipOutput, this.createEntryName(DATA_CHANGES, "xml", cnt++), new LSDiagramChangeSet(set));
            }
            cnt = 0;
            // save trains cycles
            for (TrainsCycle cycle : diagram.getCycles()) {
                this.save(zipOutput, this.createEntryName(DATA_TRAINS_CYCLES, "xml", cnt++), new LSTrainsCycle(cycle));
            }

            // save images
            cnt = 0;
            FileLoadSaveImages saveImages = new FileLoadSaveImages(DATA_IMAGES);
            for (TimetableImage image : diagram.getImages()) {
                this.save(zipOutput, createEntryName(DATA_IMAGES, "xml", cnt++), new LSImage(image));
                saveImages.saveTimetableImage(image, zipOutput);
            }
            // save freight net
            this.save(zipOutput, FREIGHT_NET, new LSFreightNet(diagram.getFreightNet()));
            // save localization
            this.save(zipOutput, LOCALIZATION, new LSLocalization(diagram.getLocalization()));

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
        return METADATA_MODEL_VERSION;
    }
}
