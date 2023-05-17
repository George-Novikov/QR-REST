package tools;

import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.scenario.effect.impl.prism.ps.PPSDisplacementMapPeer;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
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

    public static PDDocument getSubdocument(InputStream inStream, int start, int end) throws Exception{
        PDDocument document = PDDocument.load(inStream);
        PDDocument subdocument = new PDDocument();

        for (int i = start; i <= end; i++){
            PDPage page = document.getPage(i);
            subdocument.addPage(page);
        }

        return subdocument;
    }

    public static List<Object> mapSubdocuments(InputStream inStream) throws Exception{
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

        document.close();

        return createMap(qrLabels, qrIndexes);
    }

    private static List<Object> createMap(List<String> labels, List<Integer> qrIndexes){
        Object[] labelArray = labels.toArray();
        Object[] indexArray = qrIndexes.toArray();

        List<Object> stringSubdocumentMap = new ArrayList<>();

        for (int i = 0; i < labelArray.length; i++){
            String label = (String) labelArray[i];
            int start = (Integer) indexArray[i];
            int end;
            if (i+1 <= labelArray.length){
                end = (Integer) indexArray[i+1];
                SubdocumentEntry entry = new SubdocumentEntry(label, start, end-1);
                stringSubdocumentMap.add(entry);
            }
        }

        return stringSubdocumentMap;
    }

    public static int measureLength(BufferedImage bufferedImage) throws IOException{
        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", bufferStream);
        bufferStream.close();
        return bufferStream.size();
    }

    public static byte[] toByteArray(PDDocument document) throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }

    public static PDDocument createEmpty() throws Exception{
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        PDPageContentStream pageStream = new PDPageContentStream(document, page);
        pageStream.beginText();
        pageStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        pageStream.newLineAtOffset(25, 500);
        pageStream.showText("Empty output. No subdocument.");
        pageStream.endText();
        pageStream.close();
        document.close();
        return document;
    }
}