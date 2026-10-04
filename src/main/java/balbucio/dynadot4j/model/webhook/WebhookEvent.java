package balbucio.dynadot4j.model.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;
import org.json.JSONArray;
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
                        (element, type, context) -> toJSONObject(element))
                .create();

        JSONObject root = new JSONObject(json);
        String event = root.optString("event", null);
        WebhookEventType type = WebhookEventType.fromLabel(event);

        Object data = null;
        if (root.has("data") && !root.isNull("data")) {
            Object rawData = root.get("data");
            if (rawData instanceof JSONObject dataObj) {
                data = type != null
                        ? effective.fromJson(dataObj.toString(), type.getDataClass())
                        : dataObj;
            } else if (rawData instanceof JSONArray dataArray) {
                // A Dynadot pode evoluir o schema e enviar `data` como array.
                // Guarda o raw em vez de lançar JSONException no controller.
                data = type != null
                        ? tryParseData(effective, dataArray.toString(), type)
                        : dataArray;
                if (data == null) {
                    data = dataArray;
                }
            } else {
                // Primitivo inesperado em `data`: preserva o valor bruto.
                data = rawData;
            }
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

    /**
     * Converte um {@link com.google.gson.JsonElement} em {@link JSONObject}.
     *
     * <p>A Dynadot envia listas como {@code order_item_list}, {@code balance_list} e
     * {@code domain_list} ora como objetos, ora como primitivos (ex:
     * {@code ["example.com"]}) ou {@code null}. O desserializador anterior fazia
     * {@code new JSONObject(element.toString())} incondicionalmente e quebrava com
     * {@code "A JSONObject text must begin with '{'"} quando o elemento não era um
     * objeto. Aqui envolvemos primitivos/arrays para manter compatibilidade com os
     * modelos {@code List<JSONObject>} existentes.</p>
     */
    private static JSONObject toJSONObject(com.google.gson.JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonObject()) {
            return new JSONObject(element.toString());
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                String value = primitive.getAsString();
                return new JSONObject().put("value", value).put("domain", value);
            }
            if (primitive.isNumber()) {
                return new JSONObject().put("value", primitive.getAsNumber());
            }
            if (primitive.isBoolean()) {
                return new JSONObject().put("value", primitive.getAsBoolean());
            }
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            return new JSONObject().put("values", new JSONArray(array.toString()));
        }
        return null;
    }

    private static Object tryParseData(Gson effective, String json, WebhookEventType type) {
        try {
            return effective.fromJson(json, type.getDataClass());
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <D> D getDataAs() {
        return (D) data;
    }
}