package balbucio.dynadot4j.exception;

import lombok.Getter;
import org.json.JSONObject;
import org.jsoup.Connection;

@Getter
public class DynadotHttpException extends RuntimeException {

    private Connection.Response response;
    private JSONObject errorData;

    public DynadotHttpException(Connection.Response response, JSONObject data) {
        this("The Dynadot API returned code " + response.statusCode() + " with the message: " + data.getString("description"), response, data);
    }

    public DynadotHttpException(String message, Connection.Response response, JSONObject data) {
        super(message);
        this.response = response;
        this.errorData = data;
    }
}
