package balbucio.dynadot4j.exception;

import org.json.JSONObject;
import org.jsoup.Connection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DomainRequiresInvestigationExceptionTest {

    private Connection.Response mockResponse() {
        Connection.Response response = mock(Connection.Response.class);
        when(response.statusCode()).thenReturn(500);
        return response;
    }

    @Test
    void shouldExtractOrderIdFromDescription() {
        Connection.Response response = mockResponse();
        JSONObject data = new JSONObject()
                .put("description", "Domain requires further investigation: order created 1234567.");

        DomainRequiresInvestigationException ex = new DomainRequiresInvestigationException(response, data, 500);

        assertEquals("1234567", ex.getOrderId());
        assertEquals(500, ex.getStatusCode());
        assertSame(data, ex.getErrorData());
        assertTrue(ex.getMessage().contains("1234567"));
    }

    @Test
    void shouldCreateWithExplicitOrderId() {
        Connection.Response response = mockResponse();
        JSONObject data = new JSONObject()
                .put("description", "Domain requires further investigation: order created 99999.");

        DomainRequiresInvestigationException ex = new DomainRequiresInvestigationException(response, data, 500, "99999");

        assertEquals("99999", ex.getOrderId());
        assertEquals(500, ex.getStatusCode());
    }

    @Test
    void shouldKeepNullOrderIdWhenDescriptionDoesNotMatch() {
        Connection.Response response = mockResponse();
        JSONObject data = new JSONObject().put("description", "Something else happened.");

        DomainRequiresInvestigationException ex = new DomainRequiresInvestigationException(response, data, 500);

        assertNull(ex.getOrderId());
    }

    @Test
    void matchesShouldReturnTrueForMatchingDescription() {
        assertTrue(DomainRequiresInvestigationException.matches(
                "Domain requires further investigation: order created 1234567."));
    }

    @Test
    void matchesShouldReturnFalseForOtherDescriptions() {
        assertFalse(DomainRequiresInvestigationException.matches("Generic server error."));
        assertFalse(DomainRequiresInvestigationException.matches((String) null));
    }

    @Test
    void matchesShouldWorkWithErrorData() {
        JSONObject data = new JSONObject()
                .put("description", "Domain requires further investigation: order created 42.");
        assertTrue(DomainRequiresInvestigationException.matches(data));
        assertFalse(DomainRequiresInvestigationException.matches((JSONObject) null));
    }

    @Test
    void isInstanceOfDynadotHttpException() {
        Connection.Response response = mockResponse();
        JSONObject data = new JSONObject()
                .put("description", "Domain requires further investigation: order created 1.");

        DomainRequiresInvestigationException ex = new DomainRequiresInvestigationException(response, data, 500);

        assertInstanceOf(DynadotHttpException.class, ex);
    }
}