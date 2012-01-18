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

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Document;

public class XMLTestCase extends TestCase {

	public void testXML() throws Exception {
		Document context = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().parse(
						getClass().getResourceAsStream(
								"/templates/xml_input.xml"));
		evalResult("templates/xml_test.zs", "/templates/xml_result.txt",
				context);
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
		HashMap props = new HashMap();
		props.put("includeResourceLoader.type", "classpath");
		props.put("includeResourceLoader.pathPrefix", "templates/includes/");
		ZipEngine zipEngine = ZipEngine.createInstance(props);
		return zipEngine.getTemplate(template).merge(context);
	}
}
