/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.util.IOUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;
import test.hudson.zipscript.model.Person;

public class DirectiveTestCase extends TestCase {

//	public static TestSuite suite() {
//		TestSuite suite = new TestSuite();
//		suite.addTest(new DirectiveTestCase("testMacro"));
//		return suite;
//	}

	public DirectiveTestCase() {
	}

	public DirectiveTestCase(String name) {
		super(name);
	}

	// public void testSimple () throws Exception {
	// String mergeTemplate = "templates/simple_test.zs";
	// System.out.println(
	// ZipEngine.createInstance().getTemplate(mergeTemplate).merge(null));
	// }

	public void testForeach() throws Exception {
		String mergeTemplate = "templates/foreach_test.zs";
		String resultFile = "/templates/foreach_result.txt";
		Map context = null;

		context = new HashMap();
		String[] l1 = new String[] { "abc", "def", "ghi" };
		context.put("theList", l1);
		evalResult(mergeTemplate, resultFile, context);

		context = new HashMap();
		List l2 = new ArrayList();
		l2.add("abc");
		l2.add("def");
		l2.add("ghi");
		context.put("theList", l2);
		evalResult(mergeTemplate, resultFile, context);

		resultFile = "/templates/foreach_result_iter.txt";
		context = new HashMap();
		context.put("isIterator", Boolean.TRUE);
		context.put("theList", l2.iterator());
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testInitialize() throws Exception {
		String mergeTemplate = "templates/initialize_test.zs";
		String resultFile = "/templates/initialize_result.txt";
		ZipEngine ze = ZipEngine.createInstance();
		ze.addMacroLibrary("test", "templates/initialize_test_macro.zsm");
		hudson.zipscript.template.Template t = ze.getTemplate(mergeTemplate);
		HashMap obj = new HashMap();
		Context ctx = t.initialize(obj);
		assertEquals(new Integer(1), obj.get("inTemplateCount"));
		assertEquals(new Integer(1), obj.get("inMacroCount"));
		String s = t.merge(ctx);
		assertEquals(new Integer(1), obj.get("inTemplateCount"));
		assertEquals(new Integer(1), obj.get("inMacroCount"));
		String expectedResult = IOUtil.toString(getClass().getResourceAsStream(
				resultFile));
		assertEquals(expectedResult, s);
	}

	public void testWhile() throws Exception {
		String mergeTemplate = "templates/while_test.zs";
		String resultFile = "/templates/while_result.txt";
		evalResult(mergeTemplate, resultFile, null);
	}

	public void testInclude() throws Exception {
		String mergeTemplate = "templates/include_test.zs";
		String resultFile = "/templates/include_result.txt";
		HashMap context = new HashMap();
		context.put("dynamicInclude", "foo/dynamic.zs");
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testImport() throws Exception {
		String mergeTemplate = "templates/import_test.zs";
		String resultFile = "/templates/import_result.txt";
		HashMap context = new HashMap();
		context.put("dynamicImport", "templates/imports/dynamic.zsm");
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testEscape() throws Exception {
		String mergeTemplate = "templates/escape_test.zs";
		String resultFile = "/templates/escape_result.txt";
		evalResult(mergeTemplate, resultFile, null);
	}

	public void testIf() throws Exception {
		String mergeTemplate = "templates/if_test.zs";
		String resultFile = "/templates/if_result.txt";
		Map context = new HashMap();
		context.put("foo", "abc");
		context.put("bar", "def");
		context.put("baz", "ghi");
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testMacro() throws Exception {
		Map context = new HashMap();
		List l = new ArrayList(3);
		l.add(new Person("John", "Smith", "03/14/82"));
		l.add(new Person("Jimmy", "Carter", "09/03/63"));
		l.add(new Person("Jerry", null, "11/21/79"));
		context.put("people", l);
		String mergeTemplate = "templates/macro_test.zs";
		String resultFile = "/templates/macro_result.txt";
		evalResult(mergeTemplate, resultFile, context);

		mergeTemplate = "templates/macro_nesting_test.zs";
		resultFile = "/templates/macro_nesting_result.txt";
		evalResult(mergeTemplate, resultFile, null);
	}

	public void testSet() throws Exception {
		String mergeTemplate = "templates/set_test.zs";
		String resultFile = "/templates/set_result.txt";
		evalResult(mergeTemplate, resultFile, null);
	}

	public void testComment() throws Exception {
		String mergeTemplate = "templates/comment_test.zs";
		String resultFile = "/templates/comment_result.txt";
		evalResult(mergeTemplate, resultFile, null);
	}

	public void testTranslation() throws Exception {
		String mergeTemplate = "templates/translate_test.zs";
		Map context = new HashMap();
		context.put("name", "Joe");

		String actualResult = merge(mergeTemplate, context, Locale.FRENCH);
		System.out.println(actualResult);

		// String resultFile = "/templates/translate_result.txt";
		// evalResult(mergeTemplate, resultFile, null, Locale.FRENCH);
	}

	private void evalResult(String mergeTemplate, String resultFile,
			Object context) throws ParseException, ExecutionException,
			IOException {
		String expectedResult = IOUtil.toString(getClass().getResourceAsStream(
				resultFile));
		String actualResult = merge(mergeTemplate, context, null);
		assertEquals(expectedResult, actualResult);
	}

	private void evalResult(String mergeTemplate, String resultFile,
			Object context, Locale locale) throws ParseException,
			ExecutionException, IOException {
		String expectedResult = IOUtil.toString(getClass().getResourceAsStream(
				resultFile));
		String actualResult = merge(mergeTemplate, context, locale);
		assertEquals(expectedResult, actualResult);
	}

	private String merge(String template, Object context, Locale locale)
			throws ParseException, ExecutionException, IOException {
		HashMap props = new HashMap();
		props.put("includeResourceLoader.type", "classpath");
		props.put("includeResourceLoader.pathPrefix", "templates/includes/");
		ZipEngine zipEngine = ZipEngine.createInstance(props);
		return zipEngine.getTemplate(template)
				.merge(context, locale);
	}
}
