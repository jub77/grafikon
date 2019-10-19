package net.parostroj.timetable.model.ls.impl3;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import net.parostroj.timetable.model.ls.LSException;

/**
 * LSSerializer for version 3.0.
 * 
 * @author jub
 */
public class LSSerializer {
    
    public static final boolean FORMATTED = true;

    private static JAXBContext context_i;
    private Marshaller marshaller;
    private Unmarshaller unmarshaller;
    private final boolean formatted;

    private synchronized static JAXBContext getContext() throws JAXBException {
        if (context_i == null) {
            context_i = JAXBContext.newInstance(new Class[]{
                LSTrainDiagram.class, LSNet.class, LSRoute.class,
                LSTrainType.class, LSTrain.class, LSTrainsCycle.class,
                LSImage.class, LSTrainsData.class, LSEngineClass.class
            });
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
            Reader reader = new NoCloseAllowedReader(new InputStreamReader(in, "utf-8"));
            return unmarshaller.unmarshal(new StreamSource(reader), clazz).getValue();
        } catch (UnsupportedEncodingException e) {
            throw new LSException("Cannot load train diagram: Unsupported enconding.", e);
        } catch (JAXBException e) {
            throw new LSException("Cannot load train diagram: JAXB exception.", e);
        }
    }

    public Object load(InputStream in) throws LSException {
        try {
            Reader reader = new NoCloseAllowedReader(new InputStreamReader(in, "utf-8"));
            return unmarshaller.unmarshal(reader);
        } catch (UnsupportedEncodingException e) {
            throw new LSException("Cannot load train diagram: Unsupported enconding.", e);
        } catch (JAXBException e) {
            throw new LSException("Cannot load train diagram: JAXB exception.", e);
        }
    }

    public <T> void save(OutputStream out, T saved) throws LSException {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(out, "utf-8");
            marshaller.marshal(saved, writer);
        } catch (UnsupportedEncodingException e) {
            throw new LSException("Cannot save train diagram: Unsupported enconding.", e);
        } catch (JAXBException e) {
            throw new LSException("Cannot save train diagram: JAXB exception.", e);
        }
    }

    public boolean isFormatted() {
        return formatted;
    }
}
