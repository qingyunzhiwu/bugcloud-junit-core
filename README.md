# bugpush-junit-core

#### 介绍
BugCloud JUint Core是Bug Cloud推出的Java 单元测试功能包，用于减少开发人员在单元测试过程中的代码量，并可将测试结果发送到Bug系统，提高开发、管理项目的效率。

#### 软件架构
BugCloud Junit core  以Spring Boot 2、JUnit 4 为基类,在此基础添加问题报告自动推送Bug系统、自动扫描Controller类并添加接口测试功能，使开发人员可以通过添加简单注解快速进行单元测试。

![image](https://github.com/qingyunzhiwu/bugcloud-junit-core/blob/master/src/main/resources/static/images/readme-framework.png)

#### 安装教程
##### Gradle
```javascript
testImplementation 'com.bug-cloud:bugcloud-junit-core'
```

##### Maven

```javascript
<dependency>
  <groupId>com.bug-cloud</groupId>
  <artifactId>bugcloud-junit-core</artifactId>
  <version>0.0.1-RELEASE</version>
  <type>module</type>
</dependency>
```

#### 使用说明

##### Runner
1.  BugCloudRunner
BugCloudRunner继承于BlockJUnit4ClassRunner类，用于检测单元测试类是否有PushReport注解。当PushReport注解存在时，测试失败的结果将提交到www.bug-cloud.com平台中。
2.  BugCloudSpringRunner
BugCloudRunner继承于SpringJUnit4ClassRunner类，用于Spring架构相关测试，并检测单元测试类是否有PushReport注解。当PushReport注解存在时，测试失败的结果将提交到www.bug-cloud.com平台中。
3.  BugCloudAutoSpringSuite
BugCloudAutoSpringSuite继承于Suite类，用于在Spring架构中自动扫描Controller接口，并自动生成测试类。

