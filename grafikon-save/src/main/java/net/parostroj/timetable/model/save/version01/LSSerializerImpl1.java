package net.parostroj.timetable.model.save.version01;

import java.io.Reader;
import java.io.Writer;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import net.parostroj.timetable.model.TrainDiagram;
import net.parostroj.timetable.model.TrainDiagramType;
import net.parostroj.timetable.model.ls.LSException;
import net.parostroj.timetable.model.save.LSSerializer;
import net.parostroj.timetable.model.save.LSTrainTypeList;
import net.parostroj.timetable.model.save.NoCloseAllowedReader;

/**
 * Implementation of LSSerializer for version 1.0.
 *
 * @author jub
 */
public class LSSerializerImpl1 extends LSSerializer {

    private static JAXBContext context_i;

    private final Marshaller marshaller;

    private final Unmarshaller unmarshaller;

    private synchronized static JAXBContext getContext() throws JAXBException {
        if (context_i == null)
            context_i = JAXBContext.newInstance(LSTrainDiagram.class);
        return context_i;
    }

    public LSSerializerImpl1() throws LSException {
        try {
            JAXBContext context = getContext();
            marshaller = context.createMarshaller();
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new LSException("Cannot initialize JAXB context.", e);
        }
    }

    @Override
    public TrainDiagram load(Reader reader, LSTrainTypeList trainTypeList, TrainDiagramType diagramType) throws LSException {
        try {
            LSTrainDiagram lsDiagram = (LSTrainDiagram) unmarshaller.unmarshal(new NoCloseAllowedReader(reader));
            LSVisitorBuilder builderVisitor = new LSVisitorBuilder(trainTypeList, diagramType);
            lsDiagram.visit(builderVisitor);
            return builderVisitor.getTrainDiagram();
        } catch (JAXBException e) {
            throw new LSException("Cannot load train diagram.", e);
        }
    }

    @Override
    public void save(Writer writer, TrainDiagram diagram, LSTrainTypeList trainTypeList) throws LSException {
        try {
            LSTransformationData data = new LSTransformationData(trainTypeList);
            LSTrainDiagram lsDiagram = new LSTrainDiagram(diagram, data);
            marshaller.marshal(lsDiagram, writer);
        } catch (JAXBException e) {
            throw new LSException("Cannot save train diagram.", e);
        }
    }
}
