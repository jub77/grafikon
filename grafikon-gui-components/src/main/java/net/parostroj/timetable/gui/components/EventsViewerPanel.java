package net.parostroj.timetable.gui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import javax.swing.AbstractListModel;
import javax.swing.Timer;

import net.parostroj.timetable.gui.utils.ResourceLoader;
import net.parostroj.timetable.utils.Triplet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Events viewer panel.
 *
 * @author jub
 */
public class EventsViewerPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(EventsViewerPanel.class);
    private static final int TIMEOUT = 100;


    private final Map<Class<?>, EventsViewerTypeConverter> converterMap;
    private boolean writeToLog = false;
    private final Timer timer;

    class EventListModel extends AbstractListModel<Object> {

        private static final long serialVersionUID = 1L;

        private final List<Triplet<Object, String, String>> eventList = new ArrayList<Triplet<Object, String, String>>();
        private int limit = 0;
        private boolean showTime = false;

        public void addEvent(Object event) {
            Calendar c = Calendar.getInstance();
            String time = String.format("%02d:%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
            String str = this.convertEventToStr(event);
            eventList.add(new Triplet<Object, String, String>(event, str, time));
            int position = eventList.size() - 1;
            fireIntervalAdded(this, position, position);
            removeOverLimit();
            if (writeToLog)
                log.debug(str);
        }

        @Override
        public int getSize() {
            return eventList.size();
        }

        public void clear() {
            int size = eventList.size();
            if (size > 0) {
                eventList.clear();
                this.fireIntervalRemoved(this, 0, size - 1);
            }
        }

        public void setLimit(int limit) {
            this.limit = limit;
            removeOverLimit();
        }

        public int getLimit() {
            return limit;
        }

        public boolean isShowTime() {
            return showTime;
        }

        public void setShowTime(boolean showTime) {
            if (this.showTime != showTime) {
                this.showTime = showTime;
                if (eventList.size() > 0)
                    this.fireContentsChanged(this, 0, eventList.size() - 1);
            }
        }

        private void removeOverLimit() {
            if (limit > 0) {
                if (eventList.size() > limit) {
                    int removedSize = eventList.size() - limit;
                    eventList.subList(0, removedSize).clear();
                    this.fireIntervalRemoved(this, 0, removedSize - 1);
                }
            }
        }

        @Override
        public Object getElementAt(int index) {
            Triplet<Object, String, String> item = eventList.get(index);
            return !showTime ?
                item.second :
                item.third + " " + item.second;
        }

        public Triplet<Object, String, String> getEventTriplet(int index) {
            return eventList.get(index);
        }

        private String convertEventToStr(Object event) {
            EventsViewerTypeConverter converter = getConverterForType(event.getClass());
            return converter != null ? converter.getListString(event) : event.toString();
        }
    }

    /** Creates new form EventsViewerPanel */
    public EventsViewerPanel() {
        initComponents();
        converterMap = new HashMap<Class<?>, EventsViewerTypeConverter>();
        timer = new Timer(TIMEOUT, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrollToLastPosition();
            }
        });
        timer.setRepeats(false);
    }

    private void initComponents() {
        splitPane = new javax.swing.JSplitPane();
        scrollPane1 = new javax.swing.JScrollPane();
        eventsList = new javax.swing.JList<Object>();
        scrollPane2 = new javax.swing.JScrollPane();
        eventTextArea = new javax.swing.JTextArea();
        buttonsPanel = new javax.swing.JPanel();
        clearButton = new javax.swing.JButton();
        limitTextField = new javax.swing.JFormattedTextField();
        limitButton = new javax.swing.JButton();
        timeCheckBox = new javax.swing.JCheckBox();
        writeLogCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.BorderLayout());

        eventsList.setModel(new EventListModel());
        eventsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        eventsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                eventsListValueChanged(evt);
            }
        });
        scrollPane1.setViewportView(eventsList);

        splitPane.setLeftComponent(scrollPane1);

        eventTextArea.setColumns(20);
        eventTextArea.setFont(eventTextArea.getFont().deriveFont((float)11));
        eventTextArea.setLineWrap(true);
        eventTextArea.setRows(5);
        scrollPane2.setViewportView(eventTextArea);

        splitPane.setRightComponent(scrollPane2);

        add(splitPane, java.awt.BorderLayout.CENTER);

        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        clearButton.setText(ResourceLoader.getString("eventsviewer.button.clear")); // NOI18N
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(clearButton);

        limitTextField.setColumns(4);
        limitTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        limitTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        limitTextField.setText("0");
        buttonsPanel.add(limitTextField);

        limitButton.setText(ResourceLoader.getString("eventsviewer.button.limit")); // NOI18N
        limitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limitButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(limitButton);

        timeCheckBox.setText(ResourceLoader.getString("eventsviewer.showtime")); // NOI18N
        timeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeCheckBoxActionPerformed(evt);
            }
        });
        buttonsPanel.add(timeCheckBox);

        writeLogCheckBox.setText(ResourceLoader.getString("eventsviewer.writetolog")); // NOI18N
        writeLogCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeLogCheckBoxActionPerformed(evt);
            }
        });
        buttonsPanel.add(writeLogCheckBox);

        add(buttonsPanel, java.awt.BorderLayout.PAGE_END);
    }

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {
        clearEvents();
    }

    private void limitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Long value = (Long)limitTextField.getValue();
        setModelLimit(value.intValue());
    }

    private void timeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        getEventsModel().setShowTime(timeCheckBox.isSelected());
    }

    private void writeLogCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (writeToLog != writeLogCheckBox.isSelected()) {
            writeToLog = writeLogCheckBox.isSelected();
        }
    }

    private void eventsListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        // show selected
        if (evt.getValueIsAdjusting())
            return;
        int index = eventsList.getSelectedIndex();
        if (index != -1) {
            Triplet<Object, String, String> event = this.getEventsModel().getEventTriplet(index);
            EventsViewerTypeConverter converter = this.getConverterForType(event.first.getClass());
            String viewString = converter != null ? converter.getViewString(event.first) : event.first.toString();
            eventTextArea.setText(viewString);
        } else
            eventTextArea.setText("");

    }

    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JTextArea eventTextArea;
    private javax.swing.JList<Object> eventsList;
    private javax.swing.JButton limitButton;
    private javax.swing.JFormattedTextField limitTextField;
    private javax.swing.JScrollPane scrollPane1;
    private javax.swing.JScrollPane scrollPane2;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JCheckBox timeCheckBox;
    private javax.swing.JCheckBox writeLogCheckBox;

    public void setDividerLocation(int value) {
        splitPane.setDividerLocation(value);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }

    public void addEvent(Object event) {
        getEventsModel().addEvent(event);
        timer.stop();
        timer.setInitialDelay(TIMEOUT);
        timer.start();
    }

    private void scrollToLastPosition() {
        eventsList.ensureIndexIsVisible(getEventsModel().getSize() - 1);
    }

    public void clearEvents() {
        getEventsModel().clear();
    }

    public void setLimit(int limit) {
        limitTextField.setText(Integer.toString(limit));
        this.setModelLimit(limit);
    }

    public int getLimit() {
        return getEventsModel().getLimit();
    }

    public boolean isShowTime() {
        return getEventsModel().isShowTime();
    }

    public void setShowTime(boolean showTime) {
        timeCheckBox.setSelected(showTime);
        getEventsModel().setShowTime(showTime);
    }

    public boolean isWriteToLog() {
        return writeToLog;
    }

    public void setWriteToLog(boolean writeToLog) {
        writeLogCheckBox.setSelected(writeToLog);
        this.writeToLog = writeToLog;
    }

    private void setModelLimit(int limit) {
        getEventsModel().setLimit(limit);
    }

    private EventListModel getEventsModel() {
        return (EventListModel)eventsList.getModel();
    }

    public void addConverter(EventsViewerTypeConverter converter) {
        converterMap.put(converter.getEventClass(), converter);
    }

    public void removeConverter(Class<?> clazz) {
        converterMap.remove(clazz);
    }

    private EventsViewerTypeConverter getConverterForType(Class<?> clazz) {
        while (clazz != null) {
            EventsViewerTypeConverter converter = converterMap.get(clazz);
            if (converter != null) {
                return converter;
            } else {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
