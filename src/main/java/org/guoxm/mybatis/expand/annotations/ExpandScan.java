package org.guoxm.mybatis.expand.annotations;

import org.guoxm.mybatis.expand.Option;
import org.guoxm.mybatis.expand.ScannerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 扩展扫描注解，用于定义
 * Created on 2019/7/8.
 * @author guoxm
 */
@Target(ElementType.TYPE) // 说明该注解可以加在类、接口和枚举上
@Retention(RetentionPolicy.RUNTIME)  // 虚让拟机将在运行期也保留注释，这样可以通过反射机制读取注解的信息
@Documented // 将该注解包含在javadoc中
@Import({ ScannerRegistrar.class}) // TODO 注意：必须这样做，通过@Import把扫描注册类导入到 spring 容器中
public @interface ExpandScan {

    /*
      下面两个属性其实最终的效果是一样的，都只是为了让开发者告知我们包名；
      之所以提供两个名称，是为了让开发者可以知道可以指定到具体的类，也可以指定到包；
     */

    String[] values() default {};

    String[] basePackages() default {};

    Option auto() default Option.CREATE;

}
