package name.guoxm.mybatis.expand.annotations;

import java.lang.annotation.*;

/**
 * 针对数据库类型加注解，用来标记该类型需要设置几个长度
 * 例如： datetime/不需要设置 ,varchar(1)/需要1个, double(5,2)/需要两个
 * Created on 2019/5/17.
 * @author guoxm
 */
@Target(ElementType.FIELD) // 说明该注解可以加在类、接口和枚举上
@Retention(RetentionPolicy.RUNTIME)  // 虚让拟机将在运行期也保留注释，这样可以通过反射机制读取注解的信息
@Documented // 将该注解包含在javadoc中，方便使用
@Inherited // 允许子类（派生类）可以继承父类（基类）中的注解
public @interface LengthCount {
    int LengthCount() default 0; // 默认是0，0表示不需要设置，1表示需要设置一个，2表示需要设置两个
}
