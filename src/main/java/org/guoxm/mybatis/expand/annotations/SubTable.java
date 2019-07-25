package org.guoxm.mybatis.expand.annotations;

import java.lang.annotation.*;

/**
 * 子表注解
 * Created on 2019/7/25.
 * @author guoxm
 */
@Target(ElementType.FIELD) // 说明该注解可以加该注解用于方法或者属性声明
@Retention(RetentionPolicy.RUNTIME)  // 虚让拟机将在运行期也保留注释，这样可以通过反射机制读取注解的信息
@Documented // 将该注解包含在javadoc中，方便使用
@Inherited // 允许子类（派生类）可以继承父类（基类）中的注解
public @interface SubTable {

    String name(); // 这里简单就一个子表名

    String[] joinColumns(); // 管理字段

    Class<?> subClass(); // 对应的类型
}
