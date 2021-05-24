package net.parostroj.timetable.output2.net;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.parostroj.timetable.model.Company;
import net.parostroj.timetable.model.FreightColor;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.Region;
import net.parostroj.timetable.output2.util.OutputFreightUtil;

public class NodeToTextBasic implements Function<Node, String> {

    private final boolean html;

    public NodeToTextBasic() {
        html = true;
    }

    @Override
    public String apply(Node node) {
        StringBuilder value = new StringBuilder();
        Company company = node.getCompany();
        List<Region> regions = OutputFreightUtil.sortRegions(node.getRegions(), Locale.getDefault());
        Set<Region> centerRegions = node.getCenterRegions();
        addTextWithColor(value, "black", node.getName());
        if (company != null) {
            value.append(" ");
            addTextWithColor(value, "gray", "[" + company.getAbbr() + "]");
        }
        if (!regions.isEmpty()) {
            String regionsStr = regions.stream()
                    .map(region -> centerRegions.contains(region) ?
                            String.format(getBoldFormat(), region.getName()) : region.getName())
                    .collect(Collectors.joining(","));
            value.append("\n(").append(regionsStr).append(')');
        }
        Collection<FreightColor> colors = OutputFreightUtil.sortFreightColors(node.getFreightColors());
        if (!colors.isEmpty()) {
            String colorsStr = colors.stream().map(FreightColor::getName).collect(Collectors.joining(",", "[", "]"));
            value.append("\n");
            addTextWithColor(value, "gray", colorsStr);
        }
        return value.toString();
    }

    private void addTextWithColor(StringBuilder builder, String color, String text) {
        if (html) {
            builder.append("<font color=").append(color).append(">");
        }
        builder.append(text);
        if (html) {
            builder.append("</font>");
        }
    }

    private String getBoldFormat() {
        return html ? "<b>%s</b>" : "%s";
    }
}
