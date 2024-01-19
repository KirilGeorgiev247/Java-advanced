package bg.sofia.uni.fmi.mjt.compass.api.request;

import java.net.URI;
import java.net.URISyntaxException;

public interface BuiltRequest {
    public URI uri() throws URISyntaxException;
}
