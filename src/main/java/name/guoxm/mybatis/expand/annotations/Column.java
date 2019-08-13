package name.guoxm.mybatis.expand.annotations;

import java.lang.annotation.*;

/**
 * 自定义注解
 * 用于注解列名
 * Created on 2019/5/17.
 * @author guoxm
 */
@Target(ElementType.FIELD) // 说明该注解可以加该注解用于方法或者属性声明
@Retention(RetentionPolicy.RUNTIME)  // 虚让拟机将在运行期也保留注释，这样可以通过反射机制读取注解的信息
@Documented // 将该注解包含在javadoc中，方便使用
@Inherited // 允许子类（派生类）可以继承父类（基类）中的注解
public @interface Column {

    String name() default ""; // 字段名

    String type() default ""; // 字段类型

    int length() default 255; // 字段长度，默认是255

    int decimalLength() default 0;  // 小数点长度，默认是0

    boolean notNull() default false; // 是否为可以为null，false是可以，true是不可以，默认为true

    boolean primaryKey() default false; // 是否是主键，默认false

    boolean autoIncrement() default false; // 是否自动递增，默认false 只有主键才能使用

    String defaultValue() default "NOT_DEFAULT"; // 默认值
}
