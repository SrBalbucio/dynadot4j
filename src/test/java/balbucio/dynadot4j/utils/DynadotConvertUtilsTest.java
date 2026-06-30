package balbucio.dynadot4j.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DynadotConvertUtilsTest {

    @Test
    void asBoolShouldReturnTrueForYes() {
        assertTrue(DynadotConvertUtils.asBool("Yes"));
        assertTrue(DynadotConvertUtils.asBool("yes"));
        assertTrue(DynadotConvertUtils.asBool("YES"));
    }

    @Test
    void asBoolShouldReturnFalseForNo() {
        assertFalse(DynadotConvertUtils.asBool("no"));
        assertFalse(DynadotConvertUtils.asBool("No"));
        assertFalse(DynadotConvertUtils.asBool("NO"));
    }

    @Test
    void asBoolShouldReturnFalseForNull() {
        assertFalse(DynadotConvertUtils.asBool(null));
    }

    @Test
    void priceAsDoubleShouldReturnZeroForNull() {
        assertEquals(0.0, DynadotConvertUtils.priceAsDouble("USD", null));
    }

    @Test
    void priceAsDoubleShouldReturnZeroForProblemGettingPrices() {
        assertEquals(0.0, DynadotConvertUtils.priceAsDouble("USD", "Problem getting prices"));
    }

    @Test
    void priceAsDoubleShouldParseUsdFormat() {
        assertEquals(10.50, DynadotConvertUtils.priceAsDouble("USD", "$10.50"), 0.001);
    }

    @Test
    void priceAsDoubleShouldParseBrlFormat() {
        assertEquals(55.90, DynadotConvertUtils.priceAsDouble("BRL", "R$55.90"), 0.001);
    }

    @Test
    void priceAsDoubleShouldParsePlainNumber() {
        assertEquals(99.99, DynadotConvertUtils.priceAsDouble("USD", "99.99"), 0.001);
    }

    @Test
    void priceAsDoubleShouldHandleInvalidValue() {
        assertEquals(0.0, DynadotConvertUtils.priceAsDouble("USD", "not-a-number"));
    }

    @Test
    void priceAsDecimalShouldReturnZeroForProblemGettingPrices() {
        assertEquals(BigDecimal.ZERO, DynadotConvertUtils.priceAsDecimal("USD", "Problem getting prices"));
    }

    @Test
    void priceAsDecimalShouldParseUsd() {
        assertEquals(new BigDecimal("10.50"), DynadotConvertUtils.priceAsDecimal("USD", "$10.50"));
    }

    @Test
    void priceAsDecimalShouldParseBrl() {
        assertEquals(new BigDecimal("55.90"), DynadotConvertUtils.priceAsDecimal("BRL", "R$55.90"));
    }

    @Test
    void getYearPeriodShouldExtractYear() {
        assertEquals(1, DynadotConvertUtils.getYearPeriod("0/1 year"));
        assertEquals(5, DynadotConvertUtils.getYearPeriod("0/5 year"));
    }

    @Test
    void getYearPeriodShouldThrowForNonYear() {
        assertThrows(RuntimeException.class, () -> DynadotConvertUtils.getYearPeriod("0/1 month"));
    }

    @Test
    void toBoolShouldConvertTrue() {
        assertEquals("yes", DynadotConvertUtils.toBool(true));
    }

    @Test
    void toBoolShouldConvertFalse() {
        assertEquals("no", DynadotConvertUtils.toBool(false));
    }

    @Test
    void toOptBoolShouldConvertTrue() {
        assertEquals("true", DynadotConvertUtils.toOptBool(true));
    }

    @Test
    void toOptBoolShouldConvertFalse() {
        assertEquals("false", DynadotConvertUtils.toOptBool(false));
    }
}
