package com.bugcloud.junit.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.runners.Suite;
import org.junit.runners.model.RunnerBuilder;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.bind.annotation.RestController;

import com.bugcloud.junit.core.annotation.AutoTestScan;
import com.bugcloud.junit.core.clazz.ParameterService;
import com.bugcloud.junit.core.clazz.SpringControllerTestClassFactory;
import com.google.common.base.Preconditions;

/**
 * 用于自动创建基于测试Spring项目的测试用例Runner
 * @author yuzhantao
 *
 */
public class BugCloudAutoSpringSuite extends Suite {
//	private static final Log log = LogFactory.getLog(BugCloudAutoRunner.class);
	private static Class<?>[] testClasses = null;
	private static SpringControllerTestClassFactory controllerScriptFactory = new SpringControllerTestClassFactory(); // Controller类脚本工厂

	public BugCloudAutoSpringSuite(Class<?> klass, RunnerBuilder builder) throws Exception {
		super(builder, klass, createTestControllerProxyClasses(klass));
	}

	/**
	 * 根据单元测试类创建Controller测试代理类
	 * 
	 * @param testCaseClass 单元测试类
	 * @return 返回代理类数组
	 * @throws Exception
	 */
	private static Class<?>[] createTestControllerProxyClasses(Class<?> testCaseClass) throws Exception {
		if (BugCloudAutoSpringSuite.testClasses == null) {
			String scanPackage = null;
			AutoTestScan atc = testCaseClass.getAnnotation(AutoTestScan.class);
			if (atc == null || atc.packageName() == null) {
				scanPackage = "";
			} else {
				scanPackage = atc.packageName();
			}
			Set<Class<?>> controllerClasses = scanControllerClass(scanPackage);
			ParameterService.getInstance().addParameterCreateClass(testCaseClass,
					controllerClasses.stream().map(item -> item.getName() + "Test").toArray(String[]::new));
			List<Class<?>> proxyClasses = new ArrayList<>();
			for (Class<?> clazz : controllerClasses) {
				Class<?> ctlClass = createProxyClass(testCaseClass, clazz);
				if (ctlClass != null) {
					proxyClasses.add(ctlClass);
				}
			}
			BugCloudAutoSpringSuite.testClasses = proxyClasses.toArray(new Class<?>[] {});
		}
		return BugCloudAutoSpringSuite.testClasses;
	}

	/**
	 * 根据需要测试的Controller类创建代理测试类
	 * 
	 * @param testCaseClass
	 * @param destTestClass 需要测试的Controller类
	 * @return 测试类
	 * @throws Exception
	 */
	private static Class<?> createProxyClass(Class<?> testCaseClass, Class<?> destTestClass) throws Exception {
		Class<?> clazz = controllerScriptFactory.createTestClass(testCaseClass, destTestClass);
		return clazz;
	}

	/**
	 * 扫描指定包下的所有Controller类
	 * 
	 * @param scanPackage
	 * @return
	 */
	private static Set<Class<?>> scanControllerClass(String scanPackage) {
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ org.springframework.util.ClassUtils
							.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(scanPackage));
			Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
			Preconditions.checkArgument(resources.length > 0, packageSearchPath + "下未扫描到需要测试的文件!");
			Resource resource = resources[0];
			// 设置扫描路径
			Reflections reflections;
			reflections = new Reflections(new ConfigurationBuilder().setUrls(resource.getURL())
					.setScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false)));
			// 扫描包内带有@RequiresPermissions注解的所有方法集合
			Set<Class<?>> clazzs = reflections.getTypesAnnotatedWith(Controller.class);
			clazzs.addAll(reflections.getTypesAnnotatedWith(RestController.class));
			return clazzs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
