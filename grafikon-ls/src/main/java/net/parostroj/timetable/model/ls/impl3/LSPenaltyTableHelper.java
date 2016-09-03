package net.parostroj.timetable.model.ls.impl3;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.model.LocalizedString;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * This class holds usefull information for computation of running time.
 *
 * @author jub
 */
public class LSPenaltyTableHelper {

    private static final Logger log = LoggerFactory.getLogger(LSPenaltyTableHelper.class);
    /** Filename of the file with information. */
    private static final String SPEEDING_BRAKING_PENALTIES_FILENAME = "/speeding_braking_penalties.xml";

    /**
     * @return penalty table
     */
    public static LSPenaltyTable getLSPenaltyTable() {
        try {
            JAXBContext context = JAXBContext.newInstance(LSPenaltyTable.class);
            Unmarshaller u = context.createUnmarshaller();
            URL url = LSPenaltyTableHelper.class.getResource(SPEEDING_BRAKING_PENALTIES_FILENAME);
            if (url == null) {
                throw new RuntimeException("Cannot find speeding braking table to load.");
            }
            LSPenaltyTable t = u.unmarshal(new javax.xml.transform.stream.StreamSource(url.openStream()), LSPenaltyTable.class).getValue();
            log.trace("Penalty table loaded.");
            return t;
        } catch (Exception e) {
            log.error("Error loading penalty table.", e);
            return null;
        }
    }

    /**
     * fills default values to penalty table.
     *
     * @param categories categories
     */
    public static void fillPenaltyTable(List<TrainTypeCategory> categories) {
        // passenger and freight categories
        TrainTypeCategory pCat = createCategory("passenger");
        TrainTypeCategory fCat = createCategory("freight");
        categories.add(pCat);
        categories.add(fCat);
        for (LSPenaltyTableItem item : getLSPenaltyTable().getItemList()) {
            TrainTypeCategory cat = item.getType() == LSSBType.FREIGHT ? fCat : pCat;
            // upper limit decreased by one - backward compatibility with new implementation
            cat.addRow(new PenaltyTableRow(item.getUpperLimit() - 1, item.getSpeedingPenalty(), item.getBrakingPenalty()));
        }
    }

    private static TrainTypeCategory createCategory(String type) {
        TrainTypeCategory category = new TrainTypeCategory(IdGenerator.getInstance().getId());
        category.setKey(type);
        category.setName(LocalizedString.fromString(type));
        return category;
    }
}
