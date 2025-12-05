package balbucio.dynadot4j;

import com.google.common.hash.Hashing;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DynadotRequester {

    private final DynadotConfig config;
    private final Dynadot instance;

    public DynadotRequester(Dynadot instance) {
        this.instance = instance;
        this.config = instance.getConfig();

        if (!config.getEndpointUrl().endsWith("/"))
            config.setEndpointUrl(config.getEndpointUrl() + "/");
    }

    private String getPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return this.config.getEndpointUrl() + path;
    }

    private String generateSignature(String path, UUID requestId, String body) {
        if (body == null) body = "";

        String signatureBody = this.config.getApiKey() + "\n" + path + "\n" + requestId.toString() + "\n" + body;

        return Hashing.hmacSha256(this.config.getApiSecret().getBytes(StandardCharsets.UTF_8))
                .hashString(signatureBody, StandardCharsets.UTF_8)
                .toString();
    }

    public Connection getConnection(String path, Connection.Method method, UUID requestId, String body) {
        Connection connection = Jsoup.connect(getPath(path))
                .method(method)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + this.config.getApiKey())
                .header("X-Request-ID", requestId.toString())
                .header("X-Signature", generateSignature(path, requestId, body))
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(true);

        if (body != null) {
            connection.requestBody(body);
        }

        return connection;
    }

    public <T> T getResponse(String path, Connection.Method method, UUID requestId, String body, Class<T> clazz) {
        Connection connection = getConnection(path, method, requestId, body);
        Connection.Response response = connection.execute();

        throwFailMessage(response);

        return this.instance.getGson().fromJson(response.body(), clazz);
    }

    public JSONObject getResponse(String path, Connection.Method method, UUID requestId, String body) {
        Connection connection = getConnection(path, method, requestId, body);
        Connection.Response response = connection.execute();

        throwFailMessage(response);

        return new JSONObject(response.body());
    }

    public <T> T post(String path, UUID requestId, String body, Class<T> clazz) {
        return getResponse(path, Connection.Method.POST, requestId, body, clazz);
    }

    public JSONObject post(String path, UUID requestId, String body) {
        return getResponse(path, Connection.Method.POST, requestId, body);
    }

    public <T> T post(String path, UUID requestId, Class<T> clazz) {
        return getResponse(path, Connection.Method.POST, requestId, null, clazz);
    }

    public JSONObject post(String path, UUID requestId) {
        return getResponse(path, Connection.Method.POST, requestId, null);
    }

    public <T> T post(String path, String body, Class<T> clazz) {
        return getResponse(path, Connection.Method.POST, UUID.randomUUID(), body, clazz);
    }

    public JSONObject post(String path, String body) {
        return getResponse(path, Connection.Method.POST, UUID.randomUUID(), body);
    }

    public <T> T put(String path, UUID requestId, String body, Class<T> clazz) {
        return getResponse(path, Connection.Method.PUT, requestId, body, clazz);
    }

    public JSONObject put(String path, UUID requestId, String body) {
        return getResponse(path, Connection.Method.PUT, requestId, body);
    }

    public <T> T put(String path, String body, Class<T> clazz) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), body, clazz);
    }

    public JSONObject put(String path, String body) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), body);
    }

    public <T> T delete(String path, UUID requestId, String body, Class<T> clazz) {
        return getResponse(path, Connection.Method.PUT, requestId, body, clazz);
    }

    public JSONObject delete(String path, UUID requestId, String body) {
        return getResponse(path, Connection.Method.PUT, requestId, body);
    }

    public <T> T delete(String path, String body, Class<T> clazz) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), body, clazz);
    }

    public JSONObject delete(String path, String body) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), body);
    }

    public <T> T get(String path, UUID requestId, Class<T> clazz) {
        return getResponse(path, Connection.Method.PUT, requestId, null, clazz);
    }

    public JSONObject get(String path, UUID requestId) {
        return getResponse(path, Connection.Method.PUT, requestId, null);
    }

    public <T> T get(String path, Class<T> clazz) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), null, clazz);
    }

    public JSONObject get(String path) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), null);
    }

    public void throwFailMessage(Connection.Response response) {

    }
}
