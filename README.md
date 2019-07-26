# mybatis-expand

    该项目为基于spring boot和mybatis而开发的自动建表工具，通过项目中定义的注解来实现项目启动是扫描实体类中的注解来构建表结构，并通过mybatis执行DDL语句。
并且可以通过Option来决定是否修改表或者创建表。目前该工具只支持PostgreSQL数据库，并且对于修改表结构只限定于判断列名和类型。

    注意：该项目建表使用默认的sql格式，既：所有表名、schema和列都采用小写（对于列名，可能实体中为驼峰，该项目会自动将其转成“_小写"）。
    下面简单介绍主要注解的功能和使用方式：
    1、@ExpandScan 注解：
        ![image]https://github.com/Guo-xm/mybatis-expand/blob/master/images/ExpandScan.png
        该注解用于启动自动扫描实体类实现建表功能，需要指定要扫描的包路径（可以通过values或者basePackages来指定），也可以指定Option（默认为CREATE）。
        
    2、@Table 注解：
        该注解用于启动自动扫描实体类实现建表功能，需要也必须指定schema和name，其中name即为表名，注意：表名和schema都要求小写。
        

