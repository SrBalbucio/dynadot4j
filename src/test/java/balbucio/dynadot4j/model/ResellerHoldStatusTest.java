package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResellerHoldStatusTest {

    @Test
    void onShouldHaveLabelOn() {
        assertEquals("on", ResellerHoldStatus.ON.getLabel());
    }

    @Test
    void offShouldHaveLabelOff() {
        assertEquals("off", ResellerHoldStatus.OFF.getLabel());
    }

    @Test
    void fromLabelShouldReturnCorrectEnum() {
        assertEquals(ResellerHoldStatus.ON, ResellerHoldStatus.fromLabel("on"));
        assertEquals(ResellerHoldStatus.ON, ResellerHoldStatus.fromLabel("ON"));
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromLabel("off"));
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromLabel("OFF"));
    }

    @Test
    void fromLabelShouldReturnNullForUnknown() {
        assertNull(ResellerHoldStatus.fromLabel("unknown"));
    }

    @Test
    void fromInfoShouldReturnOnForTruthyValues() {
        assertEquals(ResellerHoldStatus.ON, ResellerHoldStatus.fromInfo("yes"));
        assertEquals(ResellerHoldStatus.ON, ResellerHoldStatus.fromInfo("on"));
        assertEquals(ResellerHoldStatus.ON, ResellerHoldStatus.fromInfo("true"));
        assertEquals(ResellerHoldStatus.ON, ResellerHoldStatus.fromInfo("1"));
    }

    @Test
    void fromInfoShouldReturnOffForFalsyValues() {
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromInfo("no"));
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromInfo("off"));
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromInfo("false"));
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromInfo("0"));
    }

    @Test
    void fromInfoShouldReturnOffByDefault() {
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromInfo("anything"));
        assertEquals(ResellerHoldStatus.OFF, ResellerHoldStatus.fromInfo(""));
    }
}
