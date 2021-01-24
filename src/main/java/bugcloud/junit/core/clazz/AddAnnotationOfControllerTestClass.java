package bugcloud.junit.core.clazz;

import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * 在Controller测试类上添加注解
 * 
 * @author yuzhantao
 *
 */
public class AddAnnotationOfControllerTestClass extends AbstractTestClassDecorator {

	public AddAnnotationOfControllerTestClass(ITestClassFactory factory) {
		super(factory);
	}

	@Override
	public CtClass createTestClass(Class<?> clazz) throws Exception {
		CtClass ctClass = super.createTestClass(clazz);
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
		annBugCloudTest.addMemberValue("pusher", new StringMemberValue("yu", classFile.getConstPool()));
		annBugCloudTest.addMemberValue("handler", new StringMemberValue("张三", classFile.getConstPool()));
		attr.addAnnotation(annBugCloudTest);
		classFile.addAttribute(attr);
		// 添加@AutoConfigureMockMvc
		javassist.bytecode.annotation.Annotation annAutoConfigureMockMvc = new javassist.bytecode.annotation.Annotation(
				"org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc", constpool);
		attr.addAnnotation(annAutoConfigureMockMvc);
		classFile.addAttribute(attr);
		return ctClass;
	}

}
