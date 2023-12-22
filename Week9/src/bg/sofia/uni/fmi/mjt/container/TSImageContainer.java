package bg.sofia.uni.fmi.mjt.container;

import bg.sofia.uni.fmi.mjt.image.Image;

import java.util.LinkedList;
import java.util.Queue;

public class TSImageContainer implements ImageContainer {
    private final Queue<Image> images;

    private int maxCapacity;

    public TSImageContainer(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        images = new LinkedList<>();
    }

    public TSImageContainer() {
        maxCapacity = 0;
        images = new LinkedList<>();
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public synchronized void add(Image image) throws InterruptedException {
        if (images.size() == maxCapacity) {
            wait();
        }

        images.add(image);
        this.notifyAll();
    }

    @Override
    public synchronized Image get() throws InterruptedException {
        if (images.isEmpty()) {
            wait();
        }

        Image image = images.poll();
        this.notifyAll();
        return image;
    }
}
