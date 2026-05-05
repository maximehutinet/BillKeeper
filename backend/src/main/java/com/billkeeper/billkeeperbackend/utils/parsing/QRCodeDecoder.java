package com.billkeeper.billkeeperbackend.utils.parsing;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.awt.image.BufferedImage;
import java.util.Map;

public class QRCodeDecoder {

    private static final QRCodeReader READER = new QRCodeReader();
    private static final Map<DecodeHintType, Object> HINTS = Map.of(
            DecodeHintType.TRY_HARDER, Boolean.TRUE
    );

    public static String decode(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return READER.decode(bitmap, HINTS).getText();
        } catch (NotFoundException | FormatException | ChecksumException e) {
            return "";
        }
    }
}
