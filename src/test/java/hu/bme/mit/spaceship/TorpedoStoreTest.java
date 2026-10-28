package hu.bme.mit.spaceship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TorpedoStoreTest {

    @Test
    void fire_Success() {
        TorpedoStore store = new TorpedoStore(1, 0.0);
        assertTrue(store.fire(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 2})
    void fire_InvalidNumberOfTorpedos(int numberOfTorpedos) {
        TorpedoStore store = new TorpedoStore(1, 0.0);
        assertThrows(IllegalArgumentException.class, () -> store.fire(numberOfTorpedos));
    }

    @Test
    void isEmpty() {
        TorpedoStore store = new TorpedoStore(0, 0.0);
        assertTrue(store.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void getTorpedoCount(int count) {
        TorpedoStore store = new TorpedoStore(count);
        assertEquals(count, store.getTorpedoCount());
    }

    @Test
    void constructor_customFailureRate() {
        evilSetEnv("IVT_RATE", "5.0");
        TorpedoStore store = new TorpedoStore(1, 0.0);
        store.fire(1);
    }

    @Test
    void constructor_invalidCustomFailureRate() {
        evilSetEnv("IVT_RATE", "invalid");
        TorpedoStore store = new TorpedoStore(1, 0.0);
        assertTrue(store.fire(1));
    }

    @Test
    void fireFailure() {
        TorpedoStore store = new TorpedoStore(1, 1.0);
        assertFalse(store.fire(1));
    }

    private static Map<String, String> theCaseInsensitiveEnvironment = null;
    @SuppressWarnings({"unchecked", "removal", "SameParameterValue"})
    private static void evilSetEnv(String key, String value) {
        if (theCaseInsensitiveEnvironment == null) {
            try {
                Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafeField.setAccessible(true);
                Unsafe unsafe = (Unsafe) theUnsafeField.get(null);

                Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
                Field field = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");

                Object fieldBase = unsafe.staticFieldBase(field);
                long offset = unsafe.staticFieldOffset(field);
                theCaseInsensitiveEnvironment = (Map<String, String>) unsafe.getObject(fieldBase, offset);
            } catch (Exception ex) {
                throw new RuntimeException("Evil trickery is not possible!", ex);
            }
        }

        theCaseInsensitiveEnvironment.put(key, value);
    }

}
