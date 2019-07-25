package org.guoxm.mybatis.expand;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.guoxm.mybatis.expand.annotations.Column;
import org.guoxm.mybatis.expand.annotations.LengthCount;
import org.guoxm.mybatis.expand.annotations.SubTable;
import org.guoxm.mybatis.expand.annotations.Table;
import org.guoxm.mybatis.expand.constants.Constants;
import org.guoxm.mybatis.expand.constants.PostgreSQLType;
import org.guoxm.mybatis.expand.execptions.ClassNotFindAssociationKeyException;
import org.guoxm.mybatis.expand.execptions.DifferentTypeException;
import org.guoxm.mybatis.expand.execptions.NotFindAssociationKeyException;
import org.guoxm.mybatis.expand.managers.WeCreateTableService;
import org.guoxm.mybatis.expand.mappers.TableMapper;
import org.guoxm.mybatis.expand.structures.WeColumn;
import org.guoxm.mybatis.expand.structures.WeTable;
import org.guoxm.mybatis.expand.tools.ClassTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 自定义类扫描器
 * Created on 2019/7/8.
 * @author guoxm
 */
public class ClassPathScanner implements WeCreateTableService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathScanner.class);

    private Option option;

    private final SqlSessionFactory sqlSessionFactory;
    private TableMapper tableMapper;

    ClassPathScanner(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    void setAuto(Option option) {
        this.option = option;
    }

    void doScanner(List<String> packages) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        this.tableMapper = sqlSession.getMapper(TableMapper.class);
        for (String _package : packages)
            createTable(_package);
        // 确保事务提交
        sqlSession.commit();
        sqlSession.close();
    }

    @Override
    public void createTable(String _package) {
        Map<String, Integer> type2Length = getType2Length();
        Set<Class<?>> classes = ClassTool.getClasses(_package, true);

        List<WeTable> newTables = new ArrayList<>(); // 新增的表
        List<WeTable> updateTables = new ArrayList<>(); // 新增字段的表

        buildTablesConstruct(classes, type2Length, newTables, updateTables);

        createOrUpdateTable(newTables, updateTables);
    }

    private Map<String, Integer> getType2Length() {
        Field[] fields = PostgreSQLType.class.getDeclaredFields();
        Map<String, Integer> result = new TreeMap<>();
        for (Field field : fields) {
            LengthCount lengthCount = field.getAnnotation(LengthCount.class);
            result.put(field.getName().replace("_", " ").toLowerCase(), lengthCount.LengthCount());
        }
        return result;
    }

    private void buildTablesConstruct(Set<Class<?>> classes, Map<String, Integer> type2Length, List<WeTable> newTables, List<WeTable> updateTables) {
        for (Class<?> c : classes) {
            Table table = c.getAnnotation(Table.class);
            if (table == null)
                continue;

            List<WeColumn> allFields = new ArrayList<>(); // 该类所有的字段
            List<WeColumn> newFields = new ArrayList<>(); // 该类新增的字段

            buildTableFieldsConstruct(type2Length, c, allFields); // 迭代出所有model的所有fields存到newFieldList中

            checkCreateSchema(tableMapper, table); // 检查schema是否存在，如果不存在则创建

            checkOption(tableMapper, table); // 检查Option是否需要drop schema

            // 将Table注解中的属性构建成table
            buildTablesConstruct(table, newTables, allFields, updateTables, newFields);

            Map<String, List<WeColumn>> subName2AllFields = new HashMap<>(); // 该类子表所有的字段
            try {
                buildSubTableFieldsConstruct(type2Length, c, allFields, subName2AllFields);
            } catch(NotFindAssociationKeyException e) {
                throw new ClassNotFindAssociationKeyException(c.getName(), e.getMessage());
            }
            // 将SubTable注解中的内容构建成table
            buildSubTablesConstruct(table, newTables, subName2AllFields, updateTables);
        }
    }

    private void buildTablesConstruct(Table table, List<WeTable> newTables, List<WeColumn> allFields, List<WeTable> updateTables, List<WeColumn> newFields) {
        if (tableMapper.findTableCountBySchemaAndName(table.schema(), table.name()) < 1) { //  这里要先判断一下表是否存在
            System.out.println("Table: " + table.schema() + "." + table.name() + " does not exist");
            newTables.add(new WeTable(table.schema(), table.name(), allFields));
        } else {
            List<WeColumn> tableColumns = tableMapper.findTableColumnBySchemaAndName(table.schema(), table.name());
            buildNewFields(allFields, newFields, tableColumns);
            if (newFields.size() > 0)
                updateTables.add(new WeTable(table.schema(), table.name(), newFields));
        }
    }

    private void buildSubTablesConstruct(Table table, List<WeTable> newTables, Map<String, List<WeColumn>> subName2AllFields, List<WeTable> updateTables) {
        for (String name : subName2AllFields.keySet()) {
            if (tableMapper.findTableCountBySchemaAndName(table.schema(), name) < 1) { //  这里要先判断一下表是否存在
                System.out.println("SubTable: " + table.schema() + "." + name + " does not exist");
                newTables.add(new WeTable(table.schema(), name, subName2AllFields.get(name)));
            } else {
                List<WeColumn> tableColumns = tableMapper.findTableColumnBySchemaAndName(table.schema(), name);
                List<WeColumn> newFields = new ArrayList<>();
                buildNewFields(subName2AllFields.get(name), newFields, tableColumns);
                if (newFields.size() > 0)
                    updateTables.add(new WeTable(table.schema(), name, newFields));
            }
        }

    }

    private void checkOption(TableMapper tableMapper, Table table) {
        if (Option.CREATE == option) { // 如果是create，那么需要drop一下
            System.out.println("dropTable: " + table.schema() + "." + table.name());
            LOGGER.debug("dropTable: " + table.schema() + "." + table.name());
            tableMapper.dropTableBySchemaAndName(table.schema(), table.name());
        }
    }

    private void checkCreateSchema(TableMapper tableMapper, Table table) {
        if (tableMapper.findSchemaCountBySchemaName(table.schema()) < 1) {
            System.out.println("createSchema: " + table.schema());
            LOGGER.debug("createSchema: " + table.schema());
            tableMapper.createSchema(table.schema()); // 如果没有schema，那么就需要创建
        }
    }

    /**
     * 把类中的所有Column注解的属性读取出来，构建成对象
     * @param type2Length 类型列表
     * @param c 需要解析的类
     * @param allFields 类中所有注解的属性
     */
    private void buildTableFieldsConstruct(Map<String, Integer> type2Length, Class<?> c, List<WeColumn> allFields){
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields){
            // 判断方法中是否有指定注解类型的注解
            boolean hasAnnotation = field.isAnnotationPresent(Column.class);
            if (hasAnnotation) {
                // 根据注解类型返回方法的指定类型注解
                Column column = field.getAnnotation(Column.class);
                String name = column.name().equals(Constants.NULL) ? field.getName() : column.name();
                String type = column.type().equals(Constants.NULL) ? convertType(field.getType()) : column.type();
                WeColumn param = new WeColumn(name,
                        type.toLowerCase(),
                        column.length(),
                        column.decimalLength(),
                        column.notNull(),
                        column.primaryKey(),
                        column.autoIncrement(),
                        column.defaultValue(),
                        type2Length.get(type.toLowerCase()));
                allFields.add(param);
            }
        }
    }

    /**
     * 把类中的所有SubTable注解的属性读取出来
     * @param type2Length 类型列表
     * @param c 需要解析的类
     * @param subName2AllFields 类中所有注解的属性
     */
    private void buildSubTableFieldsConstruct(Map<String, Integer> type2Length, Class<?> c, List<WeColumn> parentAllFields, Map<String, List<WeColumn>> subName2AllFields) throws NotFindAssociationKeyException {
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields){
            // 判断方法中是否有指定注解类型的注解
            boolean hasAnnotation = field.isAnnotationPresent(SubTable.class);
            if (hasAnnotation) {
                // 根据注解类型返回方法的指定类型注解
                SubTable subTable = field.getAnnotation(SubTable.class);
                String name = subTable.name().equals(Constants.NULL) ? field.getName() : subTable.name();
                String[] associationKeys = subTable.joinColumns();
                Class<?> subClass = subTable.subClass();
                List<WeColumn> subAllFields = new ArrayList<>();
                checkAssociationKey(associationKeys, parentAllFields, subAllFields);
                buildTableFieldsConstruct(type2Length, subClass, subAllFields);
                subName2AllFields.put(name, subAllFields);
            }
        }
    }

    private void checkAssociationKey(String[] associationKeys, List<WeColumn> parentAllFields, List<WeColumn> subAllFields) throws NotFindAssociationKeyException{
        for (String associationKey : associationKeys) {
            boolean flag = false;
            for (WeColumn weColumn : parentAllFields)
                if (weColumn.getName().equals(associationKey)) {
                    subAllFields.add(weColumn); // 如果找到关联键，那就加入到字段中
                    flag = true;
                }
            if (!flag)
                throw new NotFindAssociationKeyException(associationKey);
        }
    }

    private String convertType(Class type) {
        return PostgreSQLType.convertType(type);
    }

    private void buildNewFields(List<WeColumn> allFields, List<WeColumn> newFields, List<WeColumn> tableColumns) throws DifferentTypeException {
        for (WeColumn weColumn : allFields) {
            checkColumnParam(tableColumns, weColumn);
            if (!tableColumns.contains(weColumn)) // 这里依赖ColumnParam中重写equals方法
                newFields.add(weColumn);
        }
    }

    private void checkColumnParam(List<WeColumn> tableColumns, WeColumn weColumn) throws DifferentTypeException {
        for (WeColumn param : tableColumns) {
            // 如果类型不一样，则不能继续
            if (param.getName().equals(weColumn.getName()) && !param.getType().equals(weColumn.getType()))
                throw new DifferentTypeException(weColumn.getName() + " type in " + weColumn.getType() + ", the type in the database is " + param.getType());
        }
    }

    private void createOrUpdateTable(List<WeTable> newTables, List<WeTable> updateTables) {
        createTables(newTables);
        updateTables(updateTables);
    }

    private void createTables(List<WeTable> tables) {
        if (tables.size() == 0)
            return;
        for (WeTable weTable : tables) {
            LOGGER.debug("createTables: " + weTable.getSchema() + "." + weTable.getName());
            this.tableMapper.createTable(weTable);
        }
    }

    private void updateTables(List<WeTable> tables) {
        if (tables.size() == 0)
            return;
        for (WeTable weTable : tables) {
            LOGGER.debug("updateTables: " + weTable.getSchema() + "." + weTable.getName());
            this.tableMapper.alterTable(weTable);
        }
    }


}
