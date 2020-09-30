package net.parostroj.timetable.output2.impl;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.actions.TextList;
import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.model.freight.FreightConnection;
import net.parostroj.timetable.output2.util.OutputFreightUtil;

/**
 * Freight destination information.
 *
 * @author jub
 */
@XmlType(propOrder = {"name", "abbr", "regions", "colors", "hidden", "capacity"})
public class FreightDestinationInfo {

    private String name;
    private String abbr;
    private List<String> regions;
    private List<FreightColor> colors;
    private Boolean hidden;
    private Integer capacity;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @XmlElement(name = "region")
    public List<String> getRegions() {
        return regions;
    }

    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    @XmlJavaTypeAdapter(FreightColorAdapter.class)
    public List<FreightColor> getColors() {
        return colors;
    }

    @XmlElement(name = "color")
    public void setColors(List<FreightColor> colors) {
        this.colors = colors;
    }

    @XmlAttribute
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    @XmlTransient
    public boolean isCenter() {
        return regions != null && !regions.isEmpty();
    }

    public String toString(Locale locale, boolean abbreviation) {
        StringBuilder freightStr = new StringBuilder();
        StringBuilder colorsStr = null;
        if (colors != null && !colors.isEmpty()) {
            colorsStr = new StringBuilder();
            TextList o = new TextList(colorsStr, "[", "]", ",");
            o.addItems(Iterables.filter(colors, FreightColor.class), color -> color.getName(locale));
            o.finish();
        }
        if (!Boolean.TRUE.equals(hidden) || colorsStr == null) {
            freightStr.append(abbreviation ? abbr : name);
        }
        if (colorsStr != null) {
            freightStr.append(colorsStr.toString());
        }
        return freightStr.toString();
    }

    public String toRegionsString(Locale locale) {
        if (!isCenter()) {
            return "";
        }
        StringBuilder regionsStr = new StringBuilder();
        new TextList(regionsStr, ",").addItems(this.regions).finish();
        return regionsStr.toString();
    }

    public static FreightDestinationInfo convert(Locale locale, FreightConnection dst) {
        FreightDestinationInfo info = new FreightDestinationInfo();
        if (dst.getTo().isNode()) {
            Node to = dst.getTo().getNode();
            info.setName(to.getName());
            info.setAbbr(to.getAbbr());
            List<FreightColor> sortedColors = OutputFreightUtil.sortFreightColors(dst.getTo().getFreightColors());
            info.setColors(sortedColors == null || sortedColors.isEmpty() ? null : sortedColors);
            if (!dst.getTo().isVisible()) {
                info.setHidden(true);
            }
            if (to.getFreightCapacity() != null) {
                info.setCapacity(to.getFreightCapacity());
            }
        }
        if (dst.getTo().isRegions()) {
            Set<Region> regions = dst.getTo().getRegions();
            info.setRegions(new OutputFreightUtil().regionsToString(regions, locale));
        }
        return info;
    }
}
