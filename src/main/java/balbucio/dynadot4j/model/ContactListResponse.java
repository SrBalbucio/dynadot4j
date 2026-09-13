package balbucio.dynadot4j.model;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ContactListResponse {
    @SerializedName("contact_list")
    private List<Contact> contactList;
}
