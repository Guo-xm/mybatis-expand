package name.guoxm.mybatis.expand.tools;

import name.guoxm.mybatis.expand.annotations.Table;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 寻找使用了某个注解的类的工具
 * 参考PathMatchingResourcePatternResolver中的findAllClassPathResources方法
 * Created on 2019/8/5.
 * @author guoxm
 */
public class FindAnnotationClassUtil {

    /**
     * 在包中找到使用某个注解的类
     * @param packageName 包
     * @param recursive 是否递归查找
     * @param annotation 注解
     * @return 结果，用Set是为了避免重复定义的类
     */
    public static Set<Class<?>> findBeanDefinition(String packageName , boolean recursive, Class<? extends Annotation> annotation) throws IOException, ClassNotFoundException {
        Set<Class<?>> candidates = new LinkedHashSet<>(); // 用LinkedHashSet是用于根据找到的顺序进行处理
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        // 获取指定包下的所有类
        String str = packageName.replaceAll("\\.", "\\\\");
        if (recursive)
            str += "\\\\**\\\\*"; // 组装后的结果应该是 name\guoxm\mybatis\expand\**\* 这样的格式
        Resource[] resources = resourcePatternResolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + str); // 组装后通过spring的资源解析器获取该包下所有资源
        MetadataReaderFactory metadata = new SimpleMetadataReaderFactory();
        for (Resource resource : resources) {
            if (!resource.isReadable()) // 资源是否可读
                continue;
            // 通过元数据读取器读取该资源中的元数据
            MetadataReader metadataReader = metadata.getMetadataReader(resource);
            // 将元数据转换成Bean
            ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
            sbd.setResource(resource);
            sbd.setSource(resource);
            // 该Bean是否有annotation注解
            if (Class.forName(sbd.getBeanClassName()).getAnnotation(annotation) != null)
                candidates.add(Class.forName(sbd.getBeanClassName()));
        }
        return candidates;
    }
}
