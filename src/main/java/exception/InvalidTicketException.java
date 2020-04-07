package exception;

public class InvalidTicketException extends RuntimeException {
    public InvalidTicketException() {
        super();
    }

    public InvalidTicketException(String message) {
        super(message);
    }
}
