package bugcloud.junit.core.vo;

import javassist.CtClass;
import javassist.CtMethod;

public class ControllerTestMethod extends TestMethod {
	public ControllerTestMethod(CtClass ctClass, CtMethod ctMethod) {
		super(ctClass, ctMethod);
	}

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
