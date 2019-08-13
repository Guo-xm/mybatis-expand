package name.guoxm.mybatis.expand;

import name.guoxm.mybatis.expand.annotations.Column;
import name.guoxm.mybatis.expand.annotations.SubTable;
import name.guoxm.mybatis.expand.annotations.Table;
import name.guoxm.mybatis.expand.execptions.ExpandException;
import name.guoxm.mybatis.expand.mappers.TableMapper;
import name.guoxm.mybatis.expand.options.Option;
import name.guoxm.mybatis.expand.structures.ColumnMate;
import name.guoxm.mybatis.expand.structures.TableMate;
import name.guoxm.mybatis.expand.tools.FindAnnotationClassUtil;
import name.guoxm.mybatis.expand.types.TypeConvert;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 类扫描器
 * Created on 2019/7/8.
 * @author guoxm
 */
@SuppressWarnings("all")
public class ExpandClassScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpandClassScanner.class);

    private Option option;
    private String dbDriver;

    private final SqlSessionFactory sqlSessionFactory;
    private TableMapper tableMapper;

    ExpandClassScanner(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    void setAuto(Option option) {
        this.option = option;
    }

    void setDBDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    void doScanner(List<String> packages) throws ExpandException, IOException, ClassNotFoundException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        this.tableMapper = sqlSession.getMapper(TableMapper.class);
        for (String _package : packages)
            createTable(_package);
        // 确保事务提交
        sqlSession.commit();
        sqlSession.close();
    }

    private void createTable(String _package) throws ExpandException, IOException, ClassNotFoundException {
        Map<String, Integer> type2Length = getType2Length();
        Set<Class<?>> classes = FindAnnotationClassUtil.findBeanDefinition(_package, true, Table.class);

        List<TableMate> newTables = new ArrayList<>(); // 新增的表
        List<TableMate> updateTables = new ArrayList<>(); // 新增字段的表

        buildTablesConstruct(classes, type2Length, newTables, updateTables);

        createOrUpdateTable(newTables, updateTables);
    }

    private Map<String, Integer> getType2Length() {
        return TypeConvert.getType2Length(this.dbDriver);
    }

    private void buildTablesConstruct(Set<Class<?>> classes, Map<String, Integer> type2Length, List<TableMate> newTables, List<TableMate> updateTables) throws ExpandException {
        for (Class<?> c : classes) {
            Table table = c.getAnnotation(Table.class);

            List<ColumnMate> allFields = new ArrayList<>(); // 该类所有的字段
            List<ColumnMate> newFields = new ArrayList<>(); // 该类新增的字段

            buildTableFieldsConstruct(type2Length, c, allFields); // 迭代出所有model的所有fields存到newFieldList中

            checkCreateSchema(tableMapper, table); // 检查schema是否存在，如果不存在则创建

            checkOption(tableMapper, table); // 检查Option是否需要drop schema

            // 将Table注解中的属性构建成table
            buildTablesConstruct(table, newTables, allFields, updateTables, newFields);

            Map<String, List<ColumnMate>> subName2AllFields = new HashMap<>(); // 该类子表所有的字段
            try {
                buildSubTableFieldsConstruct(type2Length, c, allFields, subName2AllFields);
            } catch(ExpandException e) {
                throw new ExpandException("Class " + c.getName() + " did " + e.getMessage());
            }
            // 将SubTable注解中的内容构建成table
            buildSubTablesConstruct(table, newTables, subName2AllFields, updateTables);
        }
    }

    private void buildTablesConstruct(Table table, List<TableMate> newTables, List<ColumnMate> allFields, List<TableMate> updateTables, List<ColumnMate> newFields) {
        if (tableMapper.findTableCountBySchemaAndName(table.schema(), table.name()) < 1) { //  这里要先判断一下表是否存在
            newTables.add(new TableMate(table.schema(), table.name(), allFields));
        } else {
            List<ColumnMate> tableColumns = tableMapper.findTableColumnBySchemaAndName(table.schema(), table.name());
            buildNewFields(allFields, newFields, tableColumns);
            if (newFields.size() > 0)
                updateTables.add(new TableMate(table.schema(), table.name(), newFields));
        }
    }

    private void buildSubTablesConstruct(Table table, List<TableMate> newTables, Map<String, List<ColumnMate>> subName2AllFields, List<TableMate> updateTables) {
        for (String name : subName2AllFields.keySet()) {
            if (tableMapper.findTableCountBySchemaAndName(table.schema(), name) < 1) { //  这里要先判断一下表是否存在
                newTables.add(new TableMate(table.schema(), name, subName2AllFields.get(name)));
            } else {
                List<ColumnMate> tableColumns = tableMapper.findTableColumnBySchemaAndName(table.schema(), name);
                List<ColumnMate> newFields = new ArrayList<>();
                buildNewFields(subName2AllFields.get(name), newFields, tableColumns);
                if (newFields.size() > 0)
                    updateTables.add(new TableMate(table.schema(), name, newFields));
            }
        }

    }

    private void checkOption(TableMapper tableMapper, Table table) {
        if (Option.CREATE == option) { // 如果是create，那么需要drop一下
            LOGGER.debug("dropTable: " + table.schema() + "." + table.name());
            tableMapper.dropTableBySchemaAndName(table.schema(), table.name());
        }
    }

    private void checkCreateSchema(TableMapper tableMapper, Table table) {
        if (tableMapper.findSchemaCountBySchemaName(table.schema()) < 1) {
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
    private void buildTableFieldsConstruct(Map<String, Integer> type2Length, Class<?> c, List<ColumnMate> allFields){
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields){
            // 判断方法中是否有指定注解类型的注解
            boolean hasAnnotation = field.isAnnotationPresent(Column.class);
            if (hasAnnotation) {
                // 根据注解类型返回方法的指定类型注解
                Column column = field.getAnnotation(Column.class);
                String name = StringUtils.isEmpty(column.name()) ? field.getName() : column.name();
                String type = StringUtils.isEmpty(column.type()) ? convertType(field.getType()) : column.type().toUpperCase();
                ColumnMate param = new ColumnMate(name,
                        type.toLowerCase(),
                        column.length(),
                        column.decimalLength(),
                        column.notNull(),
                        column.primaryKey(),
                        column.autoIncrement(),
                        column.defaultValue(),
                        type2Length.get(type.toUpperCase()));
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
    private void buildSubTableFieldsConstruct(Map<String, Integer> type2Length, Class<?> c, List<ColumnMate> parentAllFields, Map<String, List<ColumnMate>> subName2AllFields) throws ExpandException {
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields){
            // 判断方法中是否有指定注解类型的注解
            boolean hasAnnotation = field.isAnnotationPresent(SubTable.class);
            if (hasAnnotation) {
                // 根据注解类型返回方法的指定类型注解
                SubTable subTable = field.getAnnotation(SubTable.class);
                String name = StringUtils.isEmpty(subTable.name()) ? field.getName() : subTable.name();
                String[] associationKeys = subTable.joinColumns();
                Class<?> subClass = subTable.subClass();
                List<ColumnMate> subAllFields = new ArrayList<>();
                checkAssociationKey(associationKeys, parentAllFields, subAllFields);
                buildTableFieldsConstruct(type2Length, subClass, subAllFields);
                subName2AllFields.put(name, subAllFields);
            }
        }
    }

    private void checkAssociationKey(String[] associationKeys, List<ColumnMate> parentAllFields, List<ColumnMate> subAllFields) throws ExpandException {
        for (String associationKey : associationKeys) {
            boolean flag = false;
            for (ColumnMate columnMate : parentAllFields)
                if (columnMate.getName().equals(associationKey)) {
                    subAllFields.add(columnMate); // 找到关联键，加入到字段中
                    flag = true;
                }
            if (!flag)
                throw new ExpandException("not find the associated attribute:" + associationKey);
        }
    }

    private String convertType(Class type) {
        return TypeConvert.convertType(this.dbDriver, type);
    }

    private void buildNewFields(List<ColumnMate> allFields, List<ColumnMate> newFields, List<ColumnMate> tableColumns) throws ExpandException {
        for (ColumnMate columnMate : allFields) {
            checkColumnParam(tableColumns, columnMate);
            if (!tableColumns.contains(columnMate)) // 这里依赖ColumnParam中重写equals方法
                newFields.add(columnMate);
        }
    }

    private void checkColumnParam(List<ColumnMate> tableColumns, ColumnMate weColumn) throws ExpandException {
        for (ColumnMate param : tableColumns) {
            // 如果类型不一样，则不能继续
            if (param.getName().equals(weColumn.getName()) && !param.getType().equals(weColumn.getType())) {
                if (param.getType().startsWith("int") && weColumn.getType().startsWith("int"))
                    continue;
                throw new ExpandException(weColumn.getName() + " type in " + weColumn.getType() + ", the type in the database is " + param.getType());
            }
        }
    }

    private void createOrUpdateTable(List<TableMate> newTables, List<TableMate> updateTables) {
        createTables(newTables);
        updateTables(updateTables);
    }

    private void createTables(List<TableMate> tables) {
        if (tables.size() == 0)
            return;
        for (TableMate tableMate : tables) {
            LOGGER.debug("createTables: " + tableMate.getSchema() + "." + tableMate.getName());
            this.tableMapper.createTable(tableMate, this.dbDriver);
        }
    }

    private void updateTables(List<TableMate> tables) {
        if (tables.size() == 0)
            return;
        for (TableMate weTable : tables) {
            LOGGER.debug("updateTables: " + weTable.getSchema() + "." + weTable.getName());
            this.tableMapper.alterTable(weTable, this.dbDriver);
        }
    }


}
