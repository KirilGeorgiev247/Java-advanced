import bg.sofia.uni.fmi.mjt.photoalbum.ParallelMonochromeAlbumCreator;

public class Main {
    public static void main(String[] args) {
        String sourceDirectory = "sourceDirectory";

        String outputDirectory = "outputDirectory";

        ParallelMonochromeAlbumCreator albumCreator = new ParallelMonochromeAlbumCreator(10);
        albumCreator.processImages(sourceDirectory, outputDirectory);
    }
}