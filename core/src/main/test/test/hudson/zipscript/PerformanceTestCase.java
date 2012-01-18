/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.template.Template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import test.hudson.zipscript.model.Obj1;
import test.hudson.zipscript.model.Obj2;
import test.hudson.zipscript.model.Obj3;

public class PerformanceTestCase extends TestCase {

	public PerformanceTestCase() {
	}

	public PerformanceTestCase(String name) {
		super(name);
	}

	public void testPerformance() throws Exception {
		Template template = ZipEngine.createInstance().getTemplate(
				"templates/performance_test.zs");

		Map context = new HashMap();
		List someList = new ArrayList();
		someList.add("abc");
		someList.add("def");
		someList.add("ghi");
		context.put("someList", someList);
		context.put("obj1", new Obj1());
		context.put("obj2", new Obj2());
		context.put("obj3", new Obj3("test test test"));

		long currentMilis = System.currentTimeMillis();
		int numRuns = 1000;
		for (int i = 0; i < numRuns; i++) {
			template.merge(context);
		}
		long diff = System.currentTimeMillis() - currentMilis;
		float currentSeconds = ((float) diff / (float) 1000);
		System.out.println(numRuns + " merges in " + currentSeconds
				+ " seconds");
		assertTrue("Performance test failed!", (currentSeconds < 3));
	}
}