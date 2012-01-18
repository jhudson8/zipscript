/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class ListTestCase extends TestCase {

	public void testNoContextExpressions() throws Exception {
		List l = (List) eval("{1, 2, 3}");
		assertEquals(3, l.size());
		assertEquals(new Integer(1), l.get(0));
		assertEquals(new Integer(2), l.get(1));
		assertEquals(new Integer(3), l.get(2));

		l = (List) eval("{'a', 'b', 'c'}");
		assertEquals(3, l.size());
		assertEquals("a", l.get(0));
		assertEquals("b", l.get(1));
		assertEquals("c", l.get(2));
	}

	public void testContextExpressions() throws Exception {
		Map context = new HashMap();
		context.put("foo", Boolean.FALSE);
		context.put("num3", new Integer(3));
		context.put("num4", new Double(4));

		List l = (List) eval("{1f, 'b', foo, num3}", context);
		assertEquals(4, l.size());
		assertEquals(new Float(1), l.get(0));
		assertEquals("b", l.get(1));
		assertEquals(Boolean.FALSE, l.get(2));
		assertEquals(new Integer(3), l.get(3));
	}

	private Object eval(String s) throws ParseException, ExecutionException {
		return eval(s, null);
	}

	private Object eval(String s, Object context) throws ParseException,
			ExecutionException {
		return ZipEngine.createInstance().getEvaluator(s).objectValue(context);
	}
}
