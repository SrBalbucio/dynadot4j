package balbucio.dynadot4j;

import balbucio.dynadot4j.exception.DynadotHttpException;
import balbucio.dynadot4j.exception.DynadotTooManyRequestException;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import com.google.common.hash.Hashing;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DynadotRequester implements Runnable {

    private final DynadotConfig config;
    private final Dynadot instance;
    private final ScheduledExecutorService executor;
    private final Queue<Runnable> queue = new LinkedList<>();

    public DynadotRequester(Dynadot instance) {
        this.instance = instance;
        this.config = instance.getConfig();
        this.executor = config.getExecutorService();

        if (!config.getEndpointUrl().endsWith("/"))
            config.setEndpointUrl(config.getEndpointUrl() + "/");

        this.executor.scheduleWithFixedDelay(this, 1, 3, TimeUnit.SECONDS);
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

    public Future<DynadotHttpResponse> getResponse(String path, Connection.Method method, UUID requestId, String body) throws IOException {
        CompletableFuture<DynadotHttpResponse> future = new CompletableFuture<>();

        Runnable request = () -> {
            try {
                Connection connection = getConnection(path, method, requestId, body);
                Connection.Response response = connection.execute();

                throwFailMessage(response);

                future.complete(this.instance.getGson().fromJson(response.body(), DynadotHttpResponse.class));
            } catch (Exception e){
                future.completeExceptionally(e);
            }
        };
        queue.offer(request);

        return future;
    }

    public Future<DynadotHttpResponse> post(String path, UUID requestId, String body) throws IOException {
        return getResponse(path, Connection.Method.POST, requestId, body);
    }

    public Future<DynadotHttpResponse> post(String path, String body) throws IOException {
        return getResponse(path, Connection.Method.POST, UUID.randomUUID(), body);
    }

    public Future<DynadotHttpResponse> put(String path, UUID requestId, String body) throws IOException {
        return getResponse(path, Connection.Method.PUT, requestId, body);
    }

    public Future<DynadotHttpResponse> put(String path, String body) throws IOException {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), body);
    }

    public Future<DynadotHttpResponse> del(String path, UUID requestId) throws IOException {
        return getResponse(path, Connection.Method.DELETE, requestId, null);
    }

    public Future<DynadotHttpResponse> del(String path) throws IOException {
        return getResponse(path, Connection.Method.DELETE, UUID.randomUUID(), null);
    }

    public Future<DynadotHttpResponse> get(String path, UUID requestId) throws IOException {
        return getResponse(path, Connection.Method.GET, requestId, null);
    }

    public Future<DynadotHttpResponse> get(String path) throws IOException {
        return getResponse(path, Connection.Method.GET, UUID.randomUUID(), null);
    }

    public void throwFailMessage(Connection.Response response) {
        if (response.statusCode() == 200 || response.statusCode() == 201 || response.statusCode() == 202) return;

        JSONObject body = new JSONObject(response.body());
        JSONObject error = body.getJSONObject("error");

        switch (response.statusCode()) {
            case 429 -> throw new DynadotTooManyRequestException(response, error);
            default -> throw new DynadotHttpException(response, error);
        }
    }

    @Override
    public void run() {
        Runnable runnable = this.queue.poll();

        if(runnable == null) return;

        runnable.run();
    }
}
