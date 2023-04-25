package tools;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

        return bufferedImages;
    }
}