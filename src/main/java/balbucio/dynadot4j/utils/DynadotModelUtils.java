package balbucio.dynadot4j.utils;

import java.math.BigDecimal;

public class DynadotModelUtils {

    public static boolean asBool(String value) {
        return value != null && value.equalsIgnoreCase("yes");
    }

    public static double priceAsDouble(String currency, String value) {
        return switch (currency.toUpperCase()) {
            case "BRL" -> Double.parseDouble(value.substring(2));
            default -> Double.parseDouble(value.substring(1));
        };
    }

    public static BigDecimal priceAsDecimal(String currency, String value) {
        return switch (currency.toUpperCase()) {
            case "BRL" -> new BigDecimal(value.substring(2));
            default -> new BigDecimal(value.substring(1));
        };
    }

    public static int getYearPeriod(String value) {
        String[] values = value.split("/")[1].split(" ");

        if (!values[1].equalsIgnoreCase("year"))
            throw new RuntimeException("O período não é anual: " + values[1]);

        return Integer.parseInt(values[0]);
    }
}
