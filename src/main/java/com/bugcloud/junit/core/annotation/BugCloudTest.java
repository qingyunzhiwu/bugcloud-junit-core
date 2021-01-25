package com.bugcloud.junit.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface BugCloudTest {
	/**
	 * 应用的Key
	 * @return
	 */
	String appKey() default "";
	/**
	 * 应用的安全码
	 * @return
	 */
	String appSecret() default "";
	/**
	 * 推送人账号
	 * @return
	 */
	String pusher() default "";
	/**
	 * 处理人账号
	 * @return
	 */
	String handler() default "";
	/**
	 * 是否推送测试报告
	 * @return
	 */
	boolean isPush() default true;
	/**
	 * 是否断言推送报告的异常
	 * @return
	 */
	boolean isAssertPushException() default true;
}
