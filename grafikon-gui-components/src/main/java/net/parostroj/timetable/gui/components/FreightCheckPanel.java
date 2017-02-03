package net.parostroj.timetable.gui.components;

import static java.util.stream.Collectors.joining;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.utils.GuiComponentUtils;
import net.parostroj.timetable.gui.utils.GuiIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.model.Node;
import net.parostroj.timetable.model.NodeType;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.freight.FreightChecker;
import net.parostroj.timetable.model.freight.FreightChecker.ConnectionState;
import net.parostroj.timetable.model.freight.NodeConnectionEdges;
import net.parostroj.timetable.output2.util.OutputFreightUtil;
import net.parostroj.timetable.output2.util.OutputUtil;

/**
 * Checking if the freight is complete.
 *
 * @author jub
 */
public class FreightCheckPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(FreightCheckPanel.class);

    private final JTextPane textPane;

    private TrainDiagram diagram;

    private Style okStyle;
    private Style errorStyle;
    private Style boldUnderlineStyle;

    private final OutputUtil outputUtil = new OutputUtil();
    private final OutputFreightUtil util = new OutputFreightUtil();
    private final Comparator<String> comparator = outputUtil.getStringComparator(Locale.getDefault());

    private final Set<NodeType> notFreightTypes = NodeType.filteredOf(t -> !t.isFreight());


    public FreightCheckPanel() {
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        add(topPanel, BorderLayout.NORTH);
        FlowLayout topLayout = new FlowLayout();
        topLayout.setAlignment(FlowLayout.LEFT);
        topPanel.setLayout(topLayout);

        JButton refreshButton = GuiComponentUtils.createButton(GuiIcon.REFRESH, 1);
        topPanel.add(refreshButton);

        refreshButton.addActionListener(e -> this.updateView());

        textPane = new JTextPane();
        textPane.setEditable(false);

        JScrollPane scroll = new JScrollPane(textPane);
        scroll.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), scroll.getBorder()));
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(scroll, BorderLayout.CENTER);
    }

    private void initializeStyles() {
        if (okStyle == null && errorStyle == null && textPane.getGraphics() != null) {
            FontMetrics metrics = textPane.getGraphics().getFontMetrics();
            int size = metrics.getAscent() * 5 / 6;

            // images
            StyledDocument document = (StyledDocument) textPane.getDocument();
            okStyle = document.addStyle("ok.style", null);
            ImageIcon okIcon = GuiComponentUtils.resizeIcon(ResourceLoader.createImageIcon(GuiIcon.OK), size, size);
            StyleConstants.setIcon(okStyle, okIcon);
            errorStyle = document.addStyle("error.style", null);
            ImageIcon errorIcon = GuiComponentUtils.resizeIcon(ResourceLoader.createImageIcon(GuiIcon.ERROR), size, size);
            StyleConstants.setIcon(errorStyle, errorIcon);
            StyleConstants.setSubscript(okStyle, true);

            boldUnderlineStyle = document.addStyle("bold.underline", null);
            StyleConstants.setUnderline(boldUnderlineStyle, true);
            StyleConstants.setBold(boldUnderlineStyle, true);
        }
    }

    private void updateView() {
        initializeStyles();
        textPane.setText("");
        if (diagram != null) {
            // check
            FreightChecker checker = new FreightChecker(diagram);
            // check all centers
            appendText(ResourceLoader.getString("freight.check.centers") + ":\n", boldUnderlineStyle);
            diagram.getNet().getNodes().stream().filter(n -> n.isCenterOfRegions()).forEach(n -> {
                Set<Node> noConnToNodes = checker.getNoConnectionsToNodes(n, notFreightTypes);
                Set<Node> noConnToCenter = checker.getNoConnectionToCenter(n, notFreightTypes);
                appendText("-", noConnToCenter.isEmpty() && noConnToNodes.isEmpty() ? okStyle : errorStyle);
                appendText(String.format("  %s\n", nodeWithRegionToString(n)), null);
                for (Node ncNode : noConnToNodes) {
                    appendText(String.format("   %s → %s\n", n.getName(), ncNode.getName()), null);
                }
                for (Node ncNode : noConnToCenter) {
                    appendText(String.format("   %s → %s\n", ncNode.getName(), n.getName()), null);
                }
            });

            appendText(ResourceLoader.getString("freight.check.center.connections") + ":\n", boldUnderlineStyle);
            Map<Node, List<ConnectionState<NodeConnectionEdges>>> centerConnMap =
                    checker.analyseCenterConnections().stream().collect(Collectors.groupingBy(c -> c.getFrom()));

            centerConnMap.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getKey().getName(), comparator))
                .forEach(c -> {
                    boolean exists = c.getValue().stream().allMatch(ConnectionState::exists);
                    appendText("-", exists ? okStyle : errorStyle);
                    appendText(String.format("  %s\n", nodeWithRegionToString(c.getKey())), null);
                    c.getValue().stream().filter(cx -> !cx.exists()).forEach(cx -> {
                            appendText(String.format("   %s → %s\n", cx.getFrom().getName(),
                                    nodeWithRegionToString(cx.getTo())), null);
                        });
                });

            appendText(ResourceLoader.getString("freight.check.node.connections") + ":\n", boldUnderlineStyle);
            checker.analyseNodeConnections(notFreightTypes).stream()
                .filter(c -> !c.exists())
                .sorted(this.compareConnections())
                .forEach(c -> {
                    appendText("-", errorStyle);
                    appendText(String.format("  %s → %s\n", c.getFrom().getName(), c.getTo().getName()), null);
                });

            textPane.setCaretPosition(0);
        }
    }

    private <T> Comparator<ConnectionState<T>> compareConnections() {
        return Comparator
                .comparing((ConnectionState<T> c) -> c.getFrom().getName(), comparator)
                .thenComparing((ConnectionState<T> c) -> c.getTo().getName(), comparator);
    }

    private String nodeWithRegionToString(Node node) {
        return String.format("%s (%s)", node.getName(), util.regionsToString(node.getCenterRegions(), Locale.getDefault()).stream().collect(joining(", ")));
    }

    private void appendText(String text, AttributeSet style) {
        try {
            textPane.getDocument().insertString(textPane.getDocument().getLength(), text, style);
        } catch (BadLocationException e) {
            log.warn("Cannot add text", e);
        }
    }

    public void setDiagram(TrainDiagram diagram) {
        this.diagram = diagram;
        this.textPane.setText("");
    }
}