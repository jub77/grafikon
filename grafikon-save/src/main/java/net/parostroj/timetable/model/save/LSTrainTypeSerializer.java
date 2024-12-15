package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import java.io.*;
import java.nio.charset.StandardCharsets;

import jakarta.xml.bind.*;

/**
 * Train types serializer.
 *
 * @author jub
 */
public class LSTrainTypeSerializer {

    private static JAXBContext context_i;

    private final Marshaller marshaller;

    private final Unmarshaller unmarshaller;

    private synchronized static JAXBContext getContext() throws JAXBException {
        if (context_i == null)
            context_i = JAXBContext.newInstance(LSTrainTypeList.class);
        return context_i;
    }

    protected LSTrainTypeSerializer() throws LSException {
        try {
            JAXBContext context = getContext();
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new LSException("Cannot initialize JAXB context.", e);
        }
    }

    public static LSTrainTypeSerializer getLSTrainTypeSerializer() throws LSException {
        return new LSTrainTypeSerializer();
    }

    public LSTrainTypeList load(Reader reader) throws LSException {
        try {
            return (LSTrainTypeList) unmarshaller.unmarshal(new NoCloseAllowedReader(reader));
        } catch (JAXBException e) {
            throw new LSException("Cannot load list of train types.", e);
        }
    }

    public void save(Writer writer, LSTrainTypeList trainTypeList) throws LSException {
        try {
            marshaller.marshal(trainTypeList, writer);
        } catch (JAXBException e) {
            throw new LSException("Cannot save list of train types.", e);
        }
    }

    public void save(OutputStream os, LSTrainTypeList trainTypeList) throws LSException {
        this.save(new OutputStreamWriter(os, StandardCharsets.UTF_8), trainTypeList);
    }
}
