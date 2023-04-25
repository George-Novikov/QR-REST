package tools;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import java.awt.image.BufferedImage;
import java.util.Map;

public class QRReader {
    public static String readQR(BufferedImage bufferedImage, String charset, Map hashMap) throws NotFoundException {
        BufferedImageLuminanceSource imageSource = new BufferedImageLuminanceSource(bufferedImage);
        HybridBinarizer hybridBinarizer = new HybridBinarizer(imageSource);
        BinaryBitmap binaryBitmap = new BinaryBitmap(hybridBinarizer);

        MultiFormatReader reader = new MultiFormatReader();
        Result result = reader.decode(binaryBitmap);
        return result.getText();
    }
}