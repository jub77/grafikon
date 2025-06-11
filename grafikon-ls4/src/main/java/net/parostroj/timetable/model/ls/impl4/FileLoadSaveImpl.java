package net.parostroj.timetable.model.ls.impl4;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

import net.parostroj.timetable.model.*;
import net.parostroj.timetable.model.ls.*;
import net.parostroj.timetable.model.ls.impl4.filters.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.changes.DiagramChangeSet;

/**
 * Implementation of FileLoadSave for model versions 4.x.
 *
 * @author jub
 */
public class FileLoadSaveImpl extends AbstractLSImpl implements LSFile {

    private static final Logger log = LoggerFactory.getLogger(FileLoadSaveImpl.class);

    private static final String INLINE_ATTACHMENTS = "inline.output.template.attachments";

    private static final String DATA_TRAIN_DIAGRAM = "train_diagram.xml";
    private static final String DATA_PENALTY_TABLE = "penalty_table.xml";
    private static final String DATA_NET = "net.xml";
    private static final String FREIGHT_NET = "freight_net.xml";
    private static final String DATA_ROUTES = "routes/";
    private static final String DATA_TRAIN_TYPES = "train_types/";
    private static final String DATA_TRAIN_TYPE_CATEGORIES = "train_type_categories/";
    private static final String DATA_TRAINS = "trains/";
    private static final String DATA_TEXT_ITEMS = "text_items/";
    private static final String DATA_OUTPUT_TEMPLATES = "output_templates/";
    private static final String DATA_OUTPUTS = "outputs/";
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
                "4.18.3", "4.18.4", "4.19", "4.19.1", "4.19.2", "4.20", "4.21", "4.21.1", "4.21.2", "4.21.3",
                "4.22", "4.23", "4.23.1", "4.24", "4.24.1", "4.24.2", "4.25", "4.26", "4.26.1", "4.27",
                "4.28");
        CURRENT_VERSION = getLatestVersion(VERSIONS);
    }

    private final LSSerializer lss;
    private final List<LoadFilter> loadFilters;

    public FileLoadSaveImpl() throws LSException {
        lss = new LSSerializer(true);
        loadFilters = List.of(
                // filter up to 22 is first because it deals with change from list to set (format)
                new LoadFilter4d22(),
                // filters with increasing version
                new LoadFilter4d2(),
                new LoadFilter4d7(),
                new LoadFilter4d13(),
                new LoadFilter4d18(),
                new LoadFilter4d19(),
                new LoadFilter4d21(),
                new LoadFilter4d24(),
                new LoadFilter4d26(),
                new LoadFilter4d27());
        properties.put(INLINE_ATTACHMENTS, true);
    }

    private void save(LSSink sink, String itemName, Object saved) throws LSException, IOException {
        lss.save(sink.nextItem(itemName), saved);
    }

    private String createEntryName(String prefix, int cnt) {
        return String.format("%s%06d.%s", prefix, cnt, "xml");
    }

    @Override
    public TrainDiagram load(LSSource source, LSFeature... features) throws LSException {
        try {
            LSSource.Item item;
            TrainDiagramBuilder builder = null;
            FileLoadSaveImages loadImages = new FileLoadSaveImages(DATA_IMAGES);
            FileLoadSaveAttachments attachments = new FileLoadSaveAttachments(DATA_ATTACHMENTS);
            ModelVersion version = (ModelVersion) properties.get(VERSION_PROPERTY);
            while ((item = source.nextItem()) != null) {
                if (item.name().equals(METADATA)) {
                    // check major and minor version (do not allow load newer versions)
                    Properties props = new Properties();
                    props.load(item.stream());
                    version = checkVersion(METADATA_KEY_MODEL_VERSION, props);
                    continue;
                }
                if (item.name().equals(DATA_TRAIN_DIAGRAM)) {
                    LSTrainDiagram lstd = lss.load(item.stream(), LSTrainDiagram.class);
                    builder = new TrainDiagramBuilder(lstd, attachments, getDiagramType(features));
                }
                // test diagram
                if (builder == null) {
                    throw new LSException("Train diagram builder has to be first entry: " + item.name());
                }
                if (item.name().equals(DATA_PENALTY_TABLE)) {
                    builder.setPenaltyTable(lss.load(item.stream(), LSPenaltyTable.class));
                } else if (item.name().equals(DATA_NET)) {
                    builder.setNet(lss.load(item.stream(), LSNet.class));
                } else if (item.name().startsWith(DATA_ROUTES)) {
                    builder.setRoute(lss.load(item.stream(), LSRoute.class));
                } else if (item.name().startsWith(DATA_TRAIN_TYPE_CATEGORIES)) {
                    builder.setTrainTypeCategory(lss.load(item.stream(), LSTrainTypeCategory.class));
                } else if (item.name().startsWith(DATA_TRAIN_TYPES)) {
                    builder.setTrainType(lss.load(item.stream(), LSTrainType.class));
                } else if (item.name().startsWith(DATA_TEXT_ITEMS)) {
                    builder.setTextItem(lss.load(item.stream(), LSTextItem.class));
                } else if (item.name().startsWith(DATA_OUTPUT_TEMPLATES)) {
                    builder.setOutputTemplate(lss.load(item.stream(), LSOutputTemplate.class));
                } else if (item.name().startsWith(DATA_TRAINS)) {
                    builder.setTrain(lss.load(item.stream(), LSTrain.class));
                } else if (item.name().startsWith(DATA_ENGINE_CLASSES)) {
                    builder.setEngineClass(lss.load(item.stream(), LSEngineClass.class));
                } else if (item.name().startsWith(DATA_TRAINS_CYCLES)) {
                    builder.setTrainsCycle(lss.load(item.stream(), LSTrainsCycle.class));
                } else if (item.name().startsWith(DATA_CHANGES)) {
                    builder.setDiagramChangeSet(lss.load(item.stream(), LSDiagramChangeSet.class));
                } else if (item.name().startsWith(FREIGHT_NET)) {
                    builder.setFreightNet(lss.load(item.stream(), LSFreightNet.class));
                } else if (item.name().startsWith(DATA_IMAGES)) {
                    if (item.name().endsWith(".xml")) {
                        builder.addImage(lss.load(item.stream(), LSImage.class));
                    } else {
                        builder.addImageFile(new File(item.name()).getName(), loadImages.loadTimetableImage(item.stream()));
                    }
                } else if (item.name().startsWith(DATA_ATTACHMENTS)) {
                    attachments.load(item);
                } else if (item.name().startsWith(DATA_OUTPUTS)) {
                    builder.setOutput(lss.load(item.stream(), LSOutput.class));
                }
            }
            TrainDiagram trainDiagram = Objects.requireNonNull(builder).getTrainDiagram();
            for (LoadFilter filter : loadFilters) {
                filter.checkDiagram(trainDiagram, version);
            }
            // set file version
            trainDiagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE_VERSION, version);
            source.updateInfo(trainDiagram);
            log.debug("Loaded version: {}", version != null ? version : "<missing>");
            return trainDiagram;
        } catch (IOException e) {
            throw new LSException(e);
        }
    }

    @Override
    public void save(TrainDiagram diagram, LSSink sink) throws LSException {
        try (sink) {
            // save metadata
            this.createMetadata(METADATA_KEY_MODEL_VERSION).store(sink.nextItem(METADATA), null);
            FileLoadSaveAttachments attachments = new FileLoadSaveAttachments(DATA_ATTACHMENTS);

            // increase save version (increment by one)
            diagram.setSaveVersion(diagram.getSaveVersion() + 1);
            // update save timestamp
            diagram.setSaveTimestamp(Instant.now());

            // save train diagram
            this.save(sink, DATA_TRAIN_DIAGRAM, new LSTrainDiagram(diagram));
            // save net
            this.save(sink, DATA_NET, new LSNet(diagram.getNet()));
            int cnt = 0;
            // save train type categories
            for (TrainTypeCategory category : diagram.getTrainTypeCategories()) {
                this.save(sink, this.createEntryName(DATA_TRAIN_TYPE_CATEGORIES, cnt++), new LSTrainTypeCategory(category));
            }
            cnt = 0;
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
            // save text items
            for (TextItem item : diagram.getTextItems()) {
                this.save(sink, this.createEntryName(DATA_TEXT_ITEMS, cnt++), new LSTextItem(item));
            }
            cnt = 0;
            // save output templates
            for (OutputTemplate template : diagram.getOutputTemplates()) {
                LSOutputTemplate lsOutputTemplate = Boolean.TRUE.equals(properties.get(INLINE_ATTACHMENTS)) ?
                        new LSOutputTemplate(template) :
                        new LSOutputTemplate(template, attachments);
                this.save(sink, this.createEntryName(DATA_OUTPUT_TEMPLATES, cnt++), lsOutputTemplate);
            }
            cnt = 0;
            // save diagram change sets
            for (String version : diagram.getChangesTracker().getVersions()) {
                DiagramChangeSet set = diagram.getChangesTracker().getChangeSet(version);
                if (!set.getChanges().isEmpty())
                    this.save(sink, this.createEntryName(DATA_CHANGES, cnt++), new LSDiagramChangeSet(set));
            }
            cnt = 0;
            // save trains cycles
            for (TrainsCycle cycle : diagram.getCycles()) {
                this.save(sink, this.createEntryName(DATA_TRAINS_CYCLES, cnt++), new LSTrainsCycle(cycle));
            }
            cnt = 0;
            // save outputs
            for (Output output : diagram.getOutputs()) {
                this.save(sink, this.createEntryName(DATA_OUTPUTS, cnt++), new LSOutput(output));
            }

            // save images
            cnt = 0;
            FileLoadSaveImages saveImages = new FileLoadSaveImages(DATA_IMAGES);
            for (TimetableImage image : diagram.getImages()) {
                this.save(sink, createEntryName(DATA_IMAGES, cnt++), new LSImage(image));
                saveImages.saveTimetableImage(image, sink);
            }
            // save attachments
            attachments.save(sink);

            // save freight net
            this.save(sink, FREIGHT_NET, new LSFreightNet(diagram.getFreightNet()));

            // update file version
            diagram.getRuntimeInfo().setAttribute(RuntimeInfo.ATTR_FILE_VERSION, this.getSaveVersion());
            sink.updateInfo(diagram);
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

    private TrainDiagramType getDiagramType(LSFeature[] features) {
        return Stream.of(features)
                .filter(feature -> LSFeature.RAW_DIAGRAM == feature)
                .findAny()
                .map(f -> TrainDiagramType.RAW)
                .orElse(TrainDiagramType.NORMAL);
    }
}
