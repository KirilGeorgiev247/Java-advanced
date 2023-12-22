package bg.sofia.uni.fmi.mjt.image;

import java.awt.image.BufferedImage;

public record Image(String name, BufferedImage data) {
    public static final String DOT_PNG_EXTENSION = ".png";
    public static final String DOT_JPG_EXTENSION = ".jpg";
    public static final String JPG_EXTENSION = "jpg";
    public static final String DOT_JPEG_EXTENSION = ".jpeg";
    public static final String JPEG_EXTENSION = "jpeg";
    public static final String PNG_EXTENSION = "png";
}