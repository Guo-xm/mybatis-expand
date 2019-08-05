package name.guoxm.mybatis.expand.types;

import name.guoxm.mybatis.expand.annotations.LengthCount;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class SQLType {

    String convert(Class<?> type) {
        return type.getSimpleName();
    }

    Map<String, Integer> getType2Length() {
        Field[] fields = this.getClass().getDeclaredFields();
        Map<String, Integer> map = new HashMap<>();
        for (Field field : fields){
            LengthCount lengthCount = field.getAnnotation(LengthCount.class);
            map.put(field.getName().toUpperCase().replace("_", " "), lengthCount.LengthCount());
        }
        return map;
    }
}
