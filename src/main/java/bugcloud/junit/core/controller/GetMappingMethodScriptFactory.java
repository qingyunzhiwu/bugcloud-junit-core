package bugcloud.junit.core.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import bugcloud.junit.core.util.HttpUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class GetMappingMethodScriptFactory implements IMethodScriptFactory {
//	private static final Log log = LogFactory.getLog(GetMappingMethodScriptFactory.class);

	@Override
	public CtMethod createScript(CtClass clazz, Method method) throws Exception {
		String url = getUrl(method);
		if (url == null)
			return null;

		String methodName = "test" + method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
		// 创建测试方法
		CtMethod ctMethod = new CtMethod(CtClass.voidType, methodName, new CtClass[] {}, clazz);
		ctMethod.setModifiers(Modifier.PUBLIC);

		// 创建方法体
		StringBuffer body = new StringBuffer();
		body.append("\n{");
		body.append("\n log.info(\"====================================================================\");");
		body.append("\n log.info(\"测试方法:" + methodName + "\");");
		body.append("\n java.util.Map params = new java.util.HashMap();");
//		body.append("\n Object dd = new HashSet<String>();");
		// 遍历参数
		ClassPool pool = ClassPool.getDefault();
		CtClass srcClass = pool.getCtClass(method.getDeclaringClass().getName()); // 获取原始类
		CtMethod srcMethod = srcClass.getDeclaredMethod(method.getName()); // 获取原始类方法
		MethodInfo methodInfo = srcMethod.getMethodInfo();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		if (codeAttribute != null) {
			LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
					.getAttribute(LocalVariableAttribute.tag);
			int paramLen = srcMethod.getParameterTypes().length; // 参数数量
			Object[][] ans = srcMethod.getParameterAnnotations(); // 获取参数注解
			int pos = Modifier.isStatic(srcMethod.getModifiers()) ? 0 : 1; // 非静态的成员函数的第一个参数是this
			for (int i = 0; i < paramLen; i++) {
				String paramName = attr.variableName(i + pos); // 参数名称
				String paramType = srcMethod.getParameterTypes()[i].getName(); // 参数类型
				String paramValue = HttpUtils.createPropertyValue(paramType);
				String paramUse = "";	// 参数用途
				if (ans[i] != null && ans[i].length > 0) {
					for (Object pa : ans[i]) {
						if (pa instanceof PathVariable) { // 如果是路径参数
							url = this.pathParamHandler(url, paramName, paramValue);
							paramUse="PathVariable";
							break;
						} else if (pa instanceof RequestParam) { // 如果是请求参数
							paramUse="RequestParam";
							body.append("\n params.put(\"" + paramName + "\", \"" + paramValue + "\");");
						} else if (pa instanceof RequestBody) { // 如果是Body请求参数
							paramUse="RequestBody";
//						parameterValues.put(par.getName(), HttpUtils.createPropertyValue(par));
						}
					}
				} else {
					paramUse="RequestParam";
					body.append("\n params.put(\"" + paramName + "\", \"" + paramValue + "\");");
				}
				body.append("\n log.info(\"参数" + (i + 1) + ":{name:" + paramName + ",type:" + paramType + ",value:"
						+ paramValue + ",use:"+paramUse+"}\");");
			}
			body.append("\n log.info(\"URL:" + url + "\");");
			body.append("\n bugcloud.junit.core.util.HttpUtils.testGet(mock,\""+url+"\");");
		}
		body.append("\n}");
//		log.info("创建方法:" + body.toString());
		ctMethod.setBody(body.toString());
		body.setLength(0);
		return ctMethod;
	}
	
	private String pathParamHandler(String url, String name, String value) {
		return url.replace(" ", "").replace("{" + name + "}", value);
	}

	private String getUrl(Method method) {
		List<String> urls = new ArrayList<>();
		String headUrl = null;
		String[] getMappingValues = null;
		Annotation classAnnotation = method.getDeclaringClass().getAnnotation(RequestMapping.class);
		if (classAnnotation != null) {
			headUrl = ((RequestMapping) classAnnotation).value()[0];
		} else {
			return null;
		}
		Annotation annotation = method.getAnnotation(GetMapping.class);
		if (annotation != null) {
			getMappingValues = ((GetMapping) annotation).value();
		} else {
			if (annotation == null) {
				annotation = method.getAnnotation(RequestMapping.class);
				if (annotation != null) {
					RequestMapping rm = (RequestMapping) annotation;
					boolean isHavsGetMapping = false;
					for (RequestMethod r : rm.method()) {
						if (r == RequestMethod.GET) {
							isHavsGetMapping = true;
						}
					}
					if (isHavsGetMapping) {
						getMappingValues = ((RequestMapping) annotation).value();
					}
				}
			}
		}
		if (annotation != null) {
			if (getMappingValues != null && getMappingValues.length > 0) {
				for (String shortUrl : getMappingValues) {
					urls.add(headUrl);
					urls.add(shortUrl);
					return HttpUtils.getUrl(urls);
				}
			} else {
				urls.add(headUrl);
				return HttpUtils.getUrl(urls);
			}
		}
		return null;
	}

}
