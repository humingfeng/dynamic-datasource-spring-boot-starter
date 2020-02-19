
<p align="center">
	<strong>一个基于springboot的快速集成多数据源的启动器</strong>
</p>

<p align="center">
    <a href="https://www.travis-ci.org/humingfeng/dynamic-datasource-spring-boot-starter" target="_blank">
        <img src="https://www.travis-ci.org/humingfeng/dynamic-datasource-spring-boot-starter.svg?branch=master" >
    <a href="http://mvnrepository.com/artifact/com.humingfeng/dynamic-datasource-spring-boot-starter" target="_blank">
        <img src="https://img.shields.io/maven-central/v/com.humingfeng/dynamic-datasource-spring-boot-starter.svg" >
    </a>
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="http://img.shields.io/:license-apache-brightgreen.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/springBoot-1.4+_1.5+_2.0+-green.svg" >
    </a>
</p>

# 简介

dynamic-datasource-spring-boot-starter 是一个基于com.baomidou根据需求深度二次开发的springboot的快速集成多数据源的启动器。

其支持 **Jdk 1.8+,    SpringBoot 1.4.x  1.5.x   2.0.x**。


# 特性

1. 数据源分组，适用于多种场景 纯粹多库  读写分离  一主多从  混合模式。
2. 内置敏感参数加密和启动初始化表结构schema数据库database。
3. 提供对Druid，Mybatis-Plus，P6sy，Jndi的快速集成。
4. 简化Druid和HikariCp配置，提供全局参数配置。
5. 提供自定义数据源来源接口(默认使用yml或properties配置)。
6. 提供项目启动后增减数据源方案。
7. 提供Mybatis环境下的  **纯读写分离** 方案。
8. 使用spel动态参数解析数据源，如从session，header或参数中获取数据源。（多租户架构神器）
9. 提供多层数据源嵌套切换。（ServiceA >>>  ServiceB >>> ServiceC，每个Service都是不同的数据源）
10. 提供 **不使用注解**  而   **使用 正则 或 spel**    来切换数据源方案（实验性功能）。


# 使用方法

1. 引入dynamic-datasource-spring-boot-starter。

```xml
<dependency>
  <groupId>cn.humingfeng</groupId>
  <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
  <version>1.2.0</version>
</dependency>
```
2. 配置数据源。

```yaml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #设置严格模式,默认false不启动. 启动后在未匹配到指定数据源时候回抛出异常,不启动会使用默认数据源.
      datasource:
        master:
          url: jdbc:mysql://xx.xx.xx.xx:3306/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_1:
          url: jdbc:mysql://xx.xx.xx.xx:3307/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_2:
          url: ENC(xxxxx) # 内置加密,使用请查看详细文档
          username: ENC(xxxxx)
          password: ENC(xxxxx)
          driver-class-name: com.mysql.jdbc.Driver
          schema: db/schema.sql # 配置则生效,自动初始化表结构
          data: db/data.sql # 配置则生效,自动初始化数据
          continue-on-error: true # 默认true,初始化失败是否继续
          separator: ";" # sql默认分号分隔符
          
       #......省略
       #以上会配置一个默认库master，一个组slave下有两个子库slave_1,slave_2
```

```yaml
# 多主多从                      纯粹多库（记得设置primary）                   混合配置
spring:                               spring:                               spring:
  datasource:                           datasource:                           datasource:
    dynamic:                              dynamic:                              dynamic:
      datasource:                           datasource:                           datasource:
        master_1:                             mysql:                                master:
        master_2:                             oracle:                               slave_1:
        slave_1:                              sqlserver:                            slave_2:
        slave_2:                              postgresql:                           oracle_1:
        slave_3:                              h2:                                   oracle_2:
```

3. 使用  **@DS**  切换数据源。

**@DS** 可以注解在方法上和类上，**同时存在方法注解优先于类上注解**。

强烈建议只注解在service实现上。

|     注解      |                   结果                   |
| :-----------: | :--------------------------------------: |
|    没有@DS    |                默认数据源                |
| @DS("dsName") | dsName可以为组名也可以为具体某个库的名称 |

```java
@Service
@DS("slave")
public class UserServiceImpl implements UserService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List<Map<String, Object>> selectAll() {
    return  jdbcTemplate.queryForList("select * from user");
  }
  
  @Override
  @DS("slave_1")
  public List<Map<String, Object>> selectByCondition() {
    return  jdbcTemplate.queryForList("select * from user where age >10");
  }
}
```

> PS:最新的1.2.0版本支持：
现在启动时会在从配置文件中初始化数据源后再从默认数据库表中初始化其他数据源，如果数据源表不存在会自动建表，如果存在则从配置表中读取其他数据源配置信息，并进行初始化。
*MysqlDynamicDataSourceProvider为新增类
*data_source_config 为数据源配置表，不需要手动建，表不存在会自动创建
*表中字段为
USER_NAME varchar(255) '用户名',
PASSWORD varchar(255) '密码',
URL varchar(255) '地址',
DRIVER_CLASS_NAME '驱动',
DB_NAME '数据源名称',
TYPE '数据源类型',
==> TYPE内容支持内容：1、com.alibaba.druid.pool.DruidDataSource
2、com.alibaba.druid.pool.DruidDataSource
3、空，则自动创建BasicDataSource
默认库如果非MySQL或者Oracle，需要改动建表语句自己打包
欢迎STAR，并关注
---
---

关注Github：[1/2极客](https://github.com/humingfeng)

关注博客：[御前提笔小书童](https://blog.csdn.net/qq_22260641)

关注网站：[HuMingfeng](https://royalscholar.cn)

关注公众号：开发者的花花世界

![](https://img-blog.csdnimg.cn/20190106225239166.jpg)
---
