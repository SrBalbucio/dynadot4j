package balbucio.dynadot4j;

import balbucio.dynadot4j.exception.DomainRequiresInvestigationException;
import balbucio.dynadot4j.exception.DynadotHttpException;
import balbucio.dynadot4j.exception.DynadotTooManyRequestException;
import org.jsoup.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DynadotRequesterTest {

    private DynadotRequester requester;

    @BeforeEach
    void setUp() throws NoSuchAlgorithmException {
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("test-key")
                .apiSecret("test-secret")
                .endpointUrl("https://api.dynadot.com")
                .build();
        requester = new Dynadot(config).getRequester();
    }

    @Test
    void throwFailMessageShouldThrowSpecialExceptionForFurtherInvestigation() {
        Connection.Response response = mock(Connection.Response.class);
        when(response.statusCode()).thenReturn(500);
        String body = """
                {"code":500,"error":{"description":"Domain requires further investigation: order created 1234567."}}
                """;

        DomainRequiresInvestigationException ex = assertThrows(
                DomainRequiresInvestigationException.class,
                () -> requester.throwFailMessage(response, body));

        assertEquals("1234567", ex.getOrderId());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void throwFailMessageShouldThrowGenericHttpExceptionForOtherErrors() {
        Connection.Response response = mock(Connection.Response.class);
        when(response.statusCode()).thenReturn(500);
        String body = """
                {"code":500,"error":{"description":"Internal server error."}}
                """;

        assertThrows(DynadotHttpException.class, () -> requester.throwFailMessage(response, body));
    }

    @Test
    void throwFailMessageShouldThrowTooManyRequestsFor429() {
        Connection.Response response = mock(Connection.Response.class);
        when(response.statusCode()).thenReturn(429);
        String body = """
                {"code":429,"error":{"description":"Too many requests"}}
                """;

        assertThrows(DynadotTooManyRequestException.class, () -> requester.throwFailMessage(response, body));
    }

    @Test
    void throwFailMessageShouldNotThrowForSuccess() {
        Connection.Response response = mock(Connection.Response.class);
        String body = """
                {"code":200,"status":"success"}
                """;

        assertDoesNotThrow(() -> requester.throwFailMessage(response, body));
    }
}