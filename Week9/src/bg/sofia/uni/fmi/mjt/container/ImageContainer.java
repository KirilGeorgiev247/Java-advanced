package bg.sofia.uni.fmi.mjt.container;

import bg.sofia.uni.fmi.mjt.image.Image;

public interface ImageContainer {
    public void add(Image image) throws InterruptedException;

    public Image get() throws InterruptedException;

    public void setMaxCapacity(int maxCapacity);
}
