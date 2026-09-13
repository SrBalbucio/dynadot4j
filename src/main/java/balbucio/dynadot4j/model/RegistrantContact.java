package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import org.json.JSONObject;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrantContact {

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
    private String address;
    @SerializedName("address2")
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String country;
    @SerializedName("contact_extension")
    private ContactExtension contactExtension;

    public String getAddress1() {
        return address;
    }

    public void setAddress1(String address1) {
        this.address = address1;
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ContactExtension {
        private String tld;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        if (organization != null) obj.put("organization", organization);
        if (name != null) obj.put("name", name);
        if (email != null) obj.put("email", email);
        if (phoneNumber != null) obj.put("phone_number", phoneNumber);
        if (phoneCC != null) obj.put("phone_cc", phoneCC);
        if (faxNumber != null) obj.put("fax_number", faxNumber);
        if (faxCc != null) obj.put("fax_cc", faxCc);
        if (address != null) obj.put("address1", address);
        if (address2 != null) obj.put("address2", address2);
        if (city != null) obj.put("city", city);
        if (state != null) obj.put("state", state);
        if (zip != null) obj.put("zip", zip);
        if (country != null) obj.put("country", country);
        if (contactExtension != null && contactExtension.getTld() != null) {
            obj.put("contact_extension", new JSONObject().put("tld", contactExtension.getTld()));
        }
        return obj;
    }
}
