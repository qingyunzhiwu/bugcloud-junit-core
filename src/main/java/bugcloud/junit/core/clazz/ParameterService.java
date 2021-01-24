package bugcloud.junit.core.clazz;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import bugcloud.junit.core.annotation.RandomParameter;

/**
 * 参数服务类
 * 
 * @author yuzhantao
 *
 */
public class ParameterService {
	private Map<String, Object> createParameterClassMap = new ConcurrentHashMap<>();

	private static ParameterService instance;

	public static ParameterService getInstance() {
		if (instance == null) {
			instance = new ParameterService();
		}
		return instance;
	}

	/**
	 * 添加参数创建类
	 * 
	 * @param createClass 可以创建参数值的类
	 * @param useClass    使用创建类的其它类数组
	 * @throws Exception
	 */
	public void addParameterCreateClass(Class<?> createClass, String[] useClassNames) throws Exception {
		Object createObj = createClass.newInstance();
		for (String cls : useClassNames) {
			this.createParameterClassMap.put(cls, createObj);
		}
	}

	/**
	 * 获取参数值
	 * 
	 * @param useClass      使用创建类的测试类
	 * @param method        测试方法
	 * @param parameterName 参数名
	 * @return
	 * @throws Exception
	 */
	public String createParameterValue(String useClassName,String methodName, String parameterName, String paramType) throws Exception {
		if (this.createParameterClassMap.containsKey(useClassName)) {
			Object createInstance = this.createParameterClassMap.get(useClassName); // 获取创建类的实例
			// 遍历用户写的测试类中的方法，查找是否有定义随机参数的函数，如果有，就用用户函数计算参数值
			Method[] methods = createInstance.getClass().getMethods();
			for (Method m : methods) {
				RandomParameter rp = m.getAnnotation(RandomParameter.class);
				if (rp == null)
					continue;
				boolean isMatch = Pattern.matches(rp.className(), useClassName);
				if (!isMatch && "".equals(rp.className()) == false)
					continue;
				isMatch = Pattern.matches(rp.methodName(), methodName);
				if (!isMatch && "".equals(rp.methodName()) == false)
					continue;
				isMatch = Pattern.matches(rp.parameterName(), parameterName);
				if (!isMatch && "".equals(rp.parameterName()) == false)
					continue;
				if (rp.parameterType() != Object.class && paramType.equals(rp.parameterType().getName()) == false) {
					continue;
				}
				return (String) m.invoke(createInstance, new Object[] {});
			}
			return this.createParameterValueByAI(parameterName, paramType); // 如果没有自定义函数，就用内部算法计算参数值
		} else {
			return this.createParameterValueByAI(parameterName, paramType); // 如果没有自定义函数，就用内部算法计算参数值
		}
	}

	/**
	 * 根据智能化算法创建参数值
	 * 
	 * @param parameterName
	 * @param paramType
	 * @return
	 */
	private String createParameterValueByAI(String parameterName, String paramType) {
		return UUID.randomUUID().toString();
	}
}
