package balbucio.dynadot4j.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DigestTypeTest {

    @Test
    void getLabelShouldReturnLowerCase() {
        assertEquals("sha1", DigestType.SHA1.getLabel());
        assertEquals("sha256", DigestType.SHA256.getLabel());
        assertEquals("gost", DigestType.GOST.getLabel());
        assertEquals("sha384", DigestType.SHA384.getLabel());
    }
}
