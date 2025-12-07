package balbucio.dynadot4j.exception;

import org.json.JSONObject;
import org.jsoup.Connection;

public class DynadotTooManyRequestException extends DynadotHttpException {

    public DynadotTooManyRequestException(Connection.Response response, JSONObject data) {
        super("You have reached the maximum number of requests per second. Please wait a moment and try again.", response, data);
    }
}
