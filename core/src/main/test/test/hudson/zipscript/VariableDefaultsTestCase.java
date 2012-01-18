/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.template.Evaluator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class VariableDefaultsTestCase extends TestCase {

	public void _testNoContextExpressions() throws Exception {
		assertEquals("abc", eval("foo!'abc'"));
		assertEquals(new Integer(1), eval("foo!1"));
		assertEquals(new Short((short) 1), eval("foo!1s"));
		assertEquals(new Integer(1), eval("foo!1i"));
		assertEquals(new Long(1), eval("foo!1l"));
		assertEquals(new BigDecimal(1), eval("foo!1b"));
		assertEquals(new Float(1), eval("foo!1f"));
		assertEquals(new Double(1), eval("foo!1d"));
		assertEquals(Boolean.TRUE, eval("foo!true"));
		assertEquals(Boolean.FALSE, eval("foo!false"));
	}

	public void testContextExpressions() throws Exception {
		Map context = new HashMap();
		context.put("abc", "def");
		Map subMap = new HashMap();
		subMap.put("foo", "bibble");
		context.put("baz", subMap);
		assertEquals("def", eval("foo!abc", context));
		assertEquals("bibble", eval("foo!pop!baz.foo", context));
		context.put("pop", "babble");
		assertEquals("babble", eval("foo!pop!baz.foo", context));
	}

	private Object eval(String s) throws ParseException, ExecutionException {
		return eval(s, null);
	}

	private Object eval(String s, Object context) throws ParseException,
			ExecutionException {
		Evaluator t = ZipEngine.createInstance().getEvaluator(s);
		return t.objectValue(context);
	}
}
