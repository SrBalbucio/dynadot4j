package balbucio.dynadot4j.exception;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DynadotTooManyRequestExceptionTest {

    @Test
    void shouldHaveRateLimitMessage() {
        Connection.Response response = mock(Connection.Response.class);
        when(response.statusCode()).thenReturn(429);

        JSONObject data = new JSONObject().put("description", "Too many requests");

        DynadotTooManyRequestException ex = new DynadotTooManyRequestException(response, data, 429);

        assertTrue(ex.getMessage().contains("maximum number of requests"));
        assertEquals(429, ex.getStatusCode());
    }
}
