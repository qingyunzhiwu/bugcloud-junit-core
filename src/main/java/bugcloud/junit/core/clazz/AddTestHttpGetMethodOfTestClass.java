package bugcloud.junit.core.clazz;

import java.lang.annotation.Annotation;
import java.util.Map;

public class AddTestHttpGetMethodOfTestClass extends AbstractAddMethodOfTestClass{

	public AddTestHttpGetMethodOfTestClass(ITestClassFactory factory, String name, Class<?>[] paramesClasses,
			Map<Class<? extends Annotation>, Map<String, Object>> annoations) {
		super(factory, name, paramesClasses, annoations);
	}

	@Override
	public String body() {
		// TODO Auto-generated method stub
		return "System.out.println(\"======= 哈哈哈哈==================\");";
	}

}
