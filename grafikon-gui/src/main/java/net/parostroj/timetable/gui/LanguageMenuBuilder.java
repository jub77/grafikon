package net.parostroj.timetable.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JRadioButtonMenuItem;
import net.parostroj.timetable.gui.actions.ModelUtils;

/**
 * Creates menu with languages.
 * 
 * @author jub
 */
public class LanguageMenuBuilder {

    public static class LanguageMenuItem extends JRadioButtonMenuItem {

        private Locale language;

        public LanguageMenuItem(String text, Locale language) {
            super(text);
            this.language = language;
        }

        public Locale getLanguage() {
            return language;
        }

        public void setLanguage(Locale language) {
            this.language = language;
        }
    }

    public List<LanguageMenuItem> createLanguageMenuItems() {
        try {
            List<LanguageMenuItem> languages = new LinkedList<LanguageMenuItem>();
            
            // load propertis
            Properties langProps = new Properties();
            InputStream stream = LanguageMenuBuilder.class.getResourceAsStream("/languages.properties");
            try {
                langProps.load(stream);
            } finally {
                stream.close();
            }
            for (Map.Entry<Object,Object> entry : langProps.entrySet()) {
                Locale language = ModelUtils.parseLocale((String)entry.getKey());
                String text = (String)entry.getValue();
                languages.add(new LanguageMenuItem(text, language));
            }

            return this.sort(languages);
        } catch (IOException ex) {
            Logger.getLogger(LanguageMenuBuilder.class.getName()).log(Level.WARNING, "Cannot find languages property file.", ex);
            return Collections.emptyList();
        }
    }
    
    private List<LanguageMenuItem> sort(List<LanguageMenuItem> items) {
        Collections.sort(items, new Comparator<LanguageMenuItem>() {
            @Override
            public int compare(LanguageMenuItem o1, LanguageMenuItem o2) {
                return o1.getLanguage().toString().compareTo(o2.getLanguage().toString());
            }
        });
        return items;
    }
}