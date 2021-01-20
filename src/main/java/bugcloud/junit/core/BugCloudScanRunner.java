package bugcloud.junit.core;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import bugcloud.junit.core.annotation.AutoTestScan;
import bugcloud.junit.core.util.JavaCodeCompileUtils;

public class BugCloudScanRunner extends Suite {
	public BugCloudScanRunner(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(builder, klass, createTestControllerProxyClasses(klass));
	}

	/**
	 * 创建代理类
	 * @param testCaseClass
	 * @param destTestClass
	 * @return
	 */
	private static Class<?> createProxyClass(Class<?> testCaseClass, Class<?> destTestClass) {
		String className = destTestClass.getSimpleName() + "Test";
		String packageNme = destTestClass.getPackage().getName();
		StringBuffer sb = new StringBuffer();
		sb.append("package " + packageNme + ";");
		sb.append("import org.junit.Test;");
//		sb.append("import org.springframework.boot.test.context.SpringBootTest;");
//		sb.append("import org.springframework.test.context.web.WebAppConfiguration;");
//		sb.append("import org.springframework.test.context.junit4.SpringRunner;");
//		sb.append("import org.junit.runner.RunWith;");
//		sb.append("@WebAppConfiguration \n");
//		sb.append("@SpringBootTest \n");
//		sb.append("@RunWith(SpringRunner.class) \n");
		sb.append("public class " + className + " {\n ");
		Set<Method> methods = scanControllerMethod(destTestClass);
		for (Method m : methods) {
			String methodName = "test" + m.getName();
			sb.append("@Test public void " + methodName + "(){\n");
			sb.append("System.out.println(\"===============" + methodName + "hello world!\");\n");
			sb.append("\n}");
		}

		sb.append("\n}");
		Class<?> clazz = JavaCodeCompileUtils.compile(packageNme + "." + className, sb.toString());
		return clazz;
	}

	private static Class<?>[] createTestControllerProxyClasses(Class<?> testCaseClass) {
		String scanPackage = null;
		AutoTestScan atc = testCaseClass.getAnnotation(AutoTestScan.class);
		if (atc == null || atc.packageName() == null) {
			scanPackage = "";
		} else {
			scanPackage = atc.packageName();
		}
		Set<Class<?>> controllerClasses = scanControllerClass(scanPackage);
		List<Class<?>> proxyClasses = new ArrayList<>();
		for (Class<?> clazz : controllerClasses) {
			Class<?> ctlClass = createProxyClass(testCaseClass, clazz);
			if (ctlClass != null) {
				proxyClasses.add(ctlClass);
			}
		}
		return proxyClasses.toArray(new Class<?>[] {});
	}

	private static Set<Class<?>> scanControllerClass(String scanPackage) {
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ org.springframework.util.ClassUtils
							.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPackage));
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			Resource resource = resources[0];
			// 设置扫描路径
			Reflections reflections;
			reflections = new Reflections(new ConfigurationBuilder().setUrls(resource.getURL())
					.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false)));
			// 扫描包内带有@RequiresPermissions注解的所有方法集合
			Set<Class<?>> clazzs = reflections.getTypesAnnotatedWith(RestController.class);
			return clazzs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param scanPackage 需要扫描的包路径
	 */
	private static Set<Method> scanControllerMethod(Class<?> testClass) {
		Set<Method> ret = new HashSet<>();
		// 设置扫描路径
		Method[] methods = testClass.getMethods();
		for (Method m : methods) {
			if (m.getAnnotation(GetMapping.class) != null) {
				ret.add(m);
			}
		}
		// 扫描包内带有@RequiresPermissions注解的所有方法集合
		return ret;
//        List<InterfaceEntity> list = new ArrayList<>();
//        Date now = new Date();

		// 循环获取方法
//        methods.forEach(method -> {
////        	HttpGetTest.test(method);
////            String methodType = "";
////
////            //获取类上的@RequestMapping注解的值，作为请求的基础路径
////            String authUrl = method.getDeclaringClass().getAnnotation(RequestMapping.class).value()[0];
////
////            //获取方法上的@PutMapping,@GetMapping,@PostMapping,@DeleteMapping注解的值，作为请求路径,并区分请求方式
////            if (method.getAnnotation(PutMapping.class) != null) {
////                methodType = "put";
////                if (method.getAnnotation(PutMapping.class).value().length > 0) {
////                    authUrl = method.getAnnotation(PutMapping.class).value()[0];
////                }
////            } else if (method.getAnnotation(GetMapping.class) != null) {
////                methodType = "get";
////                if (method.getAnnotation(GetMapping.class).value().length > 0) {
////                    authUrl = method.getAnnotation(GetMapping.class).value()[0];
////                }
////            } else if (method.getAnnotation(PostMapping.class) != null) {
////                methodType = "post";
////                if (method.getAnnotation(PostMapping.class).value().length > 0) {
////                    authUrl = method.getAnnotation(PostMapping.class).value()[0];
////                }
////            } else if (method.getAnnotation(DeleteMapping.class) != null) {
////                if (method.getAnnotation(DeleteMapping.class).value().length > 0) {
////                    authUrl = method.getAnnotation(DeleteMapping.class).value()[0];
////                }
////            }
//
//            //使用Auth对象来保存值
////            InterfaceEntity auth = new InterfaceEntity();
////            auth.setMethodType(methodType);
//////            auth.setAuthUniqueMark(method.getAnnotation(RestController.class).value()[0]);
////            auth.setUrl(authUrl);
//////            auth.setAuthName(method.getDeclaringClass().getAnnotation(Api.class).value() + "-" + method.getAnnotation(ApiOperation.class).value());
////            auth.setCreateTime(now);
////            list.add(auth);
//        });
		// TODO 输出到控制台,此处存数据库即可
//        System.out.println(JSON.toJSONString(list));
	}
}
