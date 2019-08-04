package name.guoxm.mybatis.expand.types;

import name.guoxm.mybatis.expand.annotations.LengthCount;

@SuppressWarnings("all")
public class PostgreSQLType extends SQLType {

    /**
     * 数字类型
     */
    @LengthCount
    public final static String INTEGER = "INTEGER"; // 常用的整数
    @LengthCount
    public final static String SMALLINT = "SMALLINT"; // 小范围整数
    @LengthCount
    public final static String BIGINT = "BIGINT"; // 大范围整数
    @LengthCount
    public final static String DECIMAL = "DECIMAL"; // 用户指定的精度，精确
    @LengthCount
    public final static String DOUBLE_PRECISION = "DOUBLE PRECISION"; // 可变精度，不精确
    @LengthCount
    public final static String NUMERIC = "NUMERIC"; // 用户指定的精度，精确
    @LengthCount
    public final static String REAL = "REAL"; // 可变精度，不精确
    @LengthCount
    public final static String SERIAL = "SERIAL"; // 自增整数
    @LengthCount
    public final static String BIGSERIAL = "BIGSERIAL"; // 增的大范围整数
    @LengthCount
    public final static String SMALLSERIAL = "SMALLSERIAL"; // 自增的小范围整数

    /**
     * 字符串类型
     */
    @LengthCount(LengthCount = 1)
    public final static String CHAR = "CHAR"; // 变长，有长度限制
    @LengthCount(LengthCount = 1)
    public final static String CHARACTER = "CHARACTER"; // 定长,不足补空白
    @LengthCount(LengthCount = 1)
    public final static String VARCHAR = "VARCHAR"; // 变长，有长度限制
    @LengthCount
    public final static String TEXT = "TEXT"; // 变长，无长度限制

    /**
     * 日期类型
     */
    @LengthCount
    public final static String TIMESTAMP = "TIMESTAMP";
    @LengthCount
    public final static String DATE = "DATE";
    @LengthCount
    public final static String TIME = "TIME";
    @LengthCount
    public final static String INTERVAL = "INTERVAL"; // 时间间隔

    /**
     *  布尔类型
     */
    @LengthCount
    public final static String BOOLEAN = "BOOLEAN";

    /**
     * 货币类型
     */
    @LengthCount
    public final static String MONEY = "MONEY"; // 货币金额

    /**
     * 字节流
     */
    @LengthCount
    public final static String BYTEA = "BYTEA"; // 货币金额

    String convert(Class<?> type) {
        switch(type.getSimpleName().toLowerCase()) {
            case "int":
            case "integer":
                return INTEGER;
            case "byte":
                return BYTEA;
            case "short":
                return SMALLINT;
            case "long":
                return BIGINT;
            case "char":
                return CHAR;
            case "float":
                return REAL;
            case "double":
                return DOUBLE_PRECISION;
            case "string":
                return TEXT;
            case "boolean":
                return BOOLEAN;
            default:
                return type.getSimpleName();
        }
    }
}
