package org.guoxm.mybatis.expand;

import org.guoxm.mybatis.expand.annotations.Column;
import org.guoxm.mybatis.expand.annotations.Table;
import org.guoxm.mybatis.expand.constants.PostgreSQLType;

/**
 * 测试类
 * Created on 2019/5/17.
 * @author guoxm
 */
@Table(schema = "guoxm", name = "hero")
public class Hero {
    @Column(name = "id", type= PostgreSQLType.INTEGER, notNull = false, primaryKey = true, autoIncrement = true)
    private int id; // 编号
    @Column(name = "name", type= PostgreSQLType.TEXT)
    private String name; // 名称
    @Column(name = "height", type= PostgreSQLType.DOUBLE_PRECISION)
    private double height; // 身高
    @Column(name = "career", type= PostgreSQLType.TEXT)
    private String career; // 职业
}
