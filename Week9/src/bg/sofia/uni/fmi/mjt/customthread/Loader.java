package bg.sofia.uni.fmi.mjt.customthread;

import bg.sofia.uni.fmi.mjt.container.ImageContainer;
import bg.sofia.uni.fmi.mjt.exception.UncheckedInterruptedException;
import bg.sofia.uni.fmi.mjt.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public class Loader extends Thread {
    private final ImageContainer imageContainer;

    private final List<Path> imagePaths;

    public Loader(ImageContainer imageContainer, List<Path> imagePaths) {
        this.imageContainer = imageContainer;
        this.imagePaths = imagePaths;
    }

    @Override
    public void run() {
        while (true) {
            try {
                synchronized (imagePaths) {
                    Path imagePath;

                    if (imagePaths.isEmpty()) {
                        imageContainer.add(new Image(null, null));
                        return;
                    }

                    imagePath = imagePaths.removeFirst();

                    if (imagePath != null) {
                        imageContainer.add(loadImage(imagePath));
                    }
                }
            } catch (InterruptedException e) {
                throw new UncheckedInterruptedException("Image loading is interrupted unexpectedly!", e);
            }
        }
    }

    private Image loadImage(Path imagePath) {
        try {
            BufferedImage imageData = ImageIO.read(imagePath.toFile());
            return new Image(imagePath.getFileName().toString(), imageData);
        } catch (IOException e) {
            throw new UncheckedIOException(String.format("Failed to load image %s", imagePath.toString()), e);
        }
    }
}
