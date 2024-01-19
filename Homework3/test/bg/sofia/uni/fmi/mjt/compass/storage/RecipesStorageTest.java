package bg.sofia.uni.fmi.mjt.compass.storage;

import bg.sofia.uni.fmi.mjt.compass.api.RecipesResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RecipesStorageTest {

    private static final int BIG_TIMEOUT = 10;

    private static final int SMALL_TIMEOUT = 1;
    private final String testKey = "testKey";
    @Mock
    RecipesResult recipesResult;
    Storage<String, RecipesResult> recipesStorage;

    @Test
    void testIfResultIsGetAndPutCorrectlyWhenNotTimedOut() {
        recipesStorage = new RecipesStorage(BIG_TIMEOUT);

        recipesStorage.put(testKey, recipesResult);

        assertTrue(recipesStorage.has(testKey), "Added key should be stored until timed out!");

        recipesStorage.get(testKey);
        recipesStorage.get(testKey);

        assertEquals(recipesResult, recipesStorage.get(testKey),
            "Added key value pair should be returned correctly until timed out!");

        assertTrue(recipesStorage.has(testKey), "Added key should be stored until timed out!");
    }

    @Test
    void testIfResultIsDeletedAfterTimeOutExceeds() {
        recipesStorage = new RecipesStorage(SMALL_TIMEOUT);

        recipesStorage.put(testKey, recipesResult);

        assertTrue(recipesStorage.has(testKey), "Added key should be stored until timed out!");

        assertEquals(recipesResult, recipesStorage.get(testKey),
            "Added key value pair should be returned correctly until timed out!");

        assertFalse(recipesStorage.has(testKey), "Added key should be deleted after timed out!");

        assertNull(recipesStorage.get(testKey), "Timed out keys should return null!");
    }

    @Test
    void testIfAbsentKeyReturnsNull() {
        recipesStorage = new RecipesStorage(BIG_TIMEOUT);

        assertNull(recipesStorage.get(testKey), "Absent key should return null!");
    }
}
