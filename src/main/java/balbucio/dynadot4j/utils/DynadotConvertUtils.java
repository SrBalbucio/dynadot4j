package balbucio.dynadot4j.utils;

import java.math.BigDecimal;

public class DynadotConvertUtils {

    public static boolean asBool(String value) {
        return value != null && value.equalsIgnoreCase("yes");
    }

    public static double priceAsDouble(String currency, String value) {
        if (!value.contains("$")) return Double.parseDouble(value);
        return switch (currency.toUpperCase()) {
            case "BRL" -> Double.parseDouble(value.substring(2));
            default -> Double.parseDouble(value.substring(1));
        };
    }

    public static BigDecimal priceAsDecimal(String currency, String value) {
        if (!value.contains("$")) return new BigDecimal(value);
        return switch (currency.toUpperCase()) {
            case "BRL" -> new BigDecimal(value.substring(2));
            default -> new BigDecimal(value.substring(1));
        };
    }

    public static int getYearPeriod(String value) {
        String[] values = value.split("/")[1].split(" ");

        if (!values[1].trim().equalsIgnoreCase("year)"))
            throw new RuntimeException("O período não é anual: " + values[1]);

        return Integer.parseInt(values[0]);
    }

    public static String toBool(boolean bool) {
        return bool ? "yes" : "no";
    }
}
