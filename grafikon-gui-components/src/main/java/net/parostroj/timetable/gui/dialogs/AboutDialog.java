package net.parostroj.timetable.gui.dialogs;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.utils.ManifestVersionInfo.VersionData;
import net.parostroj.timetable.utils.VersionInfo;

/**
 * About dialog.
 *
 * @author jub
 */
public class AboutDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 1L;

	private static final Logger log = LoggerFactory.getLogger(AboutDialog.class);

    private VersionInfo versionInfo;

    public AboutDialog(java.awt.Frame parent, boolean modal, String text, URL imageURL, boolean rotated,
            VersionInfo versionInfo) {
        super(parent, modal);
        this.versionInfo = versionInfo;
        initComponents();

        this.setText(text);
        Image image = null;
        if (!rotated)
            image = Toolkit.getDefaultToolkit().createImage(imageURL);
        else {
            try {
                BufferedImage src = ImageIO.read(imageURL);
                AffineTransform t = AffineTransform.getQuadrantRotateInstance(3);
                t.translate(-src.getWidth(), 0);
                AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                image = op.filter(src, null);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }

        if (image != null) {
            ImageIcon icon = new ImageIcon(image);
            imageLabel.setIcon(icon);
        }
        pack();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    private void initComponents() {
        javax.swing.JTabbedPane tabs = new javax.swing.JTabbedPane();
        getContentPane().add(tabs);

        javax.swing.JPanel tabInfo = new javax.swing.JPanel(new java.awt.BorderLayout(5, 5));
        tabInfo.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabs.add(ResourceLoader.getString("aboutdialog.tab.info"), tabInfo); // NOI18N

        javax.swing.JPanel tabVersions = new javax.swing.JPanel(new java.awt.BorderLayout());
        tabVersions.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        tabs.add(ResourceLoader.getString("aboutdialog.tab.versions"), tabVersions); // NOI18N

        javax.swing.JPanel textPanel = new javax.swing.JPanel();
        javax.swing.JPanel marginPanel = new javax.swing.JPanel();
        textArea = new javax.swing.JTextArea();
        imageLabel = new javax.swing.JLabel();

        setTitle(ResourceLoader.getString("aboutdialog.title")); // NOI18N

        textPanel.setLayout(new java.awt.BorderLayout());

        marginPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        marginPanel.setLayout(new java.awt.BorderLayout());

        textArea.setBackground(textPanel.getBackground());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Dialog", 0, 12));
        textArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        marginPanel.add(textArea, java.awt.BorderLayout.CENTER);

        textPanel.add(marginPanel, java.awt.BorderLayout.CENTER);

        tabInfo.add(textPanel, java.awt.BorderLayout.CENTER);

        imageLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        tabInfo.add(imageLabel, java.awt.BorderLayout.LINE_START);

        javax.swing.JTextArea versionsTextArea = new javax.swing.JTextArea();
        versionsTextArea.setBackground(textPanel.getBackground());
        versionsTextArea.setEditable(false);
        versionsTextArea.setFont(textArea.getFont());
        versionsTextArea.setMargin(new java.awt.Insets(5, 5, 5, 5));

        StringBuilder text = new StringBuilder();
        Map<String, VersionData> versions = versionInfo.getVersions();
        for (VersionData data : versions.values()) {
            text.append(data.getTitle()).append(": ").append(data.getVersion()).append("\n");
        }
        versionsTextArea.setText(text.toString());

        javax.swing.JPanel marginPanel2 = new javax.swing.JPanel();
        marginPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        marginPanel2.setLayout(new java.awt.BorderLayout());

        marginPanel2.add(versionsTextArea, java.awt.BorderLayout.CENTER);
        tabVersions.add(marginPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }

    private javax.swing.JLabel imageLabel;
    private javax.swing.JTextArea textArea;
}
