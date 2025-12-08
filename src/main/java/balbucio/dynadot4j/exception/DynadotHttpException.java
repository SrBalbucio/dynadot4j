package balbucio.dynadot4j.exception;

import lombok.Getter;
import org.json.JSONObject;
import org.jsoup.Connection;

@Getter
public class DynadotHttpException extends RuntimeException {

    private Connection.Response response;
    private JSONObject errorData;
    private int statusCode;

    public DynadotHttpException(Connection.Response response, JSONObject data, int statusCode) {
        this("The Dynadot API returned code " + response.statusCode() + ":" + statusCode + " with the message: " + data.getString("description"), response, data, statusCode);
    }

    public DynadotHttpException(String message, Connection.Response response, JSONObject data, int statusCode) {
        super(message);
        this.response = response;
        this.errorData = data;
        this.statusCode = statusCode;
    }
}
