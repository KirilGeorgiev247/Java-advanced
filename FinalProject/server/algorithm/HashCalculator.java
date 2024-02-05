package server.algorithm;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public interface HashCalculator {
    public String calculate(InputStream is) throws NoSuchAlgorithmException, IOException;
}
