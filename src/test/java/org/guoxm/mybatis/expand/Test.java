package org.guoxm.mybatis.expand;

import com.google.common.base.CaseFormat;
/**
 * 测试
 * Created on 2019/7/23.
 * @author guoxm
 */
public class Test {

    @org.junit.Test
    public void test() {
        String name = "testStringName";
        name = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
        System.out.println(name);
    }
}
