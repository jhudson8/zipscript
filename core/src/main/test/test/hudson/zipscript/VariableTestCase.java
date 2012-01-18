/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.util.IOUtil;
import hudson.zipscript.template.Evaluator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import test.hudson.zipscript.model.Obj1;
import test.hudson.zipscript.model.Obj3;

public class VariableTestCase extends TestCase {

	 public static TestSuite suite () {
	 TestSuite suite = new TestSuite();
	 suite.addTest(new VariableTestCase("testSimpleVariables"));
	 return suite;
	 }

	public VariableTestCase() {
	}

	public VariableTestCase(String name) {
		super(name);
	}

	public void testSeparatedPathVariables() throws Exception {
		Evaluator template = null;
		template = ZipEngine.createInstance().getEvaluator("foo.bar");
		Map context = new HashMap();
		context.put("foo.bar", "black sheep");
		assertEquals("black sheep", template.objectValue(context));
		template = ZipEngine.createInstance().getEvaluator("foo.bar");
		assertEquals("black sheep", template.objectValue(context));
	}

	public void testSimpleVariables() throws Exception {
		String mergeTemplate = "templates/variable_simple_test.zs";
		String resultFile = "/templates/variable_simple_result.txt";
		Map context = new HashMap();
		context.put("myObject", new Obj1());
		context.put("myString", "this is a test");
		context.put("myList", new ArrayList());
		context.put("obj", new Obj3("foo bar"));
		context.put("dynamicPath", "text");
		
		context.put("fieldErrorName", "foo");
		Map errors = new HashMap();
		errors.put("foo", "abc");
		context.put("fieldErrors", errors);
		
		evalResult(mergeTemplate, resultFile, context);
	}

	private void evalResult(String mergeTemplate, String resultFile,
			Object context) throws ParseException, ExecutionException,
			IOException {
		String expectedResult = IOUtil.toString(getClass().getResourceAsStream(
				resultFile));
		String actualResult = merge(mergeTemplate, context);
		assertEquals(expectedResult, actualResult);
	}

	private String merge(String template, Object context)
			throws ParseException, ExecutionException, IOException {
		return ZipEngine.createInstance().getTemplate(template).merge(context);
	}
}