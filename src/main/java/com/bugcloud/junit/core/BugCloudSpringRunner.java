package com.bugcloud.junit.core;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bugcloud.junit.core.listener.BugCloudRunListener;

/**
 * 基于BugCloud的SpringRunner
 * 
 * @author yuzhantao
 *
 */
public class BugCloudSpringRunner extends SpringJUnit4ClassRunner {
	private Class<?> testClass;

	public BugCloudSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		this.testClass = clazz;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.addListener(new BugCloudRunListener(this.testClass));
		super.run(notifier);
	}
}
