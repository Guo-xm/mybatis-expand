package org.guoxm.mybatis.expand.execptions;

/**
 * 类型不一致
 * Created on 2019/5/21.
 * @author guoxm
 */
public class DifferentTypeException extends RuntimeException {

    public DifferentTypeException(String message) {
        super(message);
    }
}
