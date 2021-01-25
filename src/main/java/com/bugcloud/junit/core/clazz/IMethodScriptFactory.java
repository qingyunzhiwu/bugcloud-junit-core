package com.bugcloud.junit.core.clazz;

import java.lang.reflect.Method;

import javassist.CtClass;
import javassist.CtMethod;

public interface IMethodScriptFactory {
	public CtMethod createScript(CtClass clazz,Method method) throws Exception;
}
