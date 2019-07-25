package org.guoxm.mybatis.expand.mappers;

import com.google.common.base.CaseFormat;
import org.guoxm.mybatis.expand.structures.WeColumn;
import org.guoxm.mybatis.expand.structures.WeTable;

/**
 * Created on 2019/7/15.
 * @author guoxm
 */
@SuppressWarnings("all")
public class PostgreSQLProvider {
    public String createTable(WeTable weTable) {
        StringBuilder result = new StringBuilder();
        result.append("CREATE TABLE ").append(weTable.getSchema()).append(".").append(weTable.getName());
        result.append("(");
        for (WeColumn column : weTable.getColumns()) {
            result.append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column.getName())).append(" ");
            if (!column.isAutoIncrement())
                result.append(column.getType()).append(" ");
            else
                result.append("SERIAL").append(" ");
            if (column.isKey())
                result.append("PRIMARY KEY").append(" ");
            if (!column.isNull())
                result.append("NOT NULL").append(" ");
            result.append(",");
        }
        result.append(")");
        String res = result.toString().replaceAll(" +"," ").replaceAll(" ,", ", ").replaceAll(", \\)", ")");
        System.out.println("---------------------------");
        System.out.println("sql = " + res);
        return res;
    }

    public String alterTable(WeTable weTable) {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ").append(weTable.getSchema()).append(".").append(weTable.getName());
        for (WeColumn column : weTable.getColumns()) {
            result.append(" ").append("ADD COLUMN").append(" ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column.getName()));
            if (!column.isAutoIncrement())
                result.append(" ").append(column.getType());
            else
                result.append(" ").append("SERIAL");
            if (column.isKey())
                result.append(" ").append("PRIMARY KEY");
            if (!column.isNull())
                result.append(" ").append("NOT NULL");
            result.append(",");
        }
        result.delete(result.length() - 1, result.length());
        String res = result.toString().replaceAll(" +"," ").replaceAll(" ,", ", ");
        System.out.println("---------------------------");
        System.out.println("sql = " + res);
        return result.toString();
    }
}
