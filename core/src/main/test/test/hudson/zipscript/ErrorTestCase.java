/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package test.hudson.zipscript;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.LinePosition;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.element.DebugElementContainerElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.template.Template;
import hudson.zipscript.template.TemplateImpl;

import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

public class ErrorTestCase extends TestCase {

	public ErrorTestCase() {
	}

	public ErrorTestCase(String name) {
		super(name);
	}

	// public void _testTopLevelDirectives () throws Exception {
	// TemplateImpl t = null;
	// ParsingResult pr = null;
	// long[] arr = null;
	//		
	// evalResult("top_level_directive.zs", 3, 0);
	// }

	public void testInvalidVariables() {
		evalResult("variable1.zs", 2, 7, false);
		evalResult("variable2.zs", 2, 7, false);
		evalResult("variable3.zs", 2, 7, false);
		evalResult("variable4.zs", 2, 7, false);
	}

//	public void testWhileDirective() {
//		evalResult("while1.zs", 7, 13, false);
//	}

	public void testForeachDirective() {
		evalResult("foreach1.zs", 6, 15, false);
		evalResult("foreach2.zs", 6, 11, false);
	}

	public void testIfDirective() {
		evalResult("if1.zs", 7, 0, false);
		evalResult("if2.zs", 9, 0, false);
	}

	private void evalResult(String mergeTemplate, int line, int position,
			boolean showError) {
		try {
			Template t = ZipEngine.createInstance().getTemplate(
					"templates/error/" + mergeTemplate);
			t.merge(null);
		} catch (ParseException e) {
			if (showError)
				System.out.println(e.getMessage());
			assertEquals(line, e.getLine());
			assertEquals(position, e.getPosition());
			return;
		} catch (ExecutionException e) {
			if (showError)
				System.out.println(e.getMessage());
			assertEquals(line, e.getLine());
			assertEquals(position, e.getPosition());
			return;
		}
		assertTrue("The expected exception was not thrown", false);
	}

	public static void main(String[] args) throws Exception {
		String templatePath = "templates/macro_fulltest_test.zs";
		long checkPos = 511;

		TemplateImpl t = (TemplateImpl) ZipEngine.createInstance().getTemplate(
				templatePath);
		printChildInfo(t, t.getParsingResult(), "");

		System.out.println("\n\n----- Actual Line Numbers -----\n");
		// print out the line break positions
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(templatePath);
		long pos = 0;
		int lineNum = 1;
		int linePos = 0;
		int i = is.read();
		String checkPosContents = null;
		while (i != -1) {
			char c = (char) i;

			if (pos == checkPos) {
				checkPosContents = "Position '" + checkPos + "' = '" + lineNum
						+ ":" + linePos + " at char '" + c + "'";
			}

			if (c == '\n') {
				System.out.println("Line " + (++lineNum)
						+ " starts as position " + (pos + 1));
				linePos = -1;
			}
			i = is.read();
			pos++;
			linePos++;
		}
		is.close();

		System.out.println("\n\n" + checkPosContents);
	}

	private static void printChildInfo(Element e, ParsingResult pr,
			String prefix) {
		LinePosition lp = pr.getLinePosition(e.getElementPosition());
		System.out.println(prefix + e + " (Line: " + lp.line + ", Pos: "
				+ lp.position + ": " + lp.absolutePosition + ")");
		if (null != e.getChildren()) {
			for (int i = 0; i < e.getChildren().size(); i++) {
				if (e instanceof DebugElementContainerElement) {
					List internalElements = ((DebugElementContainerElement) e)
							.getInternalElements();
					System.out
							.println(prefix
									+ "\\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/ \\/");
					for (int j = 0; j < internalElements.size(); j++) {
						printChildInfo((Element) internalElements.get(j), pr,
								prefix);
					}
					System.out
							.println(prefix
									+ "/\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\ /\\");
				}
				printChildInfo((Element) e.getChildren().get(i), pr, prefix
						+ "   ");
			}
		}
	}
}