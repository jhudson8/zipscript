/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.Constants;
import hudson.zipscript.resource.StringResourceLoader;
import hudson.zipscript.template.Template;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class InitPropertiesTestCase extends TestCase {

	public void testSuppressNullErrors() throws Exception {
		Map props = new HashMap();
		props.put(Constants.TEMPLATE_RESOURCE_LOADER_CLASS,
				StringResourceLoader.class.getName());
		props.put(Constants.SUPPRESS_NULL_ERRORS, Boolean.TRUE);
		ZipEngine engine = ZipEngine.createInstance(props);
		Template template = engine.getTemplate("${somethingMissing}");
		String s = template.merge(null);
		assertEquals("${somethingMissing}", s);
	}
}
