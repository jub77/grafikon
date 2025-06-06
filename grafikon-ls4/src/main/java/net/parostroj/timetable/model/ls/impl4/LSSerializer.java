package net.parostroj.timetable.model.ls.impl4;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import net.parostroj.timetable.model.ls.LSException;

/**
 * LSSerializer for versions 4.x.
 *
 * @author jub
 */
public class LSSerializer {

    public static final boolean FORMATTED = true;

    private static JAXBContext context_i;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;
    private final boolean formatted;

    private synchronized static JAXBContext getContext() throws JAXBException {
        if (context_i == null) {
            context_i = JAXBContext.newInstance(LSTrainDiagram.class, LSNet.class, LSRoute.class,
                    LSTrainType.class, LSTrain.class, LSTrainsCycle.class,
                    LSImage.class, LSTrainsData.class, LSEngineClass.class,
                    LSPenaltyTable.class, LSTextItem.class, LSDiagramChangeSet.class,
                    LSOutputTemplate.class, LSFreightNet.class, LSLibraryItem.class,
                    LSOutput.class, LSTrainTypeCategory.class);
        }
        return context_i;
    }

    public LSSerializer() throws LSException {
        this(FORMATTED);
    }

    public LSSerializer(boolean formatted) throws LSException {
        try {
            JAXBContext context = getContext();
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);
            unmarshaller = context.createUnmarshaller();
            this.formatted = formatted;
        } catch (JAXBException e) {
            throw new LSException("Cannot initialize JAXB context.", e);
        }
    }

    public <T> T load(InputStream in, Class<T> clazz) throws LSException {
        try {
            Reader reader = new NoCloseAllowedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return unmarshaller.unmarshal(new StreamSource(reader), clazz).getValue();
        } catch (JAXBException e) {
            throw new LSException("Cannot load train diagram: JAXB exception.", e);
        }
    }

    public Object load(InputStream in) throws LSException {
        try {
            Reader reader = new NoCloseAllowedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            return unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            throw new LSException("Cannot load train diagram: JAXB exception.", e);
        }
    }

    public <T> void save(OutputStream out, T saved) throws LSException {
        Writer writer;
        try {
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            marshaller.marshal(saved, writer);
        } catch (JAXBException e) {
            throw new LSException("Cannot save train diagram: JAXB exception.", e);
        }
    }

    public boolean isFormatted() {
        return formatted;
    }
}
