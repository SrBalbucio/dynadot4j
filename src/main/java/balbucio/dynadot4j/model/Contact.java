package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Contact {

    @SerializedName("contact_id")
    private int contactId;
    private String organization;
    private String name;
    private String email;
    @SerializedName("phone_number")
    private String phoneNumber;
    @SerializedName("phone_cc")
    private String phoneCC;
    @SerializedName("fax_number")
    private String faxNumber;
    @SerializedName("fax_cc")
    private String faxCc;
    @SerializedName("address1")
    private String address1;
    @SerializedName("address2")
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;

    public RegistrantContact toRegistrantContact() {
        return RegistrantContact.builder()
                .organization(organization)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .phoneCC(phoneCC)
                .faxNumber(faxNumber)
                .faxCc(faxCc)
                .address(address1)
                .address2(address2)
                .city(city)
                .state(state)
                .zip(zip)
                .country(country)
                .build();
    }
}
