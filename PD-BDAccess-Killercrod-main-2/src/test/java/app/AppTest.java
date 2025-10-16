package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AppTest {
    @Test
    void add_shouldSumTwoNumbers() {
        assertEquals(5, App.add(2, 3));
    }
}
