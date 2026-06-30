package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainSearchResultTest {

    private final Gson gson = new Gson();

    @Test
    void isAvailableShouldReturnTrueForYes() {
        DomainSearchResult result = gson.fromJson("""
                {"domain_name":"example.com","available":"Yes","premium":"no","show_price":"yes","price_list":[]}
                """, DomainSearchResult.class);
        assertTrue(result.isAvailable());
    }

    @Test
    void isAvailableShouldReturnFalseForNo() {
        DomainSearchResult result = gson.fromJson("""
                {"available":"no"}
                """, DomainSearchResult.class);
        assertFalse(result.isAvailable());
    }

    @Test
    void isPremiumShouldReturnTrueForYes() {
        DomainSearchResult result = gson.fromJson("""
                {"premium":"Yes"}
                """, DomainSearchResult.class);
        assertTrue(result.isPremium());
    }

    @Test
    void isShowingPriceShouldReturnTrueForYes() {
        DomainSearchResult result = gson.fromJson("""
                {"show_price":"Yes"}
                """, DomainSearchResult.class);
        assertTrue(result.isShowingPrice());
    }

    @Test
    void getPriceByYearPeriodShouldReturnMatchingPrice() {
        DomainSearchResult result = gson.fromJson("""
                {
                    "price_list": [
                        {"currency":"USD","unit":"0/1 year","registration_price":"$10.00","renewal_price":"$12.00"},
                        {"currency":"USD","unit":"0/2 year","registration_price":"$18.00","renewal_price":"$22.00"}
                    ]
                }
                """, DomainSearchResult.class);
        assertTrue(result.getPriceByYearPeriod(1).isPresent());
        assertFalse(result.getPriceByYearPeriod(3).isPresent());
    }
}
