import name.guoxm.mybatis.expand.annotations.Table;
import name.guoxm.mybatis.expand.tools.FindAnnotationClassUtil;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Administrator on 2019/8/13.
 */
public class ExpandTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        Set<BeanDefinition> candidates = new LinkedHashSet<>();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        //这里特别注意一下类路径必须这样写
        //获取指定包下的所有类
        String str = "name.guoxm.**.*".replaceAll("\\.", "\\\\");
        Resource[] resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + str);
        MetadataReaderFactory metadata = new SimpleMetadataReaderFactory();
        for (Resource resource : resources) {
            if (!resource.isReadable())
                continue;
            MetadataReader metadataReader = metadata.getMetadataReader(resource);
            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
            sbd.setResource(resource);
            sbd.setSource(resource);
            if (Class.forName(sbd.getBeanClassName()).getAnnotation(Table.class) != null)
                candidates.add(sbd);
        }
        for (BeanDefinition beanDefinition : candidates) {
            String classname = beanDefinition.getBeanClassName();
            System.out.println(classname);
        }
    }

    @Test
    public void test2() throws IOException, ClassNotFoundException {
        Set<Class<?>> candidates = FindAnnotationClassUtil.findBeanDefinition("name.guoxm.mybatis.expand", true, Table.class);
        for (Class beanDefinition : candidates) {
            String classname = beanDefinition.getName();
            System.out.println(classname);
        }
    }
}
