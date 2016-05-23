package net.parostroj.timetable.model.ls.impl4;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.parostroj.timetable.model.EngineClass;
import net.parostroj.timetable.model.OutputTemplate;
import net.parostroj.timetable.model.Route;
import net.parostroj.timetable.model.TextItem;
import net.parostroj.timetable.model.TimetableImage;
import net.parostroj.timetable.model.Train;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainType;
import net.parostroj.timetable.model.TrainsCycle;
import net.parostroj.timetable.model.changes.DiagramChangeSet;
import net.parostroj.timetable.model.ls.LSFile;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;

/**
 * Implementation of FileLoadSave for model versions 4.x.
 *
 * @author jub
 */
public class FileLoadSaveImpl extends AbstractLSImpl implements LSFile {

    private static final String DATA_TRAIN_DIAGRAM = "train_diagram.xml";
    private static final String DATA_PENALTY_TABLE = "penalty_table.xml";
    private static final String DATA_NET = "net.xml";
    private static final String FREIGHT_NET = "freight_net.xml";
    private static final String DATA_ROUTES = "routes/";
    private static final String DATA_TRAIN_TYPES = "train_types/";
    private static final String DATA_TRAINS = "trains/";
    private static final String DATA_TEXT_ITEMS = "text_items/";
    private static final String DATA_OUTPUT_TEMPLATES = "output_templates/";
    private static final String DATA_ENGINE_CLASSES = "engine_classes/";
    private static final String DATA_TRAINS_CYCLES = "trains_cycles/";
    private static final String DATA_IMAGES = "images/";
    private static final String DATA_ATTACHMENTS = "attachments/";
    private static final String DATA_CHANGES = "changes/";

    private static final List<ModelVersion> VERSIONS;
    private static final ModelVersion CURRENT_VERSION;

    static {
        VERSIONS = getVersions("4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9", "4.10",
                "4.11", "4.12", "4.13", "4.14", "4.15", "4.16", "4.17", "4.17.1", "4.18", "4.18.1", "4.18.2",
                "4.18.3", "4.18.4", "4.19", "4.19.1", "4.19.2", "4.20");
        CURRENT_VERSION = getLatestVersion(VERSIONS);
    }

    private final LSSerializer lss;
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
        metadata.setProperty(METADATA_KEY_MODEL_VERSION, CURRENT_VERSION.toString());
        return metadata;
    }

    @Override
    public TrainDiagram load(ZipInputStream zipInput) throws LSException {
        try {
            ZipEntry entry = null;
            TrainDiagramBuilder builder = null;
            FileLoadSaveImages loadImages = new FileLoadSaveImages(DATA_IMAGES);
            FileLoadSaveAttachments attachments = new FileLoadSaveAttachments(DATA_ATTACHMENTS);
            ModelVersion version = null;
            while ((entry = zipInput.getNextEntry()) != null) {
                if (entry.getName().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(zipInput);
                    version = checkVersion(METADATA_KEY_MODEL_VERSION, props);
                    continue;
                }
                if (entry.getName().equals(DATA_TRAIN_DIAGRAM)) {
                    LSTrainDiagram lstd = lss.load(zipInput, LSTrainDiagram.class);
                    builder = new TrainDiagramBuilder(lstd, attachments);
                }
                // test diagram
                if (builder == null) {
                    throw new LSException("Train diagram builder has to be first entry: " + entry.getName());
                }
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
                } else if (entry.getName().startsWith(DATA_IMAGES)) {
                    if (entry.getName().endsWith(".xml")) {
                        builder.addImage(lss.load(zipInput, LSImage.class));
                    } else {
                        builder.addImageFile(new File(entry.getName()).getName(), loadImages.loadTimetableImage(zipInput, entry));
                    }
                } else if (entry.getName().startsWith(DATA_ATTACHMENTS)) {
                    attachments.load(zipInput, entry);
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
            FileLoadSaveAttachments attachments = new FileLoadSaveAttachments(DATA_ATTACHMENTS);

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
                LSOutputTemplate lsOutputTemplate = Boolean.TRUE.equals(properties.get("inline.output.template.attachments")) ?
                        new LSOutputTemplate(template) :
                        new LSOutputTemplate(template, attachments);
                this.save(zipOutput, this.createEntryName(DATA_OUTPUT_TEMPLATES, "xml", cnt++), lsOutputTemplate);
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
            // save attachments
            attachments.save(zipOutput);

            // save freight net
            this.save(zipOutput, FREIGHT_NET, new LSFreightNet(diagram.getFreightNet()));

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
        return CURRENT_VERSION;
    }
}
