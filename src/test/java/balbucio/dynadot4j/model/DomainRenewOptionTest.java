package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainRenewOptionTest {

    @Test
    void fromLabelShouldReturnCorrectEnum() {
        assertEquals(DomainRenewOption.RESET, DomainRenewOption.fromLabel("reset"));
        assertEquals(DomainRenewOption.AUTO, DomainRenewOption.fromLabel("auto"));
        assertEquals(DomainRenewOption.DONOT, DomainRenewOption.fromLabel("donot"));
    }

    @Test
    void fromLabelShouldBeCaseInsensitive() {
        assertEquals(DomainRenewOption.AUTO, DomainRenewOption.fromLabel("AUTO"));
    }

    @Test
    void fromLabelShouldReturnNullForUnknown() {
        assertNull(DomainRenewOption.fromLabel("unknown"));
    }

    @Test
    void fromInfoShouldReturnAuto() {
        assertEquals(DomainRenewOption.AUTO, DomainRenewOption.fromInfo("auto-renew"));
    }

    @Test
    void fromInfoShouldReturnDonotByDefault() {
        assertEquals(DomainRenewOption.DONOT, DomainRenewOption.fromInfo("anything"));
    }

    @Test
    void labelsShouldBeCorrect() {
        assertEquals("reset", DomainRenewOption.RESET.getLabel());
        assertEquals("auto", DomainRenewOption.AUTO.getLabel());
        assertEquals("donot", DomainRenewOption.DONOT.getLabel());
    }
}
