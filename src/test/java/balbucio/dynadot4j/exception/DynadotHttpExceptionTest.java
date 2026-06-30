package balbucio.dynadot4j.exception;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DynadotHttpExceptionTest {

    @Test
    void shouldCreateWithResponseAndData() {
        Connection.Response response = mock(Connection.Response.class);
        when(response.statusCode()).thenReturn(400);

        JSONObject data = new JSONObject().put("description", "Bad request");

        DynadotHttpException ex = new DynadotHttpException(response, data, 400);

        assertEquals(400, ex.getStatusCode());
        assertSame(response, ex.getResponse());
        assertSame(data, ex.getErrorData());
        assertTrue(ex.getMessage().contains("400"));
        assertTrue(ex.getMessage().contains("Bad request"));
    }

    @Test
    void shouldCreateWithCustomMessage() {
        Connection.Response response = mock(Connection.Response.class);
        JSONObject data = new JSONObject().put("description", "error");

        DynadotHttpException ex = new DynadotHttpException("Custom error", response, data, 500);

        assertEquals("Custom error", ex.getMessage());
        assertEquals(500, ex.getStatusCode());
    }
}
