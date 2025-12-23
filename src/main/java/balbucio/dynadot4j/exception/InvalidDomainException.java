package balbucio.dynadot4j.exception;

import java.util.List;

public class InvalidDomainException extends RuntimeException {

    public InvalidDomainException(String domain) {
        super("The domain \"" + domain + "\" is invalid, please enter a valid one.");
    }

    public InvalidDomainException(List<String> domain) {
        this(String.join(",", domain));
    }

}
