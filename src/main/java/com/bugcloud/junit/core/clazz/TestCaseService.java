package com.bugcloud.junit.core.clazz;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bugcloud.junit.core.vo.TestMethod;

public class TestCaseService {
	private Map<String,TestMethod> testMethod=new ConcurrentHashMap<>();
	private static TestCaseService instance;
	
	public static TestCaseService getInstance() {
		if(instance==null) {
			instance = new TestCaseService();
		}
		return instance;
	}
	
	public TestMethod getTestMethod(String testMethodLongName) {
		return this.testMethod.get(testMethodLongName);
	}
	
	public void addTestMethod(TestMethod method) {
		this.testMethod.put(method.getLongName(), method);
	}
	
	public void removeTestMethod(String testMethodLongName) {
		this.testMethod.remove(testMethodLongName);
	}
}
