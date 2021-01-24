package bugcloud.junit.core.clazz;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public class ControllerScriptFactory {
//	private static final Log log = LogFactory.getLog(ControllerScriptFactory.class);

	private Map<Class<?>, IMethodScriptFactory> scriptFactory = new HashMap<>();

	public ControllerScriptFactory() {
		this.scriptFactory.put(GetMapping.class, new GetMappingMethodScriptFactory());
	}

	/**
	 * 创建测试类
	 * 
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public Class<?> createTestClass(Class<?> clazz) throws Exception {
		String className = clazz.getSimpleName() + "Test";
		String packageNme = clazz.getPackage().getName();
		String longClassName = packageNme + "." + className;
		ClassPool cp = ClassPool.getDefault();
		CtClass ctClass = cp.makeClass(longClassName);
		ClassFile classFile = ctClass.getClassFile();
		ConstPool constpool = classFile.getConstPool();

		// 添加类注解
		// 添加@RunWith
		AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		javassist.bytecode.annotation.Annotation annRunWith = new javassist.bytecode.annotation.Annotation(
				"org.junit.runner.RunWith", constpool);
		annRunWith.addMemberValue("value",
				new ClassMemberValue("bugcloud.junit.core.BugCloudSpringRunner", classFile.getConstPool()));
		attr.addAnnotation(annRunWith);
		// 添加@SpringBootTest
		javassist.bytecode.annotation.Annotation annSpringBootTest = new javassist.bytecode.annotation.Annotation(
				"org.springframework.boot.test.context.SpringBootTest", constpool);
		attr.addAnnotation(annSpringBootTest);
		classFile.addAttribute(attr);
		// 添加@WebAppConfigurationh
		javassist.bytecode.annotation.Annotation annWebAppConfigurationh = new javassist.bytecode.annotation.Annotation(
				"org.springframework.test.context.web.WebAppConfigurationh", constpool);
		attr.addAnnotation(annWebAppConfigurationh);
		classFile.addAttribute(attr);
		// 添加@BugCloudTest
		
		javassist.bytecode.annotation.Annotation annBugCloudTest = new javassist.bytecode.annotation.Annotation(
				"bugcloud.junit.core.annotation.BugCloudTest", constpool);
		annBugCloudTest.addMemberValue("appKey",
				new StringMemberValue("6585667c-6f18-4c4f-b809-3be1de3b3ca7", classFile.getConstPool()));
		annBugCloudTest.addMemberValue("appSecret",
				new StringMemberValue("fce8d3b5-6c9b-4a49-a50b-ad519630c898", classFile.getConstPool()));
		annBugCloudTest.addMemberValue("pusher",
				new StringMemberValue("yu", classFile.getConstPool()));
		annBugCloudTest.addMemberValue("handler",
				new StringMemberValue("张三", classFile.getConstPool()));
		attr.addAnnotation(annBugCloudTest);
		classFile.addAttribute(attr);
		// 添加@AutoConfigureMockMvc
		javassist.bytecode.annotation.Annotation annAutoConfigureMockMvc = new javassist.bytecode.annotation.Annotation(
				"org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc", constpool);
		attr.addAnnotation(annAutoConfigureMockMvc);
		classFile.addAttribute(attr);

		StringBuffer body = null;
		// 添加日志变量
		CtField ctField = new CtField(cp.get("org.apache.commons.logging.Log"), "log", ctClass);
		ctField.setModifiers(Modifier.PROTECTED);
		ctClass.addField(ctField);

		// 添加WebApplicationContext变量
		CtField ctWebContextField = new CtField(cp.get("org.springframework.web.context.WebApplicationContext"),
				"webContext", ctClass);
		ctWebContextField.setModifiers(Modifier.PROTECTED);
		AnnotationsAttribute webContextAttr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		javassist.bytecode.annotation.Annotation annAutowired = new javassist.bytecode.annotation.Annotation(
				"org.springframework.beans.factory.annotation.Autowired", constpool);
		webContextAttr.addAnnotation(annAutowired);
		ctWebContextField.getFieldInfo().addAttribute(webContextAttr);
		ctClass.addField(ctWebContextField);

		// 添加Mock变量测试接口
		CtField ctMockField = new CtField(cp.get("org.springframework.test.web.servlet.MockMvc"), "mock", ctClass);
		ctMockField.setModifiers(Modifier.PROTECTED);
		ctClass.addField(ctMockField);

		// 添加构造函数
		CtConstructor ctConstructor = new CtConstructor(new CtClass[] {}, ctClass);
		body = new StringBuffer();
		body.append("{ this.log=org.apache.commons.logging.LogFactory.getLog(\"" + longClassName + "\"); }");
		ctConstructor.setBody(body.toString());
		ctClass.addConstructor(ctConstructor);

		// 添加Before函数(初始化MockMvc)
		CtMethod beforeMethod = new CtMethod(CtClass.voidType, "beforeMethod", new CtClass[] {}, ctClass);
		beforeMethod.setBody(
				"mock = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(webContext).build();");
		AnnotationsAttribute attrBefore = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
		javassist.bytecode.annotation.Annotation annBefore = new javassist.bytecode.annotation.Annotation(
				"org.junit.Before", constpool);
		attrBefore.setAnnotation(annBefore);
		beforeMethod.getMethodInfo().addAttribute(attrBefore);
		ctClass.addMethod(beforeMethod);

		// 添加测试方法
		Set<Method> methods = scanControllerMethod(clazz);
		for (Method m : methods) {
			// 根据controller的不同类型注解选择不同的方法工厂创建测试方法
			IMethodScriptFactory factory = null;
			if (m.getAnnotation(GetMapping.class) != null) {
				factory = this.scriptFactory.get(GetMapping.class);
			} else if (m.getAnnotation(PostMapping.class) != null) {
				factory = this.scriptFactory.get(PostMapping.class);
			} else if (m.getAnnotation(PutMapping.class) != null) {
				factory = this.scriptFactory.get(PutMapping.class);
			} else if (m.getAnnotation(DeleteMapping.class) != null) {
				factory = this.scriptFactory.get(DeleteMapping.class);
			} else if (m.getAnnotation(PatchMapping.class) != null) {
				factory = this.scriptFactory.get(PatchMapping.class);
			} else if (m.getAnnotation(RequestMapping.class) != null) {
				RequestMapping rms = m.getAnnotation(RequestMapping.class);
				if (rms.method().length > 0) {
					for (RequestMethod rm : rms.method()) {
						switch (rm) {
						case GET:
							factory = this.scriptFactory.get(GetMapping.class);
							break;
						case HEAD:
							break;
						case POST:
							factory = this.scriptFactory.get(PostMapping.class);
							break;
						case PUT:
							factory = this.scriptFactory.get(PutMapping.class);
							break;
						case PATCH:
							break;
						case DELETE:
							factory = this.scriptFactory.get(DeleteMapping.class);
							break;
						case OPTIONS:
							break;
						case TRACE:
							break;
						}
					}
				} else {
					factory = this.scriptFactory.get(GetMapping.class);
				}
			}
			// 如果方法工厂不为空，就创建测试方法，并添加到测试类中
			if (factory != null) {
				CtMethod testMethod = factory.createScript(ctClass, m); // 创建测试方法
				if (testMethod != null) {
					// 添加@Test注解
					AnnotationsAttribute annotationsAttribute = new AnnotationsAttribute(constpool,
							AnnotationsAttribute.visibleTag);
					javassist.bytecode.annotation.Annotation annotation = new javassist.bytecode.annotation.Annotation(
							"org.junit.Test", constpool);
					annotationsAttribute.setAnnotation(annotation);
					testMethod.getMethodInfo().addAttribute(annotationsAttribute);

					ctClass.addMethod(testMethod); // 将创建的测试方法添加到测试类中
				}
			}
		}
		Class<?> c = ctClass.toClass();
		return c;
	}

	/**
	 * 扫描Controller类中的接口方法
	 * 
	 * @param testClass 需要测试的类
	 * @return 接口方法类集合
	 */
	private Set<Method> scanControllerMethod(Class<?> testClass) {
		Set<Method> ret = new HashSet<>();
		Method[] methods = testClass.getMethods();
		for (Method m : methods) {
			if (m.getAnnotation(GetMapping.class) != null || m.getAnnotation(PostMapping.class) != null
					|| m.getAnnotation(PutMapping.class) != null || m.getAnnotation(DeleteMapping.class) != null
					|| m.getAnnotation(RequestMapping.class) != null || m.getAnnotation(PatchMapping.class) != null) {
				ret.add(m);
			}
		}
		return ret;
	}
}
