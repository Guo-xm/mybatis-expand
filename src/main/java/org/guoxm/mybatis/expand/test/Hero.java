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
@SuppressWarnings("all")
public class Hero {
    @Column(primaryKey = true, autoIncrement = true)
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
    private Sex sex; // 性别
    @Column
    private String birthplace; // 籍贯
    @SubTable(name = "skill", joinColumns = {"id", "name"}, subClass = Skill.class)
    private List<Skill> skills; // 技能
}
