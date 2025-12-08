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
}
