package org.guoxm.mybatis.expand.test;

import org.guoxm.mybatis.expand.annotations.Column;
import org.guoxm.mybatis.expand.annotations.SubTable;
import org.guoxm.mybatis.expand.annotations.Table;
import org.guoxm.mybatis.expand.constants.PostgreSQLType;

import java.util.List;

/**
 * 测试类
 * Created on 2019/5/17.
 * @author guoxm
 */
@Table(schema = "guoxm", name = "hero")
public class Hero {
    @Column
    private int id; // 编号
    @Column
    private String name; // 名称
    @Column
    private double height; // 身高
    @Column
    private String career; // 职业
    @Column
    private int age; // 年龄
    @Column(type = PostgreSQLType.INTEGER)
    private Six six; // 性别
    @Column
    private String birthplace; // 籍贯
    @SubTable(name = "tests", joinColumns = {"id", "name"}, subClass = Test.class)
    private List<Test> tests;
}
