package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import balbucio.dynadot4j.model.DynadotAccountInfo;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountClientTest {

    @Mock
    private Dynadot dynadot;

    @Mock
    private DynadotRequester requester;

    private Gson gson;
    private AccountClient client;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        DynadotConfig config = DynadotConfig.createDefault()
                .apiKey("key")
                .apiSecret("secret")
                .build();

        lenient().when(dynadot.getConfig()).thenReturn(config);
        lenient().when(dynadot.getRequester()).thenReturn(requester);
        lenient().when(dynadot.getGson()).thenReturn(gson);

        client = new AccountClient(dynadot);
    }

    @Test
    void getAccountInfoShouldReturnAccountInfo() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"account_info":{"username":"testuser","account_balance":"100.00"}}}
                """, DynadotHttpResponse.class);

        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        DynadotAccountInfo info = client.getAccountInfo().get();

        assertNotNull(info);
        assertEquals("testuser", info.getUsername());
        verify(requester).get("restful/v2/accounts/info");
    }
}
