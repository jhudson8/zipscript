/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.util.IOUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class MacroLibTestCase extends TestCase {

	// public static TestSuite suite () {
	// TestSuite suite = new TestSuite();
	// suite.addTest(new MacroLibTestCase("testMacroLib"));
	// return suite;
	// }

	public MacroLibTestCase() {
	}

	public MacroLibTestCase(String name) {
		super(name);
	}

	public void testMacroLib() throws Exception {
		Map context = new HashMap();
		String mergeTemplate = "templates/macros/test.zs";
		String resultFile = "/templates/macros/test_result.txt";
		evalResult(mergeTemplate, resultFile, context);
	}

	private static ZipEngine engine = null;
	static {
		try {
			engine = ZipEngine.createInstance();
			engine.addMacroLibrary("cmn", "templates/macros/cmn.zsm");
			engine.addMacroLibrary("tab", "templates/macros/tab.zsm");
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
		return engine.getTemplate(template).merge(context);
	}
}
