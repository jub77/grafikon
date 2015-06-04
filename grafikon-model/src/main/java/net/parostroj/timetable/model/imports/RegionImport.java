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

    public RegionImport(TrainDiagram diagram, TrainDiagram libraryDiagram, ImportMatch match) {
        super(diagram, libraryDiagram, match);
    }

    @Override
    protected ObjectWithId importObjectImpl(ObjectWithId o) {
        // check class
        if (!(o instanceof Region))
            return null;
        Region importedRegion = (Region)o;

        // check existence
        Region checkedRegion = this.getRegion(importedRegion);
        if (checkedRegion != null) {
            String message = "region already exists";
            this.addError(importedRegion, message);
            log.debug("{}: {}", message, checkedRegion);
            return null;
        }

        // create new region
        Region region = getDiagram().createRegion(this.getId(importedRegion), importedRegion.getName());
        region.setAttributes(this.importAttributes(importedRegion.getAttributes()));

        // add to diagram
        this.getDiagram().getNet().getRegions().add(region);
        this.addImportedObject(region);
        log.trace("Successfully imported region: " + region);
        return region;
    }
}