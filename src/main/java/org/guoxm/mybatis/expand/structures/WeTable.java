package org.guoxm.mybatis.expand.structures;

import java.util.List;

/**
 * 用于记录解析出来的结构
 * Created on 2019/5/21.
 * @author guoxm
 */
public class WeTable {

    private String schema;

    private String name;

    private List<WeColumn> columns;

    public WeTable(String schema, String name, List<WeColumn> columns) {
        this.schema = schema;
        this.name = name;
        this.columns = columns;
    }

    public String getSchema() {
        return schema;
    }

    public String getName() {
        return name;
    }

    public List<WeColumn> getColumns() {
        return columns;
    }
}
