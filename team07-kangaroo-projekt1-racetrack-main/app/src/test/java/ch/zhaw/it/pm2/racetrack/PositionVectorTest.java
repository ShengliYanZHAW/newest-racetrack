package ch.zhaw.it.pm2.racetrack;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PositionVectorTest {

    private static final int X = 3;
    private static final int Y = 5;

    @Test
    void testEquals() {
        PositionVector a = new PositionVector(X, Y);
        PositionVector b = new PositionVector(X, Y);
        assertEquals(a, b);
    }

    @Test
    void testEqualsWithHashMap() {
        Map<PositionVector, Integer> map = new HashMap<>();
        PositionVector a = new PositionVector(X, Y);
        map.put(a, 1);
        PositionVector b = new PositionVector(X, Y);
        assertTrue(map.containsKey(a), "Test with same object");
        assertTrue(map.containsKey(b), "Test with equal object");
    }
}
