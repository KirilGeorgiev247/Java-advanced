package bg.sofia.uni.fmi.mjt.photoalbum;

import bg.sofia.uni.fmi.mjt.container.ImageContainer;
import bg.sofia.uni.fmi.mjt.container.TSImageContainer;
import bg.sofia.uni.fmi.mjt.customthread.Converter;
import bg.sofia.uni.fmi.mjt.customthread.Loader;
import bg.sofia.uni.fmi.mjt.exception.UncheckedInterruptedException;
import bg.sofia.uni.fmi.mjt.image.Image;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelMonochromeAlbumCreator implements MonochromeAlbumCreator {

    private final ImageContainer rawImageContainer;
    private final ImageContainer convertedImageContainer;
    private final int imageProcessorsCount;
    private List<Path> imagePaths;
    private Path directory;

    public ParallelMonochromeAlbumCreator(int imageProcessorsCount) {
        this.rawImageContainer = new TSImageContainer();
        this.convertedImageContainer = new TSImageContainer();
        this.imageProcessorsCount = imageProcessorsCount;
    }

    @Override
    public void processImages(String sourceDirectory, String outputDirectory) {
        extractImages(sourceDirectory);

        int imagesCount = imagePaths.size();

        rawImageContainer.setMaxCapacity(imagesCount);
        convertedImageContainer.setMaxCapacity(imagesCount);

        getDirectory(outputDirectory);

        List<Thread> threads = runThreads(imagesCount);

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new UncheckedInterruptedException("Image processing was interrupted unexpectedly!", e);
            }
        }
    }

    private List<Thread> runThreads(int imagesCount) {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < imagesCount; i++) {
            Thread loader = new Loader(rawImageContainer, imagePaths);
            loader.start();
            threads.add(loader);
        }

        for (int i = 0; i < imageProcessorsCount; i++) {
            Thread converter = new Converter(rawImageContainer, directory);
            converter.start();
            threads.add(converter);
        }
        return threads;
    }

    private void extractImages(String sourceDirectory) {
        try (Stream<Path> paths = Files.walk(Path.of(sourceDirectory))) {
            imagePaths = paths.filter(Files::isRegularFile).filter(this::isValidImagePath).collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to get file path from " + sourceDirectory, e);
        }
    }

    private boolean isValidImagePath(Path path) {
        return path.toString().endsWith(Image.DOT_JPEG_EXTENSION) ||
            path.toString().endsWith(Image.DOT_JPG_EXTENSION) || path.toString().endsWith(Image.DOT_PNG_EXTENSION);
    }

    private void getDirectory(String outputDirectory) {
        directory = Paths.get(outputDirectory);
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            try {
                Files.createDirectory(directory);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to create a directory: " + outputDirectory, e);
            }
        }
    }
}
