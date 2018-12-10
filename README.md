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

1. 无侵入性，对使用纯MyBatis或MyBatis-Plus的项目进行功能增强，对原有代码无影响。 
2. 配置简单，只要在pom.xml中加一个依赖即可。
3. 功能全，补足了MyBatis开发效率低的短板，以下为新增的功能：
   跨数据库分页、DDl生成、源码生成、函数变换、主键生成、InlineSQL风格、ActiveRecord、Tail、实体越级关联查询、主从、分库分表等。  
4. 架构合理，jSqlBox模块作为MyBatis的插件存在，但它本身也是一个全功能DAO工具(只有约400K大小)，可以不依赖MyBatis单独使用。  

## 文档 | Documentation

[中文](https://gitee.com/drinkjava2/myfat/wikis/pages)  |  [English](https://github.com/drinkjava2/myfat/wiki) | [JavaDoc](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22myfat%22)

## 配置 | Configuration
在pom.xml中加入：  
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
