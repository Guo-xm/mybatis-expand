# mybatis-expand

该项目为基于spring boot和mybatis而开发的自动建表工具，通过项目中定义的注解来实现项目启动是扫描实体类中的注解来构建表结构，
并通过mybatis执行DDL语句。并且可以通过Option来决定是否修改表或者创建表。目前该工具只支持PostgreSQL数据库，并且对于修改表
结构只限定于判断列名和类型。需要注意的是，该项目依赖spring boot中定义的application.yml中定义的spring:datasource:

注意：建表使用默认的sql格式，既表名、schema和列都采用小写（对于列名，可能实体中为驼峰，该项目会自动将其转成“_小写"）。

下面简单介绍主要注解的功能和使用方式：
1、@ExpandScan 注解：
该注解用于启动自动扫描实体类实现建表功能，需要指定要扫描的包路径（可以通过values或者basePackages来指定），也可以指定Option
（默认为CREATE）。使用方式如下：

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/ExpandScan.png)

2、@Table 注解：
该注解用于注解类，即将该类结构构造成表结构，需要也必须指定schema和name，其中name即为表名，注意：表名和schema都要求小写。使
用方式如下：

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/Table.png)

3、@Column 注解：
该注解可以与@Table注解一起使用，也可单独使用，在这里先不多说。@Column即标示属性为了列的注解，其中有设置该列在数据库中的类型、
是否为主键、是否允许空值、是否自增以及默认值等功能。也可以不设置，不设置默认根据java类型来转换成数据库中对应的类型（对于
Boolean，enum和自定义类型要求必须指定类型）。其使用方式如下：

设置主键自增：

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/Column.png)

默认：

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/Column2.png)

4、@SubTable 注解：
该注解用于构建关联表或子表，必须与@Table一起使用。即@Table为主表，@subTable为关联表或者子表（也叫从表）。该注解需要提供
@Table中需要关联的列joinColumns，以及subTable中的实体类subClass，以及表名name。使用方式如下：

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/SubTable.png)


示例：

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/sex.png)

@Column在此处单独使用

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/Skill.png)

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/Hero.png)

![image](https://github.com/Guo-xm/mybatis-expand/blob/master/images/WeExpandMain.png)




        

