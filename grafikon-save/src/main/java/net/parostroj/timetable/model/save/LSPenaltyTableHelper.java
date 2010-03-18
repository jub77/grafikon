package net.parostroj.timetable.model.save;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import net.parostroj.timetable.model.PenaltyTable;
import net.parostroj.timetable.model.PenaltyTableRow;
import net.parostroj.timetable.model.TrainTypeCategory;
import net.parostroj.timetable.utils.IdGenerator;

/**
 * This class holds usefull information for computation of running time.
 *
 * @author jub
 */
public class LSPenaltyTableHelper {

    private static final Logger LOG = Logger.getLogger(LSPenaltyTableHelper.class.getName());
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
            LOG.finer("Penalty table loaded.");
            return t;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Error loading penalty table.", e);
            return null;
        }
    }

    /**
     * fills default values to penalty table.
     *
     * @param pTable penalty table
     */
    public static void fillPenaltyTable(PenaltyTable pTable) {
        // passenger and freight categories
        TrainTypeCategory pCat = new TrainTypeCategory(IdGenerator.getInstance().getId(), "passenger", "passenger");
        TrainTypeCategory fCat = new TrainTypeCategory(IdGenerator.getInstance().getId(), "freight", "freight");
        pTable.addTrainTypeCategory(pCat);
        pTable.addTrainTypeCategory(fCat);
        for (LSPenaltyTableItem item : getLSPenaltyTable().getItemList()) {
            TrainTypeCategory cat = item.getType() == LSSBType.FREIGHT ? fCat : pCat;
            // upper limit decreased by one - backward compatibility with new implementation
            pTable.addRowForCategory(cat, new PenaltyTableRow(item.getUpperLimit() - 1, item.getSpeedingPenalty(), item.getBrakingPenalty()));
        }
    }
}
