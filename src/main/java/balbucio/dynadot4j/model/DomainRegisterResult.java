package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

@Getter
@ToString
public class DomainRegisterResult {

    @SerializedName("domain_name")
    private String domainName;
    @SerializedName("expiration_date")
    private long expirationDate;

    public Date getExpirationDate() {
        return new Date(expirationDate);
    }

    public LocalDateTime getExpirationDateTime(TimeZone timeZone) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(expirationDate), timeZone.toZoneId());
    }
}
