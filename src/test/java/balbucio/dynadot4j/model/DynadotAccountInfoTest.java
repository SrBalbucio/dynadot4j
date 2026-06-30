package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DynadotAccountInfoTest {

    @Test
    void getBalanceByCurrencyShouldReturnMatching() {
        DynadotAccountInfo.Balance usd = new DynadotAccountInfo.Balance();
        usd.setCurrency("USD");
        usd.setAmount("100.50");

        DynadotAccountInfo.Balance brl = new DynadotAccountInfo.Balance();
        brl.setCurrency("BRL");
        brl.setAmount("500.00");

        DynadotAccountInfo info = new DynadotAccountInfo();
        info.setBalanceList(List.of(usd, brl));

        assertTrue(info.getBalanceByCurrency("USD").isPresent());
        assertEquals("100.50", info.getBalanceByCurrency("USD").get().getAmount());
        assertTrue(info.getBalanceByCurrency("BRL").isPresent());
        assertFalse(info.getBalanceByCurrency("EUR").isPresent());
    }

    @Test
    void getBalanceByCurrencyShouldBeCaseInsensitive() {
        DynadotAccountInfo.Balance usd = new DynadotAccountInfo.Balance();
        usd.setCurrency("USD");
        usd.setAmount("50");

        DynadotAccountInfo info = new DynadotAccountInfo();
        info.setBalanceList(List.of(usd));

        assertTrue(info.getBalanceByCurrency("usd").isPresent());
    }

    @Test
    void balanceToBigDecimalShouldConvert() {
        DynadotAccountInfo.Balance balance = new DynadotAccountInfo.Balance();
        balance.setCurrency("USD");
        balance.setAmount("99.99");

        assertEquals(new BigDecimal("99.99"), balance.toBigDecimal());
    }

    @Test
    void toStringShouldNotThrow() {
        DynadotAccountInfo info = new DynadotAccountInfo();
        info.setUsername("test");
        assertNotNull(info.toString());
    }
}
