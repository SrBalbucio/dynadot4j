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
    @SerializedName("address1")
    private String address;
    private String city;
    private String state;
    private String country;


    public JSONObject toJSON(){
        JSONObject obj = new JSONObject();
        obj.put("organization", organization);
        obj.put("name", name);
        obj.put("email", email);
        obj.put("phone_number", phoneNumber);
        obj.put("phone_cc", phoneCC);
        obj.put("address", address);
        obj.put("city", city);
        obj.put("state", state);
        obj.put("country", country);
        return obj;
    }
}
