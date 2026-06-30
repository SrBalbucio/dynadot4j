package balbucio.dynadot4j.model;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynadotHttpResponseTest {

    private final Gson gson = new Gson();

    @Test
    void asJSONShouldReturnJSONObject() {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"result":"value"}}
                """, DynadotHttpResponse.class);
        assertNotNull(response.asJSON());
        assertTrue(response.asJSON().has("result"));
    }

    @Test
    void asClazzShouldReturnTypedObject() {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"domain_name":"example.com","available":"Yes"}}
                """, DynadotHttpResponse.class);
        BulkSearchResult result = response.asClazz(gson, BulkSearchResult.class);
        assertEquals("example.com", result.getDomainName());
        assertTrue(result.isAvailable());
    }

    @Test
    void asMapShouldReturnDataMap() {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"key":"value"}}
                """, DynadotHttpResponse.class);
        assertNotNull(response.asMap());
        assertEquals("value", response.asMap().get("key"));
    }
}
