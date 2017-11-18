package com.wtz.chat.utils;

import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCodeUtil {

    // 二维码颜色
    private static final int BLACK = 0xFF000000;
    // 二维码颜色
    private static final int WHITE = 0xFFFFFFFF;

    public static ImageIcon createQrCode(String text, int width, int height) {
        ImageIcon icon = null;
        Hashtable hints = new Hashtable();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);
        try {
            BitMatrix encode = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width,
                    height, hints);

            int codeWidth = encode.getWidth();
            int codeHeight = encode.getHeight();

            BufferedImage image = new BufferedImage(codeWidth, codeHeight,
                    BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < codeWidth; i++) {
                for (int j = 0; j < codeHeight; j++) {
                    image.setRGB(i, j, encode.get(i, j) ? BLACK : WHITE);
                }
            }
            icon = new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

}