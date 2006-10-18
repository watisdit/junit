package org.junit.internal.runners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;

public class JavaMethodList extends ArrayList<JavaMethod> {
	private static final long serialVersionUID= 1L;

	void filter(Filter filter) throws NoTestsRemainException {
		for (Iterator<JavaMethod> iter= iterator(); iter.hasNext();) {
			JavaMethod method= iter.next();
			if (!filter.shouldRun(method.description()))
				iter.remove();
		}
		if (isEmpty())
			throw new NoTestsRemainException();
	}

	void filter(final Sorter sorter) {
		Collections.sort(this, new Comparator<JavaMethod>() {
			public int compare(JavaMethod o1, JavaMethod o2) {
				return sorter.compare(o1.description(), o2.description());
			}
		});
	}
}
