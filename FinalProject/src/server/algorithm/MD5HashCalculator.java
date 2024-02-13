package server.algorithm;

import config.Config;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashCalculator implements HashCalculator {
    private static final String MD5_NAME = "MD5";

    private static final int HEX = 16;

    @Override
    public String calculate(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(MD5_NAME);
        try (DigestInputStream dis = new DigestInputStream(is, md)) {
            byte[] buffer = new byte[Config.DEFAULT_BUFFER_SIZE];
            while (dis.read(buffer) > 0) {
            }
        }

        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1, digest);

        return bigInt.toString(HEX);
    }
}
