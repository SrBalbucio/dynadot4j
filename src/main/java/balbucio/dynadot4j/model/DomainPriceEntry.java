package balbucio.dynadot4j.model;

import balbucio.dynadot4j.utils.DynadotModelUtils;
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
    private String registration;
    private String renewal;
    @Nullable
    private String transfer;
    @Nullable
    private String restore;

    public int getPeriod() {
        return DynadotModelUtils.getYearPeriod(unit);
    }

    public double registrationPriceAsDouble() {
        return DynadotModelUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal registrationPriceAsDecimal() {
        return DynadotModelUtils.priceAsDecimal(currency, registration);
    }

    public double renewalPriceAsDouble() {
        return DynadotModelUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal renewalPriceAsDecimal() {
        return DynadotModelUtils.priceAsDecimal(currency, registration);
    }

    public double transferPriceAsDouble() {
        return DynadotModelUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal transferPriceAsDecimal() {
        return DynadotModelUtils.priceAsDecimal(currency, registration);
    }

    public double restorePriceAsDouble() {
        return DynadotModelUtils.priceAsDouble(currency, registration);
    }

    public BigDecimal restorePriceAsDecimal() {
        return DynadotModelUtils.priceAsDecimal(currency, registration);
    }
}
