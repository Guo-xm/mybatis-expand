package org.guoxm.mybatis.expand;

import org.guoxm.mybatis.expand.annotations.ExpandScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 拓展
 * Created on 2019/5/20.
 * @author guoxm
 */

@SpringBootApplication
@MapperScan("org.guoxm.mybatis.expand") // 扫描指定包下的注解，主要就是加载组件
@ExpandScan(values = "org.guoxm.mybatis.expand", basePackages = "org.guoxm.mybatis.expand", auto = Option.UPDATE)
@SuppressWarnings("SpringJavaAutowiringInspection")
public class WeExpandMain implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WeExpandMain.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

    }
}
