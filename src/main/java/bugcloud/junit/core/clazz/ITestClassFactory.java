package bugcloud.junit.core.clazz;

import javassist.CtClass;

public interface ITestClassFactory {
	CtClass createTestClass(Class<?> clazz) throws Exception;
}
