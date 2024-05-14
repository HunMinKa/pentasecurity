package pentasecurity.task.exception;

public class MemberAlreadyExistException extends RuntimeException {

    public MemberAlreadyExistException(String message) {
        super(message);
    }

}