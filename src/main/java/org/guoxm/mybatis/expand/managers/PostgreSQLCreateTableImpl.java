package org.guoxm.mybatis.expand.managers;

import org.guoxm.mybatis.expand.mappers.TableMapper;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

/**
 * PostgreSQL创建表的实现
 * Created on 2019/5/20.
 * @author guoxm
 */
//@Component
public class PostgreSQLCreateTableImpl implements WeCreateTableService {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreSQLCreateTableImpl.class);

    private final TableMapper tableMapper;

    private final MybatisProperties mybatisProperties;

    @Value(value = "${mybatis-expand.scan-packages}")
    private String packages;

    @Value(value = "${mybatis-expand.auto}")
    private String auto;

    @Autowired
    public PostgreSQLCreateTableImpl(TableMapper tableMapper, MybatisProperties mybatisProperties) {
        this.tableMapper = tableMapper;
        this.mybatisProperties = mybatisProperties;
    }

    @PostConstruct
    public void init() {
        System.out.println("mybatis.expand.scan-packages: " + packages);
        System.out.println("mybatis.expand.auto: " + auto);
        System.out.println(this.tableMapper);
//        for (String str : mybatisProperties.getMapperLocations())
//            System.out.println(str);
        System.out.println(mybatisProperties.getTypeAliasesPackage());
//        createTable();
    }

    @Override public void createTable(String _package) {

    }

    //    @Override
//    public void createTable() {
//        Map<String, Integer> type2Length = getType2Length();
//        Set<Class<?>> classes = ClassTool.getClasses(packages, true);
//
//        List<WeTable> newTables = new ArrayList<>();
//
//        List<WeTable> updateTables = new ArrayList<>();
//
//        buildTablesConstruct(classes, type2Length, newTables, updateTables);
//
//        createOrUpdateTable(newTables, updateTables);
//    }
//
//    private Map<String, Integer> getType2Length() {
//        Field[] fields = PostgreSQLType.class.getDeclaredFields();
//        Map<String, Integer> result = new TreeMap<>();
//        for (Field field : fields) {
//            LengthCount lengthCount = field.getAnnotation(LengthCount.class);
//            result.put(field.getName().replace("_", " ").toLowerCase(), lengthCount.LengthCount());
//        }
//        return result;
//    }
//
//    private void buildTablesConstruct(Set<Class<?>> classes, Map<String, Integer> type2Length, List<WeTable> newTables, List<WeTable> updateTables) {
//        for (Class<?> c : classes){
//            Table table = c.getAnnotation(Table.class);
//            if (table == null)
//                continue;
//
//            List<WeColumn> allFields = new ArrayList<>(); // 该类所有的字段
//            List<WeColumn> newFields = new ArrayList<>(); // 该类新增的字段
//
//            buildTableFieldsConstruct(type2Length, c, allFields); // 迭代出所有model的所有fields存到newFieldList中
//
//            if (this.tableMapper.findSchemaCountBySchemaName(table.schema()) < 1)
//                this.tableMapper.createSchema(table.schema()); // 如果没有schema，那么就需要创建
//
//            if (Constants.CREATE.equals(auto.toUpperCase())) // 如果是create，那么需要drop一下
//                this.tableMapper.dropTableBySchemaAndName(table.schema(), table.name());
//
//            if (this.tableMapper.findTableCountBySchemaAndName(table.schema(), table.name()) < 1) { //  这里要先判断一下表是否存在
//                newTables.add(new WeTable(table.schema(), table.name(), allFields));
//            } else {
//                List<WeColumn> tableColumns = this.tableMapper.findTableColumnBySchemaAndName(table.schema(), table.name());
//                buildNewFields(allFields, newFields, tableColumns);
//                if (newFields.size() > 0)
//                    updateTables.add(new WeTable(table.schema(), table.name(), newFields));
//            }
//        }
//    }
//
//    /**
//     * 把类中的所有Column注解的属性读取出来，构建成对象
//     * @param type2Length 类型列表
//     * @param c 需要解析的类
//     * @param allFields 类中所有注解的属性
//     */
//    private void buildTableFieldsConstruct(Map<String, Integer> type2Length, Class<?> c, List<WeColumn> allFields){
//        Field[] fields = c.getDeclaredFields();
//        for (Field field : fields){
//            // 判断方法中是否有指定注解类型的注解
//            boolean hasAnnotation = field.isAnnotationPresent(Column.class);
//            if (hasAnnotation) {
//                // 根据注解类型返回方法的指定类型注解
//                Column column = field.getAnnotation(Column.class);
//                String name = column.name().equals(Constants.NULL) ? field.getName() : column.name();
//                String type = column.type().equals(Constants.NULL) ? convertType(field.getType()) : column.type();
//                WeColumn param = new WeColumn(name,
//                        type.toLowerCase(),
//                        column.length(),
//                        column.decimalLength(),
//                        column.isNull(),
//                        column.isKey(),
//                        column.isAutoIncrement(),
//                        column.defaultValue(),
//                        type2Length.get(type.toLowerCase()));
//                allFields.add(param);
//            }
//        }
//    }
//
//    private String convertType(Class type) {
//        return PostgreSQLType.convertType(type);
//    }
//
//    private void buildNewFields(List<WeColumn> allFields, List<WeColumn> newFields, List<WeColumn> tableColumns) throws TypeInconsisExecption {
//        for (WeColumn weColumn : allFields) {
//            checkColumnParam(tableColumns, weColumn);
//            if (!tableColumns.contains(weColumn)) // 这里依赖ColumnParam中重写equals方法
//                newFields.add(weColumn);
//        }
//    }
//
//    private void checkColumnParam(List<WeColumn> tableColumns, WeColumn weColumn) throws TypeInconsisExecption {
//        for (WeColumn param : tableColumns) {
//            // 如果类型不一样，则不能继续
//            if (param.getName().equals(weColumn.getName()) && !param.getType().equals(weColumn.getType()))
//                throw new TypeInconsisExecption(weColumn.getName() + " type in " + weColumn.getType() + ", the type in the database is " + param.getType());
//        }
//    }
//
//    private void createOrUpdateTable(List<WeTable> newTables, List<WeTable> updateTables) {
//        createTables(newTables);
//        updateTables(updateTables);
//    }
//
//    private void createTables(List<WeTable> tables) {
//        if (tables.size() == 0)
//            return;
//        for (WeTable weTable : tables)
//            this.tableMapper.createTable(weTable);
//    }
//
//    private void updateTables(List<WeTable> tables) {
//        if (tables.size() == 0)
//            return;
//        for (WeTable weTable : tables)
//            this.tableMapper.alterTable(weTable);
//    }


}
