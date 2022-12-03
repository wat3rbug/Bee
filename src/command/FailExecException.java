package command;

public class FailExecException extends RuntimeException {

    private static final long serialVersionUID = 7718828512143293558L;

    public FailExecException(String message, Throwable error) {
        super(message, error);
    }
    public FailExecException(String message) {
        super(message);
    }
}