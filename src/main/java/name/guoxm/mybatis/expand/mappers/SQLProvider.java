package name.guoxm.mybatis.expand.mappers;

import com.google.common.base.CaseFormat;
import name.guoxm.mybatis.expand.structures.ColumnMate;
import name.guoxm.mybatis.expand.structures.TableMate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/7/15.
 * @author guoxm
 */
@SuppressWarnings("all")
public class SQLProvider {
    public String createTable(TableMate tableMate, String dbDriver) {
        StringBuilder result = new StringBuilder();
        result.append("CREATE TABLE ").append(tableMate.getSchema()).append(".").append(tableMate.getName());
        result.append("(");
        List<String> primaryKeys = new ArrayList<>();
        for (ColumnMate column : tableMate.getColumns()) {
            result.append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column.getName())).append(" ");
            autoIncrement(result, column, dbDriver);
            if (column.isPrimaryKey())
                primaryKeys.add(column.getName());
            if (column.isNotNull())
                result.append("NOT NULL").append(" ");
            if (!column.getDefaultValue().equals("NOT_DEFAULT"))
                result.append("DEFAULT").append(" ").append("'").append(column.getDefaultValue()).append("'").append(" ");
            result.append(",");
        }
        StringBuilder keys = buildPrimaryKey(primaryKeys);
        if (keys != null)
            result.append(buildPrimaryKey(primaryKeys));
        result.append(")");
        String res = result.toString().replaceAll(" +"," ").replaceAll(" ,", ", ").replaceAll(", \\)", ")").replaceAll(",\\)", ")");
        System.out.println("---------------------------");
        System.out.println("sql = " + res);
        return res;
    }

    private void autoIncrement(StringBuilder result, ColumnMate column, String dbDriver) {
        if (dbDriver.toLowerCase().contains("postgres")) {
            if (!column.isAutoIncrement())
                buildType(result, column);
            else
                result.append("SERIAL").append(" "); // auto_increment
        } else {
            buildType(result, column);
            if (column.isAutoIncrement())
                result.append("AUTO_INCREMENT").append(" ");
        }
    }

    private void buildType(StringBuilder result, ColumnMate column) {
        if (column.getTypeLength() == null || column.getTypeLength() == 0)
            result.append(column.getType()).append(" ");
        else if (column.getTypeLength() == 1)
            result.append(column.getType()).append("(").append(column.getLength()).append(")").append(" ");
        else
            result.append(column.getType()).append("(").append(column.getLength()).append(",").append(column.getDecimalLength()).append(")").append(" ");
    }

    private StringBuilder buildPrimaryKey(List<String> primaryKeys) {
        if (primaryKeys.size() == 0)
            return null;
        StringBuilder result = new StringBuilder();
        result.append("PRIMARY KEY").append("(");
        for (String primaryKey : primaryKeys)
            result.append(primaryKey).append(",");
        result.append(")");
        return result;
    }

    public String alterTable(TableMate tableMate, String dbDriver) {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ").append(tableMate.getSchema()).append(".").append(tableMate.getName());
        for (ColumnMate column : tableMate.getColumns()) {
            result.append(" ").append("ADD COLUMN").append(" ").append(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column.getName()));
            result.append(" ");
            autoIncrement(result, column, dbDriver);
            if (column.isPrimaryKey())
                result.append(" ").append("PRIMARY KEY");
            if (column.isNotNull())
                result.append(" ").append("NOT NULL");
            if (!column.getDefaultValue().equals("NOT_DEFAULT"))
                result.append("DEFAULT").append(" ").append("'").append(column.getDefaultValue()).append("'").append(" ");
            result.append(",");
        }
        result.delete(result.length() - 1, result.length());
        String res = result.toString().replaceAll(" +"," ").replaceAll(" ,", ", ");
        System.out.println("---------------------------");
        System.out.println("sql = " + res);
        return result.toString();
    }
}
