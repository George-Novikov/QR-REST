package tools;

import org.apache.pdfbox.pdmodel.PDDocument;

import java.awt.image.BufferedImage;
import java.util.List;

public class SubdocumentPDF {
    private String label;
    private PDDocument document;

    public SubdocumentPDF(){}
    public SubdocumentPDF(String label, PDDocument document){
        this.label = label;
        this.document = document;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PDDocument getDocument() {
        return document;
    }

    public void setDocument(PDDocument document) {
        this.document = document;
    }
}
