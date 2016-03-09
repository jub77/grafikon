package net.parostroj.timetable.gui.components;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

import javax.swing.*;

import net.parostroj.timetable.gui.utils.ResourceLoader;

/**
 * Search dialog for editing text.
 *
 * @author jub
 */
public class SearchDialog extends JDialog {

    private static final int DEFAULT_TEXT_FIELD_WIDTH = 25;

    private JTextField searchTextField;

    private Consumer<String> searchFunction;

    public SearchDialog(Window window, boolean modal) {
        super(window, ResourceLoader.getString("search.title"), modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
        flowLayout.setAlignment(FlowLayout.RIGHT);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        JButton searchButton = new JButton(ResourceLoader.getString("search.button"));
        ActionListener searchAction = e -> {
            if (searchFunction != null) {
                String text = searchTextField.getText();
                if (text != null) {
                    searchFunction.accept(text);
                }
            }
        };
        searchButton.addActionListener(searchAction);
        buttonPanel.add(searchButton);

        searchTextField = new JTextField();
        panel.add(searchTextField);
        searchTextField.setColumns(DEFAULT_TEXT_FIELD_WIDTH);
        searchTextField.addActionListener(searchAction);

        this.pack();
    }

    public void setSearchFunction(Consumer<String> searchFunction) {
        this.searchFunction = searchFunction;
    }

    @Override
    public void requestFocus() {
        super.requestFocus();
        searchTextField.requestFocusInWindow();
    }

    @Override
    protected JRootPane createRootPane() {
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane rootPane = new JRootPane();
        rootPane.registerKeyboardAction(e -> this.setVisible(false), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        return rootPane;
    }
}
