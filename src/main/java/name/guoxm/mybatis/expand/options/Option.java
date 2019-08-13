package name.guoxm.mybatis.expand.options;

/**
 * 操作枚举
 * CREATE 即创建，若没有则创建，若有，先删除再创建
 * UPDATE 即修改，和CREATE的区别就在若存在，就只修改
 * NONE 即不作为
 * Created on 2019/7/8.
 * @author guoxm
 */
public enum Option {
    CREATE,
    UPDATE,
    NONE,
}
