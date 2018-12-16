<p align="center">
  <a href="https://github.com/drinkjava2/myfat">
   <img alt="myfat-logo" src="myfat-logo.png">
  </a>
</p>

<p align="center">
  MyFat让MyBatis更胖
</p>

<p align="center">
  <a href="http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.drinkjava2%22%20AND%20a%3A%22myfat%22">
    <img alt="maven" src="https://img.shields.io/maven-central/v/com.github.drinkjava2/myfat.svg?style=flat-square">
  </a>

  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

## 简介 | Intro
MyFat是MyBatis的DAO功能增强插件，利用全功能持久层工具jSqlBox来补足MyBatis的短板。

## 优点 | Advantages

1. 无侵入性，对于使用MyBatis的项目唯一要做的只是添加MyFat依赖即可，不用更改任何其它配置文件和代码。  
2. 功能全，补足了MyBatis不提供CRUD的短板，以下为增强后的功能：  
   跨数据库分页、DDl生成、实体源码生成、函数变换、主键生成、多种SQL写法、DataMapper、ActiveRecord、Tail、实体越级关联查询、主从、分库分表等。  
3. 架构更合理，它不是基于MyBatis内核开发，而是整合了jSqlBox的功能，项目本身是模块式开发，可维护性好。  

## 与其它Mybatis插件的区别 
1. 源码少，MyFat本身不提供DAO功能，源码只有7个类，起到粘合剂的作用，jar包大小25k。
2. ActiveRecord可以只声明接口（Java8），不一定需要继承类，也不需要定义Mapper，更无侵入性。
3. 它没有专门的分页方法，但是所有SQL查询都可以分页，无侵入性。 
4. 支持的SQL写法更多，如参数内嵌式SQL写法、实体越级关联查询等。


## 文档 | Documentation

[中文](https://gitee.com/drinkjava2/myfat/wikis/pages)  |  [English](https://github.com/drinkjava2/myfat/wiki) | [JavaDoc](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22myfat%22)

## 配置 | Configuration
在pom.xml中加入以下内容即可，注意MyFat必须先于MyBatis加载: 
```xml
<dependency>
   <groupId>com.github.drinkjava2</groupId>
   <artifactId>myfat</artifactId>
   <version>1.0</version> <!--或最新版-->
</dependency> 

<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.6</version> <!--或最新版--> 
</dependency> 

```

如果在Spring环境下,则上面的第二项要改成Spring的包，如：
```
  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
	  <version>1.3.2</version> <!--或最新版--> 
  </dependency>
```
 
## 入门 | First Example
以下示例演示了myfat的基本配置和使用:
```
public class HelloWorld implements ActiveEntity<HelloWorld> {
    private String name;
    public String getName() {return name; }
    public void setName(String name) {this.name = name; }

    public static void main(String[] args) {
        DataSource ds = JdbcConnectionPool
                .create("jdbc:h2:mem:DBName;MODE=MYSQL;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=0", "sa", "");
        SqlBoxContext ctx = new SqlBoxContext(ds);
        SqlBoxContext.setGlobalSqlBoxContext(ctx);
        String[] ddls = ctx.toCreateDDL(HelloWorld.class);
        for (String ddl : ddls)
               ctx.nExecute(ddl);

        new HelloWorld().putField("name", "Hello myfat").insert();
        System.out.println(ctx.iQueryForString("select name from HelloWorld"));
    }
}
```

## 范例 | Demo

* [myfat-springboot](../../tree/master/demo/myfat-springboot) 演示myfat在SpringBoot环境下的配置和使用。  
* [myfat-springboot-mp](../../tree/master/demo/myfat-springboot-mp) 演示在SpringBoot环境下myfat对MyBatis-plus的功能增强。
* [myfat-java8-demo](../../tree/master/demo/myfat-java8-demo) 主要演示myfat的两个特点：实体类也可以只声明接口、利用Lambda来写SQL。

 
## 作者其它开源项目 | Other Projects
- [Java持久层工具 jSqlBox](https://gitee.com/drinkjava2/jSqlBox)
- [数据库方言工具 jDialects](https://gitee.com/drinkjava2/jdialects)
- [一个独立的声明式事务工具 jTransactions](https://gitee.com/drinkjava2/jTransactions)
- [一个微型IOC/AOP工具 jBeanBox](https://gitee.com/drinkjava2/jBeanBox)
- [一个服务端布局工具 jWebBox](https://gitee.com/drinkjava2/jWebBox)
- [人工生命实验项目 frog](https://gitee.com/drinkjava2/frog)

## 期望 | Futures

欢迎发issue提出更好的意见或提交PR，帮助完善myfat

## 版权 | License

[Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## 关注我 | About Me
[Github](https://github.com/drinkjava2)  
[码云](https://gitee.com/drinkjava2)  
