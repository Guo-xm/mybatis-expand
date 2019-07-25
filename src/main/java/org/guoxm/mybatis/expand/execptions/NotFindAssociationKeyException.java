package org.guoxm.mybatis.expand.execptions;

/**
 * 找不到关联键异常
 * Created on 2019/7/25.
 * @author guoxm
 */
public class NotFindAssociationKeyException extends RuntimeException {

    public NotFindAssociationKeyException(String key) {
        super("Cannot find association " + key);
    }
}
