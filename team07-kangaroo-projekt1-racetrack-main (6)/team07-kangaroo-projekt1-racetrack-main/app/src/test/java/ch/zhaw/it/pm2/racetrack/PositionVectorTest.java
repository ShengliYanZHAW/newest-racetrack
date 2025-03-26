package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for the PositionVector class.
 * This class tests the equals method and its behavior in collections.
 */
public class PositionVectorTest {

    private static final int X = 3;
    private static final int Y = 5;

    /**
     * Tests that two PositionVector objects with the same coordinates are equal.
     */
    @Test
    public void testEquals() {
        final PositionVector a = new PositionVector(X, Y);
        final PositionVector b = new PositionVector(X, Y);
        assertEquals(a, b, "Two PositionVectors with the same coordinates should be equal");
    }

    /**
     * Tests that PositionVector objects behave correctly when used as keys in a HashMap.
     */
    @Test
    public void testEqualsWithHashMap() {
        final Map<PositionVector, Integer> map = new HashMap<>();
        final PositionVector a = new PositionVector(X, Y);
        map.put(a, 1);
        final PositionVector b = new PositionVector(X, Y);
        assertTrue(map.containsKey(a), "HashMap should contain the key for the same object");
        assertTrue(map.containsKey(b), "HashMap should contain the key for an equal object");
    }
}
