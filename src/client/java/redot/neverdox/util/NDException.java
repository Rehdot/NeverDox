package redot.neverdox.util;

public class NDException extends RuntimeException {

    public NDException(String errorMessage) {
        super(errorMessage);
        Messenger.sendErrorText(errorMessage);
    }

}
