package com.samyukgu.what2wear.common.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtil {
    public static String convertToImagePath(byte[] pictureBytes) {
        if (pictureBytes == null) return "/assets/default-clothes.png";
        try {
            File tempFile = File.createTempFile("clothes_", ".png");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(pictureBytes);
            }
            return tempFile.toURI().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "/assets/images/dummy-1.png";
        }
    }
}
