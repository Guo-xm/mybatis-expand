package org.guoxm.mybatis.expand.execptions;

/**
 * 类中找不到关联键
 * Created on 2019/7/25.
 * @author guoxm
 */
public class ClassNotFindAssociationKeyException extends RuntimeException {

    public ClassNotFindAssociationKeyException(String className, String message) {
        super(className + ": " + message);
    }
}
