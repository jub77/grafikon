package net.parostroj.timetable.output2.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.util.ResourceHelper;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;
import org.xml.sax.SAXException;

public class PdfTransformer {

    private final TransformerFactory factory;

    public PdfTransformer(TransformerFactory factory) {
        this.factory = factory;
    }

    public FormattingResults write(OutputStream os, InputStream is) throws OutputException {
        return this.write(os, is, null);
    }

    public FormattingResults write(OutputStream os, InputStream is, URIResolver resolver) throws OutputException {
        FopFactory fopFactory = getFopFactory();
        FOUserAgent foUserAgent = getFoUserAgent(fopFactory, resolver);

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, os);
            Transformer transformer = factory.newTransformer();
            Source src = new StreamSource(is);

            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

            return fop.getResults();
        } catch (FOPException | TransformerException e) {
            throw new OutputException("Error transformation exception", e);
        }
    }

    private FOUserAgent getFoUserAgent(FopFactory fopFactory, URIResolver resolver) {
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        if (resolver != null) {
            foUserAgent.setURIResolver(resolver);
        }
        return foUserAgent;
    }

    private FopFactory getFopFactory() throws OutputException {
        try {
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder.build(ResourceHelper.getStream("templates/pdf/fop-cfg.xml", null));
            FopFactory fopFactory = FopFactory.newInstance();
            fopFactory.setUserConfig(cfg);
            return fopFactory;
        } catch (ConfigurationException | SAXException | IOException e) {
            throw new OutputException("Error creating FOP factory", e);
        }
    }
}
