# Bugpush-JUnit-Core

#### 介绍
[BugCloud Junit Core](https://github.com/qingyunzhiwu/bugcloud-junit-core) 是 _[BugCloud](http://www.bug-cloud.com)_ 推出的Java单元测试功能包，目标是用最少的代码完成单元测试，减少开发人员在单元测试过程中的代码开发量，并可将测试结果发送到[BugCloud云平台](http://www.bug-cloud.com)，提高软件开发、Bug修复及项目管理交付的效率。

#### 连接
* 官网： http://www.bug-cloud.com
* library github: https://github.com/qingyunzhiwu/bugcloud-junit-core
* library gitee:https://gitee.com/qingyunzhiwu/bugcloud-junit-core
* demo github：https://github.com/qingyunzhiwu/bugcloud-junit-demo
* demo gitee: https://gitee.com/qingyunzhiwu/bugcloud-junit-demo

#### 软件架构
BugCloud Junit core 以Spring Boot 2、JUnit 4 为基础,添加测试报告自动推送、自动创建测试类功能，使开发人员可以通过添加简单注解快速进行单元测试。

![BugCloud JUnit Core架构图](https://bug-cloud.obs.myhuaweicloud.com/git/readme-framework.png)

#### 安装教程
##### Gradle
```javascript
testImplementation 'com.bug-cloud:bugcloud-junit-core:0.0.2-RELEASE'
```

##### Maven

```javascript
<dependency>
  <groupId>com.bug-cloud</groupId>
  <artifactId>bugcloud-junit-core</artifactId>
  <version>0.0.2-RELEASE</version>
  <type>module</type>
</dependency>
```

#### 使用说明

##### 测试前的准备

如果需要在单元测试完成后，将测试失败的用例结果提交到[BugCloud云平台](http://www.bug-cloud.com)，需登陆官网开通账号，相关步骤如下：

1. 访问 http://www.bug-cloud.com 注册账号。
2. 创建组织。
3. 创建测试应用。
4. 在应用设置页面，获取appKey、appSecret参数，用于在单元测试完成后提交报告。

##### API说明
###### AutoTestScan 类注解
**用能：**用于提供自动测试的相关参数。

**参数说明：**

| 参数名         | 类型  | 是否必填 | 功能描述      |
|---|---|---|---|
| packageName | 字符串 | 是 | 指定扫描的包名称。 |

###### PushReport 类注解
**用能：**在测试完成后，将失败的测试结果发送到[BugCloud云平台](http://www.bug-cloud.com)。

**参数说明：**

| 参数名         | 类型  | 是否必填  | 功能描述      |
|---|---|---|---|
| appKey | 字符串 | 是 | 应用配置中的Key。 |
| appSecret | 字符串 | 是 | 应用配置中的安全码。 
| pusher | 字符串 | 是 | 推送人的登陆名。 | 
| handler | 字符串 | 是 | 问题处理人的登陆名。 |
| isPush | 布尔型 | 否 | 是否推送问题报告到[BugCloud云平台](http://www.bug-cloud.com)，默认值为true推送。  |

###### RandomParameter 方法注解
**用能：**此注解需要与@RunWith(BugCloudAutoSpringSuite)一起配合使用，BugCloudAutoSpringSuite类用来扫描功能类后动态创建测试用例，RandomParameter用于计算某个测试方法的参数值。当用户没有实现RandomParameter时，测试方法的参数会根据数据类型随机赋值。

**参数说明：**

| 参数名         | 类型  | 是否必填  | 功能描述      |
|---|---|---|---|
| className | 正则表达式 | 否 | 指明哪个测试类名接收此方法的返回值，如果为空，代表所有测试类都接收此值。 |
| methodName | 正则表达式 | 否 | 指明哪个测试方法名接收此方法的返回值，如果为空，代表所有测试方法都接收此值。  | 
| parameterName | 正则表达式 | 是 | 指明测试方法中的哪个参数名接收此方法的返回值。 | 

在测试一些业务功能时，如删除指定Id的数据信息，这时需要传递指定的Id值做为参数，采用随机赋值测试的方法就行不通了，此时就可以通过RandomParameter注解，指定哪一个测试类、哪一个测试方法的哪个参数来返回这个固定Id值。
另外需要注意RandomParameter注解的方法必须有返回值。

###### BugCloudRunner 类
BugCloudRunner注解继承于BlockJUnit4ClassRunner类，主要功能用于在测试完成后，将测试结果上传到[BugCloud云平台](http://www.bug-cloud.com)。此注解需要配合PushReport注解一起使用，才能提交报告。

```javascript
@RunWith(BugCloudRunner.class)
@PushReport(appKey = "********-****-****-****-********", appSecret = "********-****-****-****-********", pusher = "推送用户登陆名",handler="处理用户登陆名")
public class BugCloudRunnerTest {
	@Test
	public void testFalse() {
		Assert.assertTrue(false);
	}
}
```

###### BugCloudSpringRunner 类
BugCloudSpringRunner继承于SpringJUnit4ClassRunner类，功能与BugCloudRunner类相同，主要用于在测试完成后，将测试结果上传到[BugCloud云平台](http://www.bug-cloud.com)。

```javascript
@RunWith(BugCloudSpringRunner.class)
@PushReport(appKey = "********-****-****-****-********", appSecret = "********-****-****-****-********", pusher = "推送用户登陆名",handler="处理用户登陆名")
@WebAppConfiguration
@SpringBootTest
@Transactional
public class BugCloudSpringRunnerTest {
	private MockMvc mock;
	@Autowired
	private WebApplicationContext webContext;
	
	@Before
	public void init() {
		mock = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext).build();
	}
	
	@Test
	public void testGetAllUsers() throws Exception {
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/users")
				.contentType(MediaType.APPLICATION_JSON);
		mock.perform(builder).andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn().getResponse().getContentAsString();
	}
}
```

###### BugCloudAutoSpringSuite 类
BugCloudAutoSpringSuite继承于Suite套件类，测试时会扫描Java包中的Controller层，动态创建Controller测试类，将其中带有GetMapping、PostMapping、PutMapping、DeleteMapping、RequestMappping注解的方法自动生成测试方法进行单元测试。

```javascript
@RunWith(BugCloudAutoSpringSuite.class)
@AutoTestScan(packageName = "com.bugcloud.junit.demo")
@PushReport(appKey = "********-****-****-****-********", appSecret = "********-****-****-****-********", pusher = "推送用户登陆名",handler="处理用户登陆名")
@Transactional
public class BugcloudJunitDemoApplicationTests {

	/**
	 * 计算接口方法中，参数名包含Id的字符型的返回值。
	 * 
	 * @return
	 */
	@RandomParameter(parameterName = ".*Id.*")
	public String userId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 计算接口方法中，参数名等于name的的返回值。
	 * 
	 * @return
	 */
	@RandomParameter(parameterName = "name")
	public String name() {
		String[] names = { "唐玄奘", "孙悟空", "猪八戒", "沙悟净" };
		return names[(int) (Math.random() * names.length)];
	}
}
```
#### BugCloud云平台
[BugCloud云平台](http://www.bug-cloud.com)用于项目的自动化测试与管理，当[BugCloud Junit Core](https://github.com/qingyunzhiwu/bugcloud-junit-core)单元测试报告提交后，可在应用中进行问题的修复。问题界面如下：

![问题列表](https://bug-cloud.obs.myhuaweicloud.com/git/questions.png)

#### 计划目标

2021年，我们将一起努力拥有更多的能力，为所有工作在一线的兄弟姐妹提供更便捷、更可靠的服务与平台，为中国软件事业的腾飞保驾护航。
* 实现提供不同行业、不同平台的测试用例模版能力，使开发团队可以快速、全面的构建测试场景。
* 提供更多基于功能、性能、安全方面的测试工具。
* 推出基于容器技术的在线自动化测试的能力。
* 成立线上专家部门，提供线上人工测试服务的能力。
* 打造用户认证等级体系，提供线上线下认证培训服务。





