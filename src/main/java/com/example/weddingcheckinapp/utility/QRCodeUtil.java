package com.example.weddingcheckinapp.utility;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class QRCodeUtil {

    public static String decodeQRCode(File qrCodeImage) throws IOException, NotFoundException {
        var bufferedImage = ImageIO.read(qrCodeImage);
        var source = new BufferedImageLuminanceSource(bufferedImage);
        var binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result = new MultiFormatReader().decode(binaryBitmap);
        return result.getText(); // Liefert den Inhalt des QR-Codes zurück
    }
}

