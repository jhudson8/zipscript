/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang;

import hudson.zipscript.parser.ExpressionParser;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.lang.variable.VariablePatternMatcher;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.special.VarSpecialElement;
import hudson.zipscript.parser.util.StringUtil;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TextElement extends AbstractElement implements Element {

	private VarSpecialElement[] specialMethods = null;

	public static final PatternMatcher[] MATCHERS = new PatternMatcher[] { new VariablePatternMatcher() };

	private boolean evaluateText;
	private String text;
	private List children;

	public TextElement(String text) {
		this(text, false);
	}

	public TextElement(String text, boolean evaluateText) {
		this.text = text;
		this.evaluateText = evaluateText;
	}

	public Object objectValue(ExtendedContext context) {
		if (evaluateText) {
			if (null != specialMethods) {
				StringWriter sw1 = new StringWriter();
				for (Iterator i = getChildren().iterator(); i.hasNext();) {
					((Element) i.next()).merge(context, sw1);
				}
				Object source = sw1.toString();
				for (int i = 0; i < specialMethods.length; i++) {
					source = specialMethods[i].execute(source,
							RetrievalContext.HASH, null, context);
					if (null == source)
						return null;
				}
				return source;
			} else {
				if (getChildren().size() == 0) return "";
				StringWriter sw1 = new StringWriter();
				for (Iterator i = getChildren().iterator(); i.hasNext();) {
					((Element) i.next()).merge(context, sw1);
				}
				return sw1.toString();
			}
		} else {
			return text;
		}
	}

	public boolean booleanValue(ExtendedContext context) {
		return false;
	}

	public void merge(ExtendedContext context, Writer sw) {
		if (evaluateText) {
			if (null != specialMethods) {
				StringWriter sw1 = new StringWriter();
				for (Iterator i = getChildren().iterator(); i.hasNext();) {
					((Element) i.next()).merge(context, sw1);
				}
				Object source = sw1.toString();
				for (int i = 0; i < specialMethods.length; i++) {
					source = specialMethods[i].execute(source,
							RetrievalContext.HASH, null, context);
					if (null == source)
						return;
				}
				StringUtil.append(source.toString(), sw);
			} else {
				for (Iterator i = getChildren().iterator(); i.hasNext();) {
					((Element) i.next()).merge(context, sw);
				}
			}
		} else if (null != text) {
			StringUtil.append(text, sw);
		}
	}

	public void setText(String text) {
		this.text = text;
	}

	public String toString() {
		return "'" + text + "'";
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		if (evaluateText) {
			ParsingResult pr = ExpressionParser.getInstance().parse(text,
					MATCHERS, TextDefaultElementFactory.INSTANCE, session,
					(int) (getElementPosition() + 1));
			setChildren(pr.getElements());

			List varSpecialElements = new ArrayList();
			// pick up any special methods
			while (elementList.size() > index) {
				Element e = (Element) elementList.get(index);
				if (e instanceof VarSpecialElement) {
					elementList.remove(index);
					ElementIndex ei = e.normalize(index, elementList, session);
					if (null != ei) {
						index = ei.getIndex();
						e = ei.getElement();
					}
					varSpecialElements.add(e);
				} else
					break;
			}
			if (varSpecialElements.size() > 0) {
				this.specialMethods = (VarSpecialElement[]) varSpecialElements
						.toArray(new VarSpecialElement[varSpecialElements
								.size()]);
			}
		}
		return null;
	}

	public String getText() {
		return text;
	}

	public List getChildren() {
		return children;
	}

	public void setChildren(List children) {
		this.children = children;
	}

	public boolean isStatic () {
		return (null == children || children.size() == 1);
	}
}