package org.junit.internal.runners;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodValidator {
	private final List<Throwable> fErrors= new ArrayList<Throwable>();

	private final JavaClass fJavaClass;

	public MethodValidator(Class<?> testClass) {
		fJavaClass= new JavaClass(testClass);
	}

	public void validateInstanceMethods() {
		validateTestMethods(After.class, false);
		validateTestMethods(Before.class, false);
		validateTestMethods(Test.class, false);
	}

	public void validateStaticMethods() {
		validateTestMethods(BeforeClass.class, true);
		validateTestMethods(AfterClass.class, true);
	}
	
	// TODO Ugly API--one method should do both
	public List<Throwable> validateAllMethods() {
		validateNoArgConstructor();
		validateStaticMethods();
		validateInstanceMethods();
		return fErrors;
	}
	
	public void assertValid() throws InitializationError {
		if (!fErrors.isEmpty())
			throw new InitializationError(fErrors);
	}

	public void validateNoArgConstructor() {
		fJavaClass.validateNoArgConstructor(fErrors);
	}

	private void validateTestMethods(Class<? extends Annotation> annotation,
			boolean isStatic) {
		List<JavaMethod> methods= fJavaClass.getTestMethods(annotation);
		for (JavaMethod eachMethod : methods) {
			eachMethod.validateAsTestMethod(isStatic, fErrors);
		}
	}
}
