/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.special.xml;

import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.MapContextWrapper;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.variable.VariableElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class XPathSpecialMethod implements SpecialMethod {

	// dynamic
	private Element xPathElement;
	private Map compiledXpathStatements;

	// static
	private XPathExpression xPathExpression;

	public XPathSpecialMethod(Element[] vars) throws ExecutionException {
		if (null != vars && vars.length == 1) {
			this.xPathElement = vars[0];
			boolean isStatic = false;
			if (this.xPathElement instanceof VariableElement
					&& ((VariableElement) this.xPathElement).isStatic()) {
				isStatic = true;
			} else if (this.xPathElement instanceof TextElement) {
				isStatic = true;
			}
			if (isStatic) {
				try {
					this.xPathExpression = compile(new MapContextWrapper(
							new HashMap()), false);
				} catch (XPathExpressionException e) {
					throw new ExecutionException(e.getMessage(), null);
				}
			} else {
				compiledXpathStatements = new HashMap();
			}
		} else {
			throw new ExecutionException(
					"?xpath statements must provide 1 expression parameter",
					null);
		}
	}

	public Object execute(Object source, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) throws Exception {
		XPathExpression xPathExpression = this.xPathExpression;
		if (null == xPathExpression) {
			xPathExpression = compile(context, true);
		}

		if (retrievalContext.is(RetrievalContext.SEQUENCE)) {
			return xPathExpression.evaluate(source, XPathConstants.NODESET);
		} else if (retrievalContext.is(RetrievalContext.HASH)) {
			return xPathExpression.evaluate(source, XPathConstants.NODE);
		} else
			return xPathExpression.evaluate(source);
	}

	public RetrievalContext getExpectedType() {
		return RetrievalContext.HASH;
	}

	private XPath xPath;

	private XPathExpression compile(Context context, boolean cashXPath)
			throws XPathExpressionException {
		XPathExpression xPathExpression = null;
		String xPathStatement = this.xPathElement.objectValue(
				new MapContextWrapper(new HashMap())).toString();
		if (cashXPath) {
			xPathExpression = (XPathExpression) compiledXpathStatements
					.get(xPathStatement);
		}
		if (null == xPathExpression) {
			if (null == this.xPath) {
				this.xPath = XPathFactory.newInstance().newXPath();
			}
			xPathExpression = this.xPath.compile(xPathStatement);
			if (cashXPath)
				compiledXpathStatements.put(xPathStatement, xPathExpression);
			else
				this.xPath = null;
		}
		return xPathExpression;
	}

	public String toString() {
		return "xpath(" + xPathElement + ")";
	}
}