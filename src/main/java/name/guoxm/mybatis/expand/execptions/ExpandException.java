package name.guoxm.mybatis.expand.execptions;

/**
 * 自定义运行时异常类，该类为Expand中的基础异常类
 * 其中的异常信息通过 message 属性可以获取
 * Create on 2019/7/15
 * @author guoxm
 */
public class ExpandException extends RuntimeException {

    protected String message;

    public ExpandException(String message) {
        super(message);
        this.message = message;
    }
}
