package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@ToString
public class DynadotAccountInfo {

    private String username;
    @SerializedName("forum_name")
    private String forumName;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("customer_since")
    private String customerSince;
    @SerializedName("account_lock")
    private String accountLock;
    @SerializedName("custom_time_zone")
    private String customTimezone;
    @SerializedName("total_spending")
    private String totalSpending;
    @SerializedName("price_level")
    private String priceLevel;
    @SerializedName("account_balance")
    private String accountBalance;
    @SerializedName("balance_list")
    private List<Balance> balanceList;

    public Optional<Balance> getBalanceByCurrency(String currency) {
        return balanceList.stream()
                .filter(balance -> balance.getCurrency().equalsIgnoreCase(currency))
                .findFirst();
    }

    @Getter
    @Setter
    @ToString
    public static class Balance {
        private String currency;
        private String amount;

        public BigDecimal toBigDecimal() {
            return new BigDecimal(amount);
        }
    }
}
