package name.guoxm.mybatis.expand.execptions;

public class ExpandException extends RuntimeException {

    protected String message;

    public ExpandException(String message) {
        super(message);
        this.message = message;
    }
}
