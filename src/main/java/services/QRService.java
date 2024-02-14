package services;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;

@Stateless
public class QRService {
    private static final String DEFAULT_CHARSET = "UTF-8";
    private Hashtable hints;

    public QRService(){
        hints = new Hashtable();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.CHARACTER_SET, DEFAULT_CHARSET);
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

    public byte[] createQR(String data, int size) throws IOException, WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] output = baos.toByteArray();
        baos.close();

        return output;
    }
}