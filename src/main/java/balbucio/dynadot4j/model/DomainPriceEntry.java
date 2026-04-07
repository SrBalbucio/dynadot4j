package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotConvertUtils;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
public class DomainPriceEntry {

    private String currency;
    private String unit;
    @SerializedName("registration_price")
    private String registration;
    @SerializedName("renewal_price")
    private String renewal;
    @Nullable
    @SerializedName("transfer_price")
    private String transfer;
    @Nullable
    @SerializedName("restore_price")
    private String restore;

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
        return DynadotConvertUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal renewalPriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, registration);
    }

    public double transferPriceAsDouble() {
        return DynadotConvertUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal transferPriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, registration);
    }

    public double restorePriceAsDouble() {
        return DynadotConvertUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal restorePriceAsDecimal() {
        return DynadotConvertUtils.priceAsDecimal(currency, registration);
    }
}
