package balbucio.dynadot4j.model;


import com.google.gson.Gson;
import org.json.JSONObject;

import java.util.Map;

public class DynadotHttpResponse {

    private String code;
    private String message;
    private Object data;

    public String asString(){
        return (String) data;
    }

    public Map<String, Object> asMap(){
        return (Map<String, Object>) data;
    }

    public JSONObject asJSON(){
        return new JSONObject(asMap());
    }

    public <T> T asClazz(Gson gson, Class<T> clazz){
        return gson.fromJson(asJSON().toString(), clazz);
    }
}
