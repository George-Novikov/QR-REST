package tools;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.*;

public class PDFConverter {
    public static List<BufferedImage> pdfToImage(InputStream inStream) throws IOException {
        PDDocument document = PDDocument.load(inStream);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        int pagesNumber = document.getNumberOfPages();

        List<BufferedImage> bufferedImages = new ArrayList();
        int dpi = 300;
        for (int i = 0; i < pagesNumber; i++){
            BufferedImage bufferedPage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);
            bufferedImages.add(bufferedPage);
        }
        document.close();

        return bufferedImages;
    }

    public static List<String> mapSubdocuments(InputStream inStream) throws Exception{
        PDDocument document = PDDocument.load(inStream);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        int pagesNumber = document.getNumberOfPages();

        List<Integer> qrIndexes = new ArrayList<>();
        List<String> qrLabels = new ArrayList<>();
        int dpi = 300;
        for (int i = 0; i < pagesNumber; i++){
            BufferedImage bufferedPage = pdfRenderer.renderImageWithDPI(i, dpi, ImageType.RGB);

            String textContent = "";
            try {
                textContent = new QRReader().readQR(bufferedPage);
            } catch (NotFoundException e){}

            if (textContent != ""){
                qrIndexes.add(i);
                qrLabels.add(textContent);
            }
        }
        qrIndexes.add(pagesNumber);
        //List<PDDocument> subdocuments = divideDocument(document, qrIndexes);

        document.close();

        return createMap(qrLabels, qrIndexes);
    }

    private static List<PDDocument> divideDocument(PDDocument document, List<Integer> qrIndexes) throws Exception{
        Object[] indexes = qrIndexes.toArray();
        List<PDDocument> subdocuments = new ArrayList<>();
        List<String> stringSubdocumentMap = new ArrayList<>();

        for (int i = 0; i < indexes.length; i++){
            int start = (int) indexes[i];
            int end = (int) indexes[i+1];

            PDDocument subdocument = createSubdocument(document, start, end);
            subdocuments.add(subdocument);
            stringSubdocumentMap.add(subdocument.toString());

            subdocument.close();
        }
        return subdocuments;
    }

    private static PDDocument createSubdocument(PDDocument document, int startPage, int endPage) throws Exception{
        PDDocument subDocument = new PDDocument();
        for (int i = startPage; i < endPage; i++){
            PDPage page = document.getPage(i);
            subDocument.addPage(page);
        }
        subDocument.close();
        return subDocument;
    }

    private static List<String> createMap(List<String> labels, List<Integer> qrIndexes){
        Object[] labelArray = labels.toArray();
        Object[] indexArray = qrIndexes.toArray();

        SubdocumentEntry[] subdocumentsMap = new SubdocumentEntry[labelArray.length];
        List<String> stringSubdocumentMap = new ArrayList<>();

        for (int i = 0; i < labelArray.length; i++){
            SubdocumentEntry entry = new SubdocumentEntry((String) labelArray[i], (Integer) indexArray[i]);
            stringSubdocumentMap.add(entry.toJSON());
        }

        return stringSubdocumentMap;
    }

    public static int measureLength(BufferedImage bufferedImage) throws IOException{
        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", bufferStream);
        bufferStream.close();
        return bufferStream.size();
    }
}