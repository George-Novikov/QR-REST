package constants;

public enum ServiceMessage {
    IS_ONLINE("QR service is online."),
    OK_MESSAGE("Output is valid."),
    EMPTY_SOURCE("Source file has empty pages."),
    QR_ERROR("QRService exception: "),
    PDF_CONVERSION_ERROR("PDFConverter error: "),
    PDF_READ_ERROR("Error reading PDF: "),
    IMAGE_READ_ERROR("Error reading image: "),
    NULL_IMAGE_BUFFER("Image buffer is null.")
    ;

    private String message;

    ServiceMessage(String message) {
        this.message = message;
    }

    public String get() {
        return message;
    }
}
