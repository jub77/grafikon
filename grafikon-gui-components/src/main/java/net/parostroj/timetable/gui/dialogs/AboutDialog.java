package net.parostroj.timetable.gui.dialogs;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.parostroj.timetable.gui.utils.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * About dialog.
 *
 * @author jub
 */
public class AboutDialog extends javax.swing.JDialog {

    private static final Logger LOG = LoggerFactory.getLogger(AboutDialog.class.getName());

    /** Creates new form AboutDialog */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public AboutDialog(java.awt.Frame parent, boolean modal, String text, Image image) {
        super(parent, modal);
        initComponents();

        this.setText(text);
        ImageIcon icon = new ImageIcon(image);
        imageLabel.setIcon(icon);
        pack();
    }

    public AboutDialog(java.awt.Frame parent, boolean modal, String text, URL imageURL, boolean rotated) {
        super(parent, modal);
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
                LOG.warn(e.getMessage(), e);
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
        javax.swing.JPanel textPanel = new javax.swing.JPanel();
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JPanel marginPanel = new javax.swing.JPanel();
        textArea = new javax.swing.JTextArea();
        imageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(ResourceLoader.getString("aboutdialog.title")); // NOI18N

        textPanel.setLayout(new java.awt.BorderLayout());

        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(ResourceLoader.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(okButton);

        textPanel.add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        marginPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        marginPanel.setLayout(new java.awt.BorderLayout());

        textArea.setBackground(textPanel.getBackground());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Dialog", 0, 12));
        textArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        marginPanel.add(textArea, java.awt.BorderLayout.CENTER);

        textPanel.add(marginPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(textPanel, java.awt.BorderLayout.CENTER);

        imageLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(imageLabel, java.awt.BorderLayout.LINE_START);

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // close dialog
        this.setVisible(false);
    }

    private javax.swing.JLabel imageLabel;
    private javax.swing.JTextArea textArea;
}
