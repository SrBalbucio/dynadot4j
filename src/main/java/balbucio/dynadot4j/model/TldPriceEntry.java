package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class TldPriceEntry {

    private String tld;
    private String currency;
    private String unit;
    @SerializedName("registration_price")
    private String registration;
    @SerializedName("renewal_price")
    private String renewal;
    @SerializedName("transfer_price")
    @Nullable
    private String transfer;
    @Nullable
    @SerializedName("restore_price")
    private String restore;

    @SerializedName("usage")
    private String usage;

    @SerializedName("price_unit")
    private String priceUnit;

    @SerializedName("all_years_register_price")
    private List<String> allYearsRegisterPrice;

    @SerializedName("all_years_renew_price")
    private List<String> allYearsRenewPrice;

    @SerializedName("grace_fee_price")
    private String graceFeePrice;

    @SerializedName("support_privacy")
    private String supportPrivacy;

    @SerializedName("grace_period_unit")
    private String gracePeriodUnit;

    @SerializedName("renew_grace_period")
    private String renewGracePeriod;

    @SerializedName("restore_period")
    private String restorePeriod;

    @SerializedName("delete_grace_period")
    private String deleteGracePeriod;

    @SerializedName("is_idn")
    private String isIdn;

    @SerializedName("restriction")
    private String restriction;

    @SerializedName("on_sale")
    private String onSale;

    @SerializedName("delete_grace_fee")
    private String deleteGraceFee;

    @SerializedName("delete_grace_max_rate")
    private String deleteGraceMaxRate;

    @SerializedName("delete_grace_current_rate")
    private String deleteGraceCurrentRate;

    @SerializedName("max_duration")
    private Integer maxDuration;

    @SerializedName("min_duration")
    private Integer minDuration;

    public int getPeriod() {
        return DynadotConvertUtils.getYearPeriod(unit);
    }

    public double registrationPriceAsDouble() {
        return DynadotConvertUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal registrationPriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, registration);
    }

    public double renewalPriceAsDouble() {
        return DynadotConvertUtils.priceAsDouble(currency, renewal);
    }

    public BigDecimal renewalPriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, renewal);
    }

    public double transferPriceAsDouble() {
        return DynadotConvertUtils.priceAsDouble(currency, transfer);
    }

    public BigDecimal transferPriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, transfer);
    }

    public double restorePriceAsDouble() {
        return DynadotConvertUtils.priceAsDouble(currency, restore);
    }

    public BigDecimal restorePriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, restore);
    }
}