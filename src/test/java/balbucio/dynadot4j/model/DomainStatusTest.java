package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainStatusTest {

    @Test
    void fromLabelShouldReturnActive() {
        assertEquals(DomainStatus.ACTIVE, DomainStatus.fromLabel("active"));
    }

    @Test
    void fromLabelShouldReturnNullForUnknown() {
        assertNull(DomainStatus.fromLabel("inactive"));
    }

    @Test
    void labelShouldBeCorrect() {
        assertEquals("active", DomainStatus.ACTIVE.getLabel());
    }
}
