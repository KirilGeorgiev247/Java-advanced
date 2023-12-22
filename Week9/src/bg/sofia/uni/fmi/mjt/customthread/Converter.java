package bg.sofia.uni.fmi.mjt.customthread;

import bg.sofia.uni.fmi.mjt.container.ImageContainer;
import bg.sofia.uni.fmi.mjt.exception.UncheckedInterruptedException;
import bg.sofia.uni.fmi.mjt.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Converter extends Thread {
    private final ImageContainer rawImageContainer;

    private final Path outputDirectory;

    public Converter(ImageContainer rawImageContainer, Path outputDirectory) {
        this.rawImageContainer = rawImageContainer;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void run() {
        saveImage();
    }

    private void saveImage() {
        while (true) {
            try {
                Image image;

                synchronized (outputDirectory) {
                    image = rawImageContainer.get();
                }

                if (image.name() == null || image.data() == null) {
                    break;
                }

                Image convertedImage = convertToBlackAndWhite(image);

                save(convertedImage);
            } catch (InterruptedException interruptedException) {
                throw new UncheckedInterruptedException("Image saving is interrupted unexpectedly!",
                    interruptedException);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to save image.", e);
            }
        }
    }

    private Image convertToBlackAndWhite(Image image) {
        BufferedImage processedData =
            new BufferedImage(image.data().getWidth(), image.data().getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        processedData.getGraphics().drawImage(image.data(), 0, 0, null);

        return new Image(image.name(), processedData);
    }

    private void save(Image image) throws IOException {
        String extension;

        if (image.name().endsWith(Image.DOT_JPG_EXTENSION)) {
            extension = Image.JPG_EXTENSION;
        } else if (image.name().endsWith(Image.DOT_JPEG_EXTENSION)) {
            extension = Image.JPEG_EXTENSION;
        } else {
            extension = Image.PNG_EXTENSION;
        }

        Path fullPath = Path.of(outputDirectory.toString() + File.separator + image.name());

        ImageIO.write(image.data(), extension, fullPath.toFile());
    }
}
