package org.guoxm.mybatis.expand;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.guoxm.mybatis.expand.annotations.ExpandScan;
import org.guoxm.mybatis.expand.mappers.TableMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class ScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScannerRegistrar.class);

    private ResourceLoader resourceLoader;

    private String username;
    private String password;
    private String url;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        getConfig();
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        SqlSessionFactory sqlSessionFactory;
        try {
            sqlSessionFactory = getSessionFactory();

        } catch(NamingException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // 获取某某注解的原数据，从中可以获取注解修饰的具体类信息
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(ExpandScan.class.getName()));
        //
        ClassPathScanner scanner = new ClassPathScanner(sqlSessionFactory);

        List<String> basePackages = new ArrayList<>();

        // 先处理 values 中的信息
        String[] values = annotationAttributes.getStringArray("values");
        buildPackages(values, basePackages);
        // 再处理 basePackages 中的信息
        values = annotationAttributes.getStringArray("basePackages");
        buildPackages(values, basePackages);
        scanner.setAuto(annotationAttributes.getEnum("auto"));
        scanner.doScanner(basePackages);
    }

    private SqlSessionFactory getSessionFactory() throws NamingException, ClassNotFoundException,
            IllegalAccessException, InstantiationException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(TableMapper.class);
        return new SqlSessionFactoryBuilder().build(configuration);
    }

    private void getConfig() {
        Resource resource = resourceLoader.getResource("classpath:application.yml");
        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            boolean flag1 = false;
            boolean flag2 = false;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("spring:"))
                    flag1 = true;
                if (flag1 && line.trim().equalsIgnoreCase("datasource:"))
                    flag2 = true;
                if (flag1 && flag2 && line.toLowerCase().trim().contains("url:"))
                    url = line.trim().substring(line.trim().indexOf(":") + 1).trim();
                if (flag1 && flag2 && line.toLowerCase().trim().contains("username:"))
                    username = line.trim().substring(line.trim().indexOf(":") + 1).trim();
                if (flag1 && flag2 && line.toLowerCase().trim().contains("password:"))
                    password = line.trim().substring(line.trim().indexOf(":") + 1).trim();
            }
            LOGGER.debug("url=" + url);
            LOGGER.debug("username=" + username);
            LOGGER.debug("password=" + password);
        } catch(IOException e) {
            e.printStackTrace();
        }
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
