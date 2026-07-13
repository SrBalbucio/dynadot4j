package balbucio.dynadot4j.client;

import balbucio.dynadot4j.Dynadot;
import balbucio.dynadot4j.DynadotConfig;
import balbucio.dynadot4j.DynadotRequester;
import balbucio.dynadot4j.model.DynadotHttpResponse;
import balbucio.dynadot4j.model.RegistrantContact;
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
class ContactClientTest {

    @Mock
    private Dynadot dynadot;

    @Mock
    private DynadotRequester requester;

    private Gson gson;
    private ContactClient client;

    @BeforeEach
    void setUp() {
        gson = new Gson();
        DynadotConfig config = Dynadot.createDefault()
                .apiKey("key")
                .apiSecret("secret")
                .build();

        lenient().when(dynadot.getConfig()).thenReturn(config);
        lenient().when(dynadot.getRequester()).thenReturn(requester);
        lenient().when(dynadot.getGson()).thenReturn(gson);

        client = new ContactClient(dynadot);
    }

    @Test
    void createContactShouldReturnContactId() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"contact_id":42}}
                """, DynadotHttpResponse.class);

        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        RegistrantContact contact = RegistrantContact.builder()
                .name("John").email("j@j.com").build();

        int id = client.createContact(contact).get();
        assertEquals(42, id);
        verify(requester).post(eq("restful/v2/contacts"), anyString());
    }

    @Test
    void getContactShouldReturnContact() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"name":"John","email":"j@j.com","organization":"","phone_number":"","phone_cc":"","address":"","city":"","state":"","country":""}}
                """, DynadotHttpResponse.class);

        when(requester.get(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        RegistrantContact contact = client.getContact(1).get();

        assertNotNull(contact);
        assertEquals("John", contact.getName());
        assertEquals("j@j.com", contact.getEmail());
        verify(requester).get("restful/v2/contacts/1");
    }

    @Test
    void updateContactShouldReturnContactId() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"data":{"contact_id":42}}
                """, DynadotHttpResponse.class);

        when(requester.post(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(response));

        RegistrantContact contact = RegistrantContact.builder().name("John").build();
        int id = client.updateContact(1, contact).get();

        assertEquals(42, id);
        verify(requester).post(eq("restful/v2/contacts/1"), anyString());
    }

    @Test
    void deleteContactShouldCompleteSuccessfully() throws Exception {
        DynadotHttpResponse response = gson.fromJson("""
                {"code":"200","message":"OK","data":{}}
                """, DynadotHttpResponse.class);

        when(requester.del(anyString())).thenReturn(CompletableFuture.completedFuture(response));

        assertDoesNotThrow(() -> client.deleteContact(1).get());
        verify(requester).del("1");
    }

    @Test
    void deleteContactShouldPropagateError() {
        when(requester.del(anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("API error")));

        assertThrows(Exception.class, () -> client.deleteContact(1).get());
    }
}
