package balbucio.dynadot4j;

import balbucio.dynadot4j.exception.DynadotHttpException;
import balbucio.dynadot4j.exception.DynadotTooManyRequestException;
import balbucio.dynadot4j.model.AccountPriceLevel;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import com.google.common.hash.Hashing;
import lombok.Getter;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class DynadotRequester implements Runnable {

    private final DynadotConfig config;
    private final Dynadot instance;
    private final ScheduledExecutorService executor;
    private final Queue<Runnable> queue = new LinkedList<>();

    public DynadotRequester(Dynadot instance, DynadotConfig config) {
        this.instance = instance;
        this.config = config;
        this.executor = config.getExecutorService();

        if (!config.getEndpointUrl().endsWith("/"))
            config.setEndpointUrl(config.getEndpointUrl() + "/");

        AccountPriceLevel priceLevel = config.getPriceLevel();
        for (int i = 0; i < (priceLevel.getMaxRequestPerSec() + config.getRequestThreads()); i++) {
            this.executor.scheduleWithFixedDelay(this, 1, 700, TimeUnit.MILLISECONDS);
        }
    }

    private String getPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return this.config.getEndpointUrl() + path;
    }

    private String generateSignature(String path, UUID requestId, String body) {
        if (path.startsWith("restful")) path = path.substring("restful".length() + 4);

        StringBuilder builder = new StringBuilder();

        builder.append(config.getApiKey().trim()).append("\n")
                .append(path).append("\n");

        if (requestId != null) {
            builder.append(requestId);
        }

        builder.append("\n");

        if (body != null) {
            builder.append(body);
        }

        System.out.println(builder.toString());

        return Hashing.hmacSha256(this.config.getApiSecret().trim().getBytes(StandardCharsets.UTF_8))
                .hashString(builder.toString(), StandardCharsets.UTF_8)
                .toString();
    }

    public Connection getConnection(String path, Connection.Method method, UUID requestId, String body) {
        String signature = generateSignature(path, requestId, body);
        System.out.println(signature);

        Connection connection = Jsoup.connect(getPath(path))
                .method(method)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + this.config.getApiKey().trim())
                .header("X-Signature", signature)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .followRedirects(true);

        if (body != null) {
            connection.header("Content-Type", "application/json");
            connection.requestBody(body);
        }

        if (requestId != null) {
            connection.header("X-Request-Id", requestId.toString());
        }

        return connection;
    }

    public CompletableFuture<DynadotHttpResponse> getResponse(String path, Connection.Method method, UUID requestId, String body) {
        CompletableFuture<DynadotHttpResponse> future = new CompletableFuture<>();

        Runnable request = () -> {
            try {
                Connection connection = getConnection(path, method, requestId, body);
                Connection.Response response = connection.execute();
                String raw = response.body();

                throwFailMessage(response, raw);

                future.complete(this.instance.getGson().fromJson(raw, DynadotHttpResponse.class));
            } catch (Exception e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        };
        queue.offer(request);

        return future;
    }

    public CompletableFuture<DynadotHttpResponse> post(String path, UUID requestId, String body) {
        return getResponse(path, Connection.Method.POST, requestId, body);
    }

    public CompletableFuture<DynadotHttpResponse> post(String path, String body) {
        return getResponse(path, Connection.Method.POST, UUID.randomUUID(), body);
    }

    public CompletableFuture<DynadotHttpResponse> put(String path, UUID requestId, String body) {
        return getResponse(path, Connection.Method.PUT, requestId, body);
    }

    public CompletableFuture<DynadotHttpResponse> put(String path, String body) {
        return getResponse(path, Connection.Method.PUT, UUID.randomUUID(), body);
    }

    public CompletableFuture<DynadotHttpResponse> del(String path, UUID requestId) {
        return getResponse(path, Connection.Method.DELETE, requestId, null);
    }

    public CompletableFuture<DynadotHttpResponse> del(String path) {
        return getResponse(path, Connection.Method.DELETE, UUID.randomUUID(), null);
    }

    public CompletableFuture<DynadotHttpResponse> get(String path, UUID requestId) {
        return getResponse(path, Connection.Method.GET, requestId, null);
    }

    public CompletableFuture<DynadotHttpResponse> get(String path) {
        return getResponse(path, Connection.Method.GET, UUID.randomUUID(), null);
    }

    public void throwFailMessage(Connection.Response response, String bodyRaw) {

        JSONObject body = new JSONObject(bodyRaw);

        if (config.isDebug()) System.out.println(body.toString());

        int statusCode = body.optInt("code", response.statusCode());

        if (statusCode == 200 || statusCode == 201 || statusCode == 202) return;

        JSONObject error = body.getJSONObject("error");
        switch (response.statusCode()) {
            case 429 -> throw new DynadotTooManyRequestException(response, error, statusCode);
            default -> throw new DynadotHttpException(response, error, statusCode);
        }
    }

    @Override
    public void run() {
        Runnable runnable = this.queue.poll();

        if (runnable != null) runnable.run();
    }
}
