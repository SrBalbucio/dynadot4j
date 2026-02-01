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

    public DynadotRequester(Dynadot instance) {
        this.instance = instance;
        this.config = instance.getConfig();
        this.executor = config.getExecutorService();

        if (!config.getEndpointUrl().endsWith("/"))
            config.setEndpointUrl(config.getEndpointUrl() + "/");

        AccountPriceLevel priceLevel = config.getPriceLevel();
        for (int i = 0; i < config.getRequestThreads(); i++) {
            this.executor.scheduleWithFixedDelay(this, 1, (priceLevel.getMaxRequestPerSec() / 1000), TimeUnit.MILLISECONDS);
        }
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
