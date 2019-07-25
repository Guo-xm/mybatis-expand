package org.guoxm.mybatis.expand.annotations;


import org.guoxm.mybatis.expand.ScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自定义注解
 * 用于注解表名
 * Created on 2019/5/17.
 * @author guoxm
 */
@Target(ElementType.TYPE) // 说明该注解可以加在类、接口和枚举上
@Retention(RetentionPolicy.RUNTIME)  // 虚让拟机将在运行期也保留注释，这样可以通过反射机制读取注解的信息
@Documented // 将该注解包含在javadoc中，方便使用
@Inherited // 允许子类（派生类）可以继承父类（基类）中的注解
public @interface Table {

    String schema(); // 可以指定schema

    String name(); // 这里简单就一个表名
}
