package balbucio.dynadot4j.model;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrantContactTest {

    @Test
    void builderShouldCreateContact() {
        RegistrantContact contact = RegistrantContact.builder()
                .name("John Doe")
                .email("john@example.com")
                .organization("ACME")
                .phoneNumber("123456789")
                .phoneCC("55")
                .address("Rua A, 123")
                .city("São Paulo")
                .state("SP")
                .country("Brazil")
                .build();

        assertEquals("John Doe", contact.getName());
        assertEquals("john@example.com", contact.getEmail());
        assertEquals("ACME", contact.getOrganization());
        assertEquals("123456789", contact.getPhoneNumber());
        assertEquals("55", contact.getPhoneCC());
        assertEquals("Rua A, 123", contact.getAddress());
        assertEquals("São Paulo", contact.getCity());
        assertEquals("SP", contact.getState());
        assertEquals("Brazil", contact.getCountry());
    }

    @Test
    void toJSONShouldContainAllFields() {
        RegistrantContact contact = RegistrantContact.builder()
                .name("Jane")
                .email("jane@example.com")
                .phoneNumber("987654321")
                .phoneCC("1")
                .address("123 Main St")
                .city("New York")
                .state("NY")
                .country("USA")
                .organization("Corp")
                .build();

        JSONObject json = contact.toJSON();

        assertEquals("Jane", json.getString("name"));
        assertEquals("jane@example.com", json.getString("email"));
        assertEquals("987654321", json.getString("phone_number"));
        assertEquals("1", json.getString("phone_cc"));
        assertEquals("123 Main St", json.getString("address"));
        assertEquals("New York", json.getString("city"));
        assertEquals("NY", json.getString("state"));
        assertEquals("USA", json.getString("country"));
        assertEquals("Corp", json.getString("organization"));
    }

    @Test
    void equalsAndHashCodeShouldWork() {
        RegistrantContact a = RegistrantContact.builder().name("John").email("j@j.com").build();
        RegistrantContact b = RegistrantContact.builder().name("John").email("j@j.com").build();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void allArgsConstructorShouldWork() {
        RegistrantContact contact = new RegistrantContact(
                "Org", "Name", "e@e.com", "123", "55",
                "Addr", "City", "ST", "BR"
        );
        assertEquals("Name", contact.getName());
        assertEquals("Org", contact.getOrganization());
    }

    @Test
    void noArgsConstructorShouldWork() {
        RegistrantContact contact = new RegistrantContact();
        assertNull(contact.getName());
    }

    @Test
    void toStringShouldNotThrow() {
        RegistrantContact contact = RegistrantContact.builder().name("Test").build();
        assertNotNull(contact.toString());
    }
}
