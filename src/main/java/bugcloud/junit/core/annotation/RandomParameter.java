package bugcloud.junit.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记随机参数的注解
 * @author yuzhantao
 *
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RandomParameter {
	/**
	 * 类名称
	 * @return
	 */
	String className() default "";
	/**
	 * 参数名
	 * @return
	 */
	String parameterName() default "";
	/**
	 * 参数类型
	 * @return
	 */
	Class<?> parameterType() default Object.class;
}
