package name.guoxm.mybatis.expand;

import com.alibaba.druid.pool.DruidDataSource;
import name.guoxm.mybatis.expand.annotations.ExpandScan;
import name.guoxm.mybatis.expand.mappers.TableMapper;
import name.guoxm.mybatis.expand.options.Option;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 扫描注册器
 * 主要用于扫描定义了 @Table 的类
 * 关键需要实现 ImportBeanDefinitionRegistrar(Bean定义注册接口) 和 ResourceLoaderAware(资源加载通知接口)
 * TODO 其中 ImportBeanDefinitionRegistrar 是spring提供的扩展接口, 其中有两个参数：注解的原数据对象和类的注册信息
 * Created on 2019/7/8.
 * @author guoxm
 */
@SuppressWarnings("all")
public class ExpandScanSelector implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        // 获取某某注解的原数据，从中可以获取注解修饰的具体类信息
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(ExpandScan.class.getName()));

        if (annotationAttributes.getEnum("auto") == Option.NONE) // 如果配置为NONE，说明不用扫描了
            return;

        ExpandClassScanner scanner = new ExpandClassScanner(getSessionFactory());

        List<String> basePackages = new ArrayList<>();

        // 先处理 values 中的信息
        String[] values = annotationAttributes.getStringArray("values");
        buildPackages(values, basePackages);
        scanner.setDBDriver(this.environment.getProperty("spring.datasource.driver-class-name"));
        scanner.setAuto(annotationAttributes.getEnum("auto"));
        try {
            scanner.doScanner(basePackages);
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private SqlSessionFactory getSessionFactory() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(this.environment.getProperty("spring.datasource.url"));
        dataSource.setUsername(this.environment.getProperty("spring.datasource.username"));
        dataSource.setPassword(this.environment.getProperty("spring.datasource.password"));

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        org.apache.ibatis.mapping.Environment environment = new org.apache.ibatis.mapping.Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(TableMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    /**
     * 遍历values中的每一项，将不是空值的项加到basePackages中
     */
    private void buildPackages(String[] values, List<String> basePackages) {
        int length = values.length;
        int index;
        String _package;
        for (index = 0; index < length; ++index) {
            _package = values[index];
            if (StringUtils.hasText(_package))
                basePackages.add(_package);
        }
    }
}
