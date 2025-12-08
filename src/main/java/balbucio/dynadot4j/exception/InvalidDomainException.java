package balbucio.dynadot4j.exception;

public class InvalidDomainException extends RuntimeException {

    public InvalidDomainException(String domain) {
        super("The domain \"" + domain + "\" is invalid, please enter a valid one.");
    }

}
