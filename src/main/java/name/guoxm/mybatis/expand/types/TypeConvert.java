package name.guoxm.mybatis.expand.types;

import java.util.Map;

public class TypeConvert {

    public static String convertType(String dbDriver, Class type) {
        if (dbDriver.toLowerCase().contains("mysql"))
            return new MySQLType().convert(type);
        else if (dbDriver.toLowerCase().contains("postgres"))
            return new PostgreSQLType().convert(type);
        else
            return new SQLType().convert(type);
    }

    public static Map<String, Integer> getType2Length(String dbDriver){
        if (dbDriver.toLowerCase().contains("mysql"))
            return new MySQLType().getType2Length();
        else if (dbDriver.toLowerCase().contains("postgres"))
            return new PostgreSQLType().getType2Length();
        else
            return new SQLType().getType2Length();
    }

}
