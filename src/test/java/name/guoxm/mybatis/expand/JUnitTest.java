package name.guoxm.mybatis.expand;


import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JUnitTest {

    @Test
    public void test() {

        String[] str = new String[3];
        str[0] = "guoxm1";
        str[1] = "guoxm2";
        str[2] = "guoxm3";
        Arrays.stream(str).forEach(a -> System.out.println(a));
        List<String> list = Arrays.stream(str).distinct().map(key -> {return key;}).collect(Collectors.toList());
        for (String s : list)
            System.out.println(s);
        list = Arrays.stream(str).filter(key -> key.endsWith("1")).collect(Collectors.toList());
        for (String s : list)
            System.out.println(s);
    }

    @Test
    public void driver() throws ClassNotFoundException {
        String name = "com.mysql.cj.jdbc.Driver";
        if (name.toLowerCase().contains("mysql")) {
            System.out.println("mysql");
        } else if (name.toLowerCase().contains("postgres")) {
            System.out.println("postgres");
        } else {
            System.out.println("other");
        }
    }
}
