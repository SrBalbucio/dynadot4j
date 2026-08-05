package balbucio.dynadot4j.exception;

import lombok.Getter;
import org.json.JSONObject;
import org.jsoup.Connection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class DomainRequiresInvestigationException extends DynadotHttpException {

    private static final Pattern PATTERN =
            Pattern.compile("Domain requires further investigation: order created\\s+([^\\s.]+)", Pattern.CASE_INSENSITIVE);

    private final String orderId;

    public DomainRequiresInvestigationException(Connection.Response response, JSONObject data, int statusCode) {
        this(response, data, statusCode, extractOrderId(data.optString("description", "")));
    }

    public DomainRequiresInvestigationException(Connection.Response response, JSONObject data, int statusCode, String orderId) {
        super(buildMessage(data.optString("description", ""), orderId), response, data, statusCode);
        this.orderId = orderId;
    }

    public static boolean matches(String description) {
        return description != null && PATTERN.matcher(description).find();
    }

    public static boolean matches(JSONObject data) {
        return data != null && matches(data.optString("description", ""));
    }

    private static String extractOrderId(String description) {
        Matcher matcher = PATTERN.matcher(description);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static String buildMessage(String description, String orderId) {
        if (description != null && !description.isBlank()) return description;
        return "Domain requires further investigation: order created " + orderId + ".";
    }
}