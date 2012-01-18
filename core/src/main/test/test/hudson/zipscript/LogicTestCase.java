/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class LogicTestCase extends TestCase {

	public void testNoContextExpressions() throws Exception {
		assertEquals(true, eval("4>3||3<4"));
		assertEquals(true, eval("4>3&&3<4"));
		assertEquals(true, eval("4>3 && (3<4 || true)"));
		assertEquals(true, eval("4>3&&(3<4||true)"));
		assertEquals(true, eval("4>3&&(3<4||false)"));
		assertEquals(true, eval("4>=3&&4==4&&3<4&&3<=4"));

	}

	public void testContextExpressions() throws Exception {
		Map context = new HashMap();
		context.put("foo", "false");
		context.put("num3", new Integer(3));
		context.put("num4", new Double(4));

		assertEquals(true, eval("num4>num3||num3<num4", context));
		assertEquals(true, eval("num4>num3&&num3<num4", context));
		assertEquals(true, eval("num4>num3 && (num3<num4 || true)", context));
		assertEquals(true, eval("num4>num3&&(num3<num4||true)", context));
		assertEquals(true, eval("num4>num3&&(num3<num4||false)", context));
		assertEquals(true, eval(
				"num4>=num3&&num4==num4&&num3<num4&&num3<=num4", context));
	}

	private boolean eval(String s) throws ParseException, ExecutionException {
		return eval(s, null);
	}

	private boolean eval(String s, Object context) throws ParseException,
			ExecutionException {
		return ZipEngine.createInstance().getEvaluator(s).booleanValue(context);
	}
}
