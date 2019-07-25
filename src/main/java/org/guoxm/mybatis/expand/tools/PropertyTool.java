package org.guoxm.mybatis.expand.tools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created on 2019/5/20.
 * @author guoxm
 */
public class PropertyTool {

    public static <T, E> List<E> getPropertyValueList(List<T> objList, String fieldName){
        List<E> list = new ArrayList<>();
        try {
            for (T object : objList){
                Field field = object.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                list.add((E) field.get(object));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
