package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainPrivacyTest {

    @Test
    void fromLabelShouldReturnFull() {
        assertEquals(DomainPrivacy.FULL, DomainPrivacy.fromLabel("full"));
        assertEquals(DomainPrivacy.FULL, DomainPrivacy.fromLabel("FULL"));
    }

    @Test
    void fromLabelShouldReturnPartial() {
        assertEquals(DomainPrivacy.PARTIAL, DomainPrivacy.fromLabel("partial"));
    }

    @Test
    void fromLabelShouldReturnNullForUnknown() {
        assertNull(DomainPrivacy.fromLabel("unknown"));
    }

    @Test
    void fromInfoShouldReturnFull() {
        assertEquals(DomainPrivacy.FULL, DomainPrivacy.fromInfo("Full Privacy"));
    }

    @Test
    void fromInfoShouldReturnPartialByDefault() {
        assertEquals(DomainPrivacy.PARTIAL, DomainPrivacy.fromInfo("anything else"));
    }

    @Test
    void labelsShouldBeCorrect() {
        assertEquals("full", DomainPrivacy.FULL.getLabel());
        assertEquals("partial", DomainPrivacy.PARTIAL.getLabel());
    }
}
