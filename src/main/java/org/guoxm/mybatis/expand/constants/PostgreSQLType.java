package org.guoxm.mybatis.expand.constants;

import org.guoxm.mybatis.expand.annotations.LengthCount;

/**
 * 类型常量
 * Created on 2019/5/17.
 * @author guoxm
 * @see LengthCount
 */
public class PostgreSQLType {

    /**
     * 数字类型
     */

    @LengthCount
    public static final String INTEGER = "INTEGER";

    @LengthCount
    public static final String SMALLINT = "SMALLINT";

    @LengthCount
    public static final String BIGINT = "BIGINT";

    @LengthCount
    public static final String DECIMAL = "DECIMAL";

    @LengthCount
    public static final String DOUBLE_PRECISION = "DOUBLE PRECISION";

    @LengthCount
    public static final String NUMERIC = "NUMERIC";

    @LengthCount
    public static final String REAL = "REAL";

    @LengthCount
    public static final String SERIAL = "SERIAL";

    @LengthCount
    public static final String BIGSERIAL = "BIGSERIAL";

    /**
     * 字符串类型
     */

    @LengthCount(LengthCount = 1)
    public static final String CHAR = "CHAR";

    @LengthCount(LengthCount = 1)
    public static final String CHARACTER = "CHARACTER";

    @LengthCount(LengthCount = 1)
    public static final String VARCHAR = "VARCHAR";

    @LengthCount
    public static final String TEXT = "text";

    /**
     * 日期类型
     */

    @LengthCount
    public static final String TIMESTAMP = "TIMESTAMP";

    @LengthCount
    public static final String DATE = "DATE";

    @LengthCount
    public static final String TIME = "TIME";

    public static String convertType(Class type) {
        switch(type.getSimpleName().toLowerCase()) {
        case "int":
        case "integer":
        case "byte":
            return INTEGER;
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
        case "boolean":
            return TEXT;
            default:
                System.out.println(type.getName());
                return null;
        }
    }
}
