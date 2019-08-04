package name.guoxm.mybatis.expand.structures;

import lombok.Data;

import java.util.List;

/**
 * 用于记录解析出来的结构
 * Created on 2019/5/21.
 * @author guoxm
 */
@Data
public class TableMate {

    private String schema;

    private String name;

    private List<ColumnMate> columns;

    public TableMate(String schema, String name, List<ColumnMate> columns) {
        this.schema = schema;
        this.name = name;
        this.columns = columns;
    }

}
