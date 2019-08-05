package name.guoxm.mybatis.expand.types;

import name.guoxm.mybatis.expand.annotations.LengthCount;

@SuppressWarnings("all")
public class MySQLType extends SQLType {
    /**
     *  数值类型
     */
    @LengthCount
    public final static String SMALLINT = "SMALLINT"; // 大整数值
    @LengthCount
    public final static String INTEGER = "INTEGER";  // 大整数值
    @LengthCount
    public final static String BIGINT = "BIGINT"; // 极大整数值
    @LengthCount
    public final static String TINYINT = "TINYINT"; // 小整数值
    @LengthCount
    public final static String FLOAT = "FLOAT"; // 单精度浮点数值
    @LengthCount
    public final static String DOUBLE = "DOUBLE"; // 双精度浮点数值
    @LengthCount
    public final static String DECIMAL = "DECIMAL"; // 小数值


    /**
     * 字符/字符串类型
     */
    @LengthCount(LengthCount = 1) // 说明需要指定长度
    public final static String VARCHAR = "VARCHAR"; // 字符/字符串，可变长度，最大长度 n
    @LengthCount(LengthCount = 1)
    public final static String CHARACTER = "CHARACTER"; // 字符/字符串，固定长度 n
    @LengthCount
    public final static String TINYBLOB = "TINYBLOB"; // 不超过 255 个字符的二进制字符串
    @LengthCount
    public final static String TINYTEXT = "TINYTEXT"; // 短文本字符串
    @LengthCount
    public final static String BLOB = "BLOB"; // 二进制形式的长文本数据
    @LengthCount
    public final static String TEXT = "TEXT"; // 长文本数据
    @LengthCount
    public final static String MEDIUMBLOB = "MEDIUMBLOB"; // 二进制形式的中等长度文本数据
    @LengthCount
    public final static String MEDIUMTEXT = "MEDIUMTEXT"; // 中等长度文本数据
    @LengthCount
    public final static String LONGBLOB = "LONGBLOB"; // 二进制形式的极大文本数据
    @LengthCount
    public final static String LONGTEXT = "LONGTEXT"; // 极大文本数据


    /**
     * 日期时间类型
     */
    @LengthCount
    public final static String DATA = "DATA"; // 日期值
    @LengthCount
    public final static String TIME = "TIME"; // 时间值或持续时间
    @LengthCount
    public final static String TIMESTAMP = "TIMESTAMP"; // 混合日期和时间值，时间戳
    @LengthCount
    public final static String YEAR = "YEAR"; // 年份值
    @LengthCount
    public final static String DATETIME = "DATETIME"; // 混合日期和时间值

    @LengthCount
    public final static String BOOLEAN = "TINYTEXT(1)";


    String convert(Class<?> type) {
        switch(type.getSimpleName().toLowerCase()) {
            case "int":
            case "integer":
                return INTEGER;
            case "byte":
                return BLOB;
            case "short":
                return SMALLINT;
            case "long":
                return BIGINT;
            case "char":
                return VARCHAR;
            case "float":
                return FLOAT;
            case "double":
                return DOUBLE;
            case "string":
                return TEXT;
            case "boolean":
                return BOOLEAN;
            default:
                return type.getSimpleName();
        }
    }

}
