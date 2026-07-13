package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import balbucio.dynadot4j.model.AccountPriceLevel;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderClientTest {

    @Mock
    private Dynadot dynadot;

    @Mock
    private DynadotRequester requester;

    @Captor
    private ArgumentCaptor<String> pathCaptor;

    @Captor
    private ArgumentCaptor<String> bodyCaptor;

    private Gson gson;
    private OrderClient client;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("key")
                .apiSecret("secret")
                .priceLevel(AccountPriceLevel.REGULAR)
                .build();

        lenient().when(dynadot.getConfig()).thenReturn(config);
        lenient().when(dynadot.getRequester()).thenReturn(requester);
        lenient().when(dynadot.getGson()).thenReturn(gson);

        client = new OrderClient(dynadot);
    }

    @Test
    void cancelTransferShouldPostCorrectPath() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.cancelTransfer("order123").get();

        verify(requester).post("restful/v2/orders/order123/cancel_transfer", "");
    }

    @Test
    void cancelTransferShouldPropagateError() {
        when(requester.post(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("API error")));

        assertThrows(Exception.class, () -> client.cancelTransfer("order123").get());
    }

    @Test
    void setTransferAuthCodeShouldPostCorrectPathAndBody() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{}}
                """, DynadotHttpResponse.class);
        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        client.setTransferAuthCode("order123", "newauthcode").get();

        verify(requester).post(eq("restful/v2/orders/order123/transfer_auth_code"), bodyCaptor.capture());
        assertTrue(bodyCaptor.getValue().contains("\"auth_code\":\"newauthcode\""));
    }

    @Test
    void setTransferAuthCodeShouldPropagateError() {
        when(requester.post(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("API error")));

        assertThrows(Exception.class, () -> client.setTransferAuthCode("order123", "code").get());
    }
}
