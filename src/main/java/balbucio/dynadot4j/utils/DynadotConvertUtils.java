package balbucio.dynadot4j.utils;

import java.math.BigDecimal;

public class DynadotConvertUtils {

    public static boolean asBool(String value) {
        if (value == null) return false;
        String v = value.trim().toLowerCase();
        return v.equals("yes") || v.equals("true") || v.equals("1") || v.equals("y");
    }

    public static boolean asBool(Object value) {
        if (value == null) return false;
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return n.intValue() != 0;
        return asBool(value.toString());
    }

    public static double priceAsDouble(String currency, String value) {
        try {
            if (value == null) return 0.0;
            if (value.equalsIgnoreCase("Problem getting prices")) return 0.0;
            if (!value.contains("$")) return Double.parseDouble(value);
            return switch (currency.toUpperCase()) {
                case "BRL" -> Double.parseDouble(value.substring(2));
                default -> Double.parseDouble(value.substring(1));
            };
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public static BigDecimal priceAsDecimal(String currency, String value) {
        try {
            if (value == null) return BigDecimal.ZERO;
            if (value.equalsIgnoreCase("Problem getting prices")) return BigDecimal.ZERO;
            if (!value.contains("$")) return new BigDecimal(value);
            return switch (currency.toUpperCase()) {
                case "BRL" -> new BigDecimal(value.substring(2));
                default -> new BigDecimal(value.substring(1));
            };
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    public static int getYearPeriod(String value) {
        String[] values = value
                .replace("(", "")
                .replace(")", "")
                .split("/")[1]
                .split(" ");

        if (!values[1].trim().equalsIgnoreCase("year"))
            throw new RuntimeException("O período não é anual: " + values[1]);

        return Integer.parseInt(values[0]);
    }

    public static String toBool(boolean bool) {
        return bool ? "yes" : "no";
    }

    public static String toOptBool(boolean bool) {
        return Boolean.toString(bool);
    }
}
