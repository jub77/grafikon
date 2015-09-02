package net.parostroj.timetable.output2.impl;

import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.google.common.collect.Iterables;

import net.parostroj.timetable.actions.FreightHelper;
import net.parostroj.timetable.actions.TextList;
import net.parostroj.timetable.model.*;
import net.parostroj.timetable.utils.ObjectsUtil;

/**
 * Freight destination information.
 *
 * @author jub
 */
@XmlType(propOrder = {"name", "abbr", "region", "colors"})
public class FreightDstInfo {

    private String name;
    private String abbr;
    private String region;
    private List<FreightColor> colors;
    private Boolean hidden;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getHidden() {
        return hidden;
    }

    @XmlAttribute
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @XmlJavaTypeAdapter(FreightColorAdapter.class)
    public List<FreightColor> getColors() {
        return colors;
    }

    @XmlElement(name = "color")
    public void setColors(List<FreightColor> colors) {
        this.colors = colors;
    }

    public String toString(Locale locale, boolean abbreviation) {
        StringBuilder freighStr = new StringBuilder();
        if (name != null) {
            StringBuilder colorsStr = null;
            if (colors != null && !colors.isEmpty()) {
                colorsStr = new StringBuilder();
                TextList o = new TextList(colorsStr, "[", "]", ",");
                o.addItems(Iterables.filter(colors, FreightColor.class), FreightHelper.colorToString(locale));
                o.finish();
            }
            if (!Boolean.TRUE.equals(hidden) || colorsStr == null) {
                freighStr.append(abbreviation ? abbr : name);
            }
            if (colorsStr != null) {
                freighStr.append(colorsStr.toString());
            }
        } else {
            freighStr.append(region);
        }
        return freighStr.toString();
    }

    public static FreightDstInfo convert(FreightDst dst) {
        FreightDstInfo info = new FreightDstInfo();
        if (dst.getNode() != null) {
            info.setName(dst.getNode().getName());
            info.setAbbr(dst.getNode().getAbbr());
            List<?> lColors = dst.getNode().getAttribute(Node.ATTR_FREIGHT_COLORS, List.class);
            info.setColors(ObjectsUtil.copyToList(lColors, FreightColor.class));
            if (dst.getNode().getType() == NodeType.STATION_HIDDEN) {
                info.setHidden(true);
            }
        }
        if (dst.getRegion() != null) {
            info.setRegion(dst.getRegion().getName());
        }
        return info;
    }
}
