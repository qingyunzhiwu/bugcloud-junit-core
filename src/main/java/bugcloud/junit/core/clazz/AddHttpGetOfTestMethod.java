package bugcloud.junit.core.clazz;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import bugcloud.junit.core.util.HttpUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class AddHttpGetOfTestMethod extends AbstractAddMethodOfTestClass {
	private Method srcMethod;

	public AddHttpGetOfTestMethod(ITestClassFactory factory, String name, Method srcMethod, Class<?>[] paramesClasses,
			Map<Class<? extends Annotation>, Map<String, Object>> annoations) {
		super(factory, name, paramesClasses, annoations);
		this.srcMethod = srcMethod;
	}

	@Override
	public String body(CtClass ctClass) throws Exception {
		String url = HttpUtils.getUrl(this.srcMethod);
		if (url == null)
			return null;

		String methodName = "test" + this.srcMethod.getName().substring(0, 1).toUpperCase()
				+ this.srcMethod.getName().substring(1);
		// 创建测试方法
		CtMethod ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[] {}, ctClass);
		ctMethod.setModifiers(Modifier.PUBLIC);

		// 创建方法体
		StringBuffer body = new StringBuffer();
		body.append("\n{");
		body.append("\n log.info(\"====================================================================\");");
		body.append("\n log.info(\"测试方法:" + methodName + "\");");
		body.append("\n java.util.Map params = new java.util.HashMap();");
		// 遍历参数
		ClassPool pool = ClassPool.getDefault();
		CtClass srcClass = pool.getCtClass(this.srcMethod.getDeclaringClass().getName()); // 获取原始类
		CtMethod method = srcClass.getDeclaredMethod(this.srcMethod.getName()); // 获取原始类方法
		MethodInfo methodInfo = method.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		if (codeAttribute != null) {
			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
					.getAttribute(LocalVariableAttribute.tag);
			int paramLen = method.getParameterTypes().length; // 参数数量
			Object[][] ans = method.getParameterAnnotations(); // 获取参数注解
			int pos = Modifier.isStatic(method.getModifiers()) ? 0 : 1; // 非静态的成员函数的第一个参数是this
			for (int i = 0; i < paramLen; i++) {
				String paramName = attr.variableName(i + pos); // 参数名称
				String paramType = method.getParameterTypes()[i].getName(); // 参数类型
				String paramValue = ParameterService.getInstance().createParameterValue(ctClass.getName(),
						methodName, paramName, paramType);
				String paramUse = ""; // 参数用途
				if (ans[i] != null && ans[i].length > 0) {
					for (Object pa : ans[i]) {
						if (pa instanceof PathVariable) { // 如果是路径参数
							url = this.pathParamHandler(url, paramName, paramValue);
							paramUse = "PathVariable";
							break;
						} else if (pa instanceof RequestParam) { // 如果是请求参数
							paramUse = "RequestParam";
							body.append("\n params.put(\"" + paramName + "\", \"" + paramValue + "\");");
						} else if (pa instanceof RequestBody) { // 如果是Body请求参数
							paramUse = "RequestBody";
//						parameterValues.put(par.getName(), HttpUtils.createPropertyValue(par));
						}
					}
				} else {
					paramUse = "RequestParam";
					body.append("\n params.put(\"" + paramName + "\", \"" + paramValue + "\");");
				}
				body.append("\n log.info(\"参数" + (i + 1) + ":{name:" + paramName + ",type:" + paramType + ",value:"
						+ paramValue + ",use:" + paramUse + "}\");");
			}
			body.append("\n log.info(\"URL:" + url + "\");");
			body.append("\n bugcloud.junit.core.util.HttpUtils.testGet(mock,\"" + url + "\",params);");
		}
		body.append("\n}");
		return body.toString();
	}

	private String pathParamHandler(String url, String name, String value) {
		return url.replace(" ", "").replace("{" + name + "}", value);
	}
}
