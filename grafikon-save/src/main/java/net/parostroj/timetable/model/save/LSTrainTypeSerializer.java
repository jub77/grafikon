package net.parostroj.timetable.model.save;

import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.ls.ModelVersion;
import java.io.*;
import javax.xml.bind.*;

/**
 * Train types serializer.
 * 
 * @author jub
 */
public class LSTrainTypeSerializer {
    
    private static JAXBContext context_i;

    private Marshaller marshaller;

    private Unmarshaller unmarshaller;
    
    private synchronized static JAXBContext getContext() throws JAXBException {
        if (context_i == null)
            context_i = JAXBContext.newInstance(new Class[]{LSTrainTypeList.class});
        return context_i;
    }

    protected LSTrainTypeSerializer(ModelVersion version) throws LSException {
        try {
            JAXBContext context = getContext();
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new LSException("Cannot initialize JAXB context.", e);
        }
    }
    
    public static LSTrainTypeSerializer getLSTrainTypeSerializer(ModelVersion version) throws LSException {
        return new LSTrainTypeSerializer(version);
    }

    public LSTrainTypeList load(Reader reader) throws LSException {
        try {
            LSTrainTypeList list = (LSTrainTypeList) unmarshaller.unmarshal(new NoCloseAllowedReader(reader));
            return list;
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
        try {
            this.save(new OutputStreamWriter(os, "utf-8"), trainTypeList);
        } catch (UnsupportedEncodingException e) {
            throw new LSException("Cannot save list of train types.", e);
        }
    }
}
