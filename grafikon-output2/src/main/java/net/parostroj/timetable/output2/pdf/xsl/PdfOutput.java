package net.parostroj.timetable.output2.pdf.xsl;

import java.io.*;
import java.util.Locale;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.parostroj.timetable.output2.*;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.*;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class PdfOutput extends OutputWithLocale {

    public PdfOutput(Locale locale) {
        super(locale);
    }

    protected InputStream getXslStream(OutputParams params, String defaultXsl, ClassLoader classLoader)
            throws IOException {
        if (params.containsKey(DefaultOutputParam.TEMPLATE_STREAM)) {
            return (InputStream) params.getParam(DefaultOutputParam.TEMPLATE_STREAM).getValue();
        } else {
            return getStream(defaultXsl, classLoader);
        }
    }

    private InputStream getStream(String resource, ClassLoader classLoader) {
        if (classLoader != null)
            return classLoader.getResourceAsStream(resource);
        else
            return ClassLoader.getSystemResourceAsStream(resource);
    }

    protected void writeOutput(OutputStream stream, InputStream xsl, InputStream xml)
            throws OutputException {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer t1 = factory.newTransformer(new StreamSource(xsl));
            ByteArrayOutputStream osx = new ByteArrayOutputStream();
            StreamResult r1 = new StreamResult(osx);
            t1.transform(new StreamSource(xml), r1);
            byte[] bytes = osx.toByteArray();

            System.out.println(new String(bytes, "utf-8"));

            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder.build(this.getStream("templates/pdf/fop-cfg.xml", null));
            FopFactory fopFactory = FopFactory.newInstance();

            fopFactory.setUserConfig(cfg);
            FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

            foUserAgent.setURIResolver(new URIResolver() {

                public Source resolve(String href, String base) throws TransformerException {
                    return new StreamSource(ClassLoader.getSystemResourceAsStream(href));
                }
            });

            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, stream);
            Transformer transformer = factory.newTransformer();
            Source src = new StreamSource(new ByteArrayInputStream(bytes));

            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

            FormattingResults foResults = fop.getResults();
            java.util.List<?> pageSequences = foResults.getPageSequences();
            for (java.util.Iterator<?> it = pageSequences.iterator(); it.hasNext();) {
                PageSequenceResults pageSequenceResults = (PageSequenceResults) it.next();
                System.out.println("PageSequence "
                        + (String.valueOf(pageSequenceResults.getID()).length() > 0 ? pageSequenceResults.getID()
                                : "<no id>") + " generated " + pageSequenceResults.getPageCount() + " pages.");
            }
            System.out.println("Generated " + foResults.getPageCount() + " pages in total.");

        } catch (Exception e) {
            throw new OutputException(e);
        }
        // TODO implementation
        // try {
        // } catch (IOException e) {
        // throw new OutputException("Error writing output.", e);
        // }
    }
}
