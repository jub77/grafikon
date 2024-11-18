package net.parostroj.timetable.model.imports;

import net.parostroj.timetable.model.ObjectWithId;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.Region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Imports regions.
 *
 * @author jub
 */
public class RegionImport extends Import {

    private static final Logger log = LoggerFactory.getLogger(RegionImport.class);

    public RegionImport(TrainDiagram diagram, ImportMatch match, boolean overwrite) {
        super(diagram, match, overwrite);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof Region importedRegion)) {
            return null;
        }

        // check existence
        Region checkedRegion = this.getRegion(importedRegion);
        if (checkedRegion != null) {
            if (overwrite) {
                this.getDiagram().getNet().getRegions().remove(checkedRegion);
            } else {
                String message = "region already exists";
                this.addError(importedRegion, message);
                log.debug("{}: {}", message, checkedRegion);
                return null;
            }
        }

        // create new region
        Region region = getDiagram().getPartFactory().createRegion(this.getId(importedRegion));
        region.getAttributes().add(this.importAttributes(importedRegion.getAttributes()));

        // add to diagram
        this.getDiagram().getNet().getRegions().add(region);
        this.addImportedObject(region);
        log.trace("Successfully imported region: {}", region);
        return region;
    }
}
