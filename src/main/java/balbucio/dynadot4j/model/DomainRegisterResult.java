package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

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
}
