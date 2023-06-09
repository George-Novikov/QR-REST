package tools;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRReader {
    private Map<EncodeHintType, ErrorCorrectionLevel> hashMap;
    private static final String charset = "UTF-8";

    public QRReader(){
        hashMap = new HashMap();
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
    }

    public String readQR(BufferedImage bufferedImage) throws NotFoundException {
        BufferedImageLuminanceSource imageSource = new BufferedImageLuminanceSource(bufferedImage);
        HybridBinarizer hybridBinarizer = new HybridBinarizer(imageSource);
        BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);

        MultiFormatReader reader = new MultiFormatReader();
        Result result = reader.decode(binaryBitmap);

        String output = "";
        if (result != null){
            output = result.getText();
        }
        return output;
    }
    public byte[] createQR(String data, int size)
            throws IOException, WriterException {
        String encodedData = new String(data.getBytes(), charset);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(encodedData, BarcodeFormat.QR_CODE, size, size);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] output = baos.toByteArray();
        baos.close();

        return output;
    }
}