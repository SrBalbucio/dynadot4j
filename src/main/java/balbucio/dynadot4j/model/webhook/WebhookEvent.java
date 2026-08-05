package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONObject;

@Getter
@ToString
public class WebhookEvent<T> {

    @SerializedName("event")
    private String event;
    @SerializedName("event_id")
    private long eventId;
    @SerializedName("timestamp")
    private long timestamp;
    @SerializedName("data")
    private T data;

    private WebhookEvent() {
    }

    public static WebhookEvent<?> parse(String json) {
        return parse(json, new Gson());
    }

    public static WebhookEvent<?> parse(String json, Gson gson) {
        Gson effective = gson.newBuilder()
                .registerTypeAdapter(JSONObject.class, (JsonDeserializer<JSONObject>)
                        (element, type, context) -> new JSONObject(element.toString()))
                .create();

        JSONObject root = new JSONObject(json);
        String event = root.optString("event", null);
        WebhookEventType type = WebhookEventType.fromLabel(event);

        Object data = null;
        if (root.has("data") && !root.isNull("data")) {
            data = type != null
                    ? effective.fromJson(root.getJSONObject("data").toString(), type.getDataClass())
                    : root.getJSONObject("data");
        }

        WebhookEvent<Object> result = new WebhookEvent<>();
        result.event = event;
        result.eventId = root.optLong("event_id");
        result.timestamp = root.optLong("timestamp");
        result.data = data;
        return result;
    }

    public WebhookEventType getEventType() {
        return WebhookEventType.fromLabel(event);
    }

    @SuppressWarnings("unchecked")
    public <D> D getDataAs() {
        return (D) data;
    }
}