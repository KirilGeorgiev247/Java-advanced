package bg.sofia.uni.fmi.mjt.space.algorithm;

import bg.sofia.uni.fmi.mjt.space.algorithm.Rijndael;
import bg.sofia.uni.fmi.mjt.space.exception.CipherException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RijndaelTest {
    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final int KEY_SIZE_IN_BITS = 128;
    private Rijndael rijndael;
    private SecretKey secretKey;
    @Mock
    private SecretKey invalidSecretKeyMock;

    @Mock
    private InputStream inputStreamMock;

    @Mock
    private OutputStream outputStreamMock;

    private SecretKey generateSecretKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm", e);
        }
        keyGenerator.init(KEY_SIZE_IN_BITS);
        return keyGenerator.generateKey();
    }

    @BeforeEach
    void setUp() {
        secretKey = generateSecretKey();
        rijndael = new Rijndael(secretKey);
    }

    @Test
    void testIfDecryptAfterEncryptReturnsEqualValueToTheOriginal() throws CipherException {
        String testText = "Testing, testing, testing!";

        try (InputStream decryptedInputStream = new ByteArrayInputStream(testText.getBytes());
             ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream()) {

            rijndael.encrypt(decryptedInputStream, encryptedOutputStream);
            byte[] encryptedData = encryptedOutputStream.toByteArray();

            try (InputStream encryptedInputStream = new ByteArrayInputStream(encryptedData);
                 ByteArrayOutputStream decryptedOutputStream = new ByteArrayOutputStream()) {
                rijndael.decrypt(encryptedInputStream, decryptedOutputStream);
                byte[] decryptedData = decryptedOutputStream.toByteArray();

                assertArrayEquals(testText.getBytes(), decryptedData,
                    "Decrypt after encrypt should return the same value compared to the original one!");
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Test
    void testIfEncryptReturnsDifferentValueToTheOriginal() throws CipherException {
        String testText = "Testing, testing, testing!";

        try (InputStream decryptedInputStream = new ByteArrayInputStream(testText.getBytes());
             ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream()) {

            rijndael.encrypt(decryptedInputStream, encryptedOutputStream);
            byte[] encryptedData = encryptedOutputStream.toByteArray();

            assertFalse(Arrays.equals(testText.getBytes(), encryptedData),
                "Encrypt should return different value compared to the original one!");
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    @Test
    void testIfCipherExceptionIsBeingThrownWhenInvalidKeyIsPassedForEncrypt() {
        Rijndael invalidRijndael = new Rijndael(invalidSecretKeyMock);

        assertThrows(CipherException.class, () -> invalidRijndael.encrypt(inputStreamMock, outputStreamMock),
            "Encrypt should throw when secret key is invalid!");
    }

    @Test
    void testIfCipherExceptionIsBeingThrownWhenInvalidKeyIsPassedForDecrypt() {
        Rijndael invalidRijndael = new Rijndael(invalidSecretKeyMock);

        assertThrows(CipherException.class, () -> invalidRijndael.decrypt(inputStreamMock, outputStreamMock),
            "Decrypt should throw when secret key is invalid!");
    }
}
