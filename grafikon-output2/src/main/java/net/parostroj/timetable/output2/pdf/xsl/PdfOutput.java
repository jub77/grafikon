package net.parostroj.timetable.output2.pdf.xsl;

import java.io.*;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.parostroj.timetable.output2.*;
import net.parostroj.timetable.output2.pdf.PdfTransformer;

import org.apache.fop.apps.FormattingResults;
import org.apache.fop.apps.PageSequenceResults;

/**
 * Gsp output.
 *
 * @author jub
 */
public abstract class PdfOutput extends OutputWithLocale {

    public PdfOutput(Locale locale) {
        super(locale);
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

            PdfTransformer pTrans = new PdfTransformer(factory);

            FormattingResults foResults = pTrans.write(stream, new ByteArrayInputStream(bytes));
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
    }
}
