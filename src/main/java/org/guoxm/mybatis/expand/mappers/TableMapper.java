package org.guoxm.mybatis.expand.mappers;

import org.apache.ibatis.annotations.*;
import org.guoxm.mybatis.expand.structures.WeColumn;
import org.guoxm.mybatis.expand.structures.WeTable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 创建表映射文件
 * Created on 2019/5/20.
 * @author guoxm
 */
@Repository
public interface TableMapper {

    // 查询schema是否存在
    @Select("SELECT count(*) FROM information_schema.schemata WHERE schema_name = #{schemaName}")
    int findSchemaCountBySchemaName(@Param("schemaName")String schemaName);

    // 查询schema下是否存在table
    @Select("SELECT count(*) FROM information_schema.tables WHERE table_schema = #{schemaName} AND table_name = #{tableName}")
    int findTableCountBySchemaAndName(@Param("schemaName")String schemaName, @Param("tableName")String tableName);

    // 删除schema下的某张表
    @Delete("DROP TABLE IF EXISTS ${schemaName}.${tableName}")
    void dropTableBySchemaAndName(@Param("schemaName")String schemaName, @Param("tableName")String tableName);

    // 根据结构中的注解的信息来创建schema
    @Insert("CREATE SCHEMA ${schemaName}")
    void createSchema(@Param("schemaName")String schemaName);

    // 根据结构中的注解的信息来创建表
    @InsertProvider(type = PostgreSQLProvider.class, method = "createTable")
    void createTable(WeTable weTable);

    // 根据新增的字段修改表
    @UpdateProvider(type = PostgreSQLProvider.class, method = "alterTable")
    void alterTable(WeTable weTable);

    @Select("SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = #{schemaName} AND table_name = #{tableName}")
    @ResultType(WeColumn.class)
    @Results({@Result(column = "column_name", property = "name"), @Result(column = "data_type", property = "type")})
    List<WeColumn> findTableColumnBySchemaAndName(@Param("schemaName")String schemaName, @Param("tableName")String tableName);

}

