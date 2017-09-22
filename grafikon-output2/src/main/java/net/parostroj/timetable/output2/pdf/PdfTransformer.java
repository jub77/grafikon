package net.parostroj.timetable.output2.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.*;
import org.apache.xmlgraphics.io.Resource;
import org.apache.xmlgraphics.io.ResourceResolver;
import org.xml.sax.SAXException;

import net.parostroj.timetable.output2.OutputException;
import net.parostroj.timetable.output2.util.ResourceHelper;

public class PdfTransformer {

    private final TransformerFactory factory;

    public PdfTransformer(TransformerFactory factory) {
        this.factory = factory;
    }

    public FormattingResults write(OutputStream os, InputStream is) throws OutputException {
        return this.write(os, is, null);
    }

    public FormattingResults write(OutputStream os, InputStream is, URIResolver resolver) throws OutputException {
        FopFactory fopFactory = getFopFactory(resolver);

        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, fopFactory.newFOUserAgent(), os);
            Transformer transformer = factory.newTransformer();
            Source src = new StreamSource(is);

            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

            return fop.getResults();
        } catch (FOPException | TransformerException e) {
            throw new OutputException("Error transformation exception", e);
        }
    }

    public static FopFactory createFopFactory() throws OutputException {
        return getFopFactory(null);
    }

    private static FopFactory getFopFactory(URIResolver resolver) throws OutputException {
        try (InputStream fopCfgStream = ResourceHelper.getStream("fop/fop-cfg.xml",
                PdfTransformer.class.getClassLoader())) {
            // base uri is empty
            URI baseURI = new URI("");
            FopConfParser parser = new FopConfParser(
                    fopCfgStream,
                    EnvironmentalProfileFactory.createRestrictedIO(baseURI, convertResolver(resolver)));
            FopFactory fopFactory = parser.getFopFactoryBuilder().build();
            fopFactory.getFontManager().disableFontCache();
            return fopFactory;
        } catch (IOException | SAXException | URISyntaxException e) {
            throw new OutputException("Error creating FOP factory", e);
        }
    }

    private static ResourceResolver convertResolver(URIResolver uriResolver) {
        return new ResourceResolver() {
            @Override
            public Resource getResource(URI uri) throws IOException {
                String asciiUri = uri.toASCIIString();
                InputStream is = PdfTransformer.class.getClassLoader().getResourceAsStream(asciiUri);
                if (is == null && uriResolver != null) {
                    try {
                        StreamSource streamSource = (StreamSource) uriResolver.resolve(asciiUri, null);
                        if (streamSource != null) {
                            is = streamSource.getInputStream();
                        }
                    } catch (TransformerException e) {
                        throw new IOException(e);
                    }
                }
                return is == null ? null : new Resource(is);
            }

            @Override
            public OutputStream getOutputStream(URI uri) throws IOException {
                return null;
            }
        };
    }
}
