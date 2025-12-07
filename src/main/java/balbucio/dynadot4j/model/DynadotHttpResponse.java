package balbucio.dynadot4j.model;


import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

public class DynadotHttpResponse {

    @SerializedName("Code")
    private String code;
    @SerializedName("Message")
    private String message;
    @SerializedName("Data")
    private Object data;

    public String asString(){
        return (String) data;
    }

    public JSONObject asJSON(){
        return new JSONObject(asString());
    }

    public <T> T asClazz(Gson gson, Class<T> clazz){
        return gson.fromJson(asString(), clazz);
    }
}
