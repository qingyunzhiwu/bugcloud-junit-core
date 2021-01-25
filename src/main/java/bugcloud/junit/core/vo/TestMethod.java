package bugcloud.junit.core.vo;

import java.util.List;

import javassist.CtClass;
import javassist.CtMethod;

public class TestMethod {

	private List<TestParameter> parameters;

	private CtClass ctClass = null;
	private CtMethod ctMethod = null;

	public TestMethod(CtClass ctClass, CtMethod ctMethod) {
		this.ctClass = ctClass;
		this.ctMethod = ctMethod;
	}

	public String getName() {
		return this.ctMethod.getName();
	}

	public String getLongName() {
		return this.ctClass.getName()+"."+this.ctMethod.getName();
	}

	public List<TestParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<TestParameter> parameters) {
		this.parameters = parameters;
	}

}
