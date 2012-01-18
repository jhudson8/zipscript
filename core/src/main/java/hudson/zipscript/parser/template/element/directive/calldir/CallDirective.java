/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.calldir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.ElementAttribute;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.directive.AbstractDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroDirective;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceEntity;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroInstanceExecutor;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroOrientedElement;
import hudson.zipscript.parser.template.element.lang.AssignmentElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.variable.VariableElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;
import hudson.zipscript.parser.template.element.special.WithPatternMatcher;
import hudson.zipscript.resource.macrolib.MacroProvider;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CallDirective extends AbstractDirective {

	private String macroName;
	private String macroNamespace;
	private MacroDirective macroDirective;
	private Element withElement;
	private List additionalAttributes;
	private MacroDirective inMacroDirective;

	private String contents;
	private int contentStartPosition;

	private static PatternMatcher[] MATCHERS;
	static {
		PatternMatcher[] matchers = Constants.VARIABLE_MATCHERS;
		MATCHERS = new PatternMatcher[matchers.length + 1];
		System.arraycopy(matchers, 0, MATCHERS, 1, matchers.length);
		MATCHERS[0] = new WithPatternMatcher();
	}

	public CallDirective(String contents, ParsingSession session,
			int contentStartPosition) throws ParseException {
		setParsingSession(session);
		this.contents = contents;
		this.contentStartPosition = contentStartPosition;
	}

	public void validate(ParsingSession session) throws ParseException {
		// we must be within a macro definition
		for (int i = session.getNestingStack().size() - 1; i >= 0; i--) {
			if (session.getNestingStack().get(i) instanceof MacroDirective) {
				inMacroDirective = (MacroDirective) session.getNestingStack()
						.get(i);
				break;
			}
		}
		if (null == inMacroDirective)
			throw new ParseException(this,
					"[#call] directives can only used within a [#macro] directive");

		StringBuffer sb = new StringBuffer();
		List mainElements = new ArrayList();
		List additionalParameters = null;
		// I know I could use a tokenizer but I wanted to get all whitespace
		for (int i = 0; i < contents.length(); i++) {
			char c = contents.charAt(i);
			if (Character.isWhitespace(c)) {
				if (sb.length() > 0) {
					mainElements.add(sb.toString());
					sb = new StringBuffer();
				}
			} else if (c == '|') {
				if (mainElements.size() != 3) {
					throw new ParseException(contentStartPosition + i,
							"Invalid character '|'");
				} else {
					String s = contents.substring(i + 1, contents.length());
					additionalParameters = parseElements(s, session,
							contentStartPosition + i + 1);
					break;
				}
			} else
				sb.append(c);
		}
		if (sb.length() > 0)
			mainElements.add(sb.toString());

		if (mainElements.size() != 3) {
			throw new ParseException(contentStartPosition,
					"Invalid call directive.  Should be [#call macroName with macroVariable/]");
		} else {
			macroName = mainElements.get(0).toString();
			int index = macroName.indexOf('.');
			if (index > 0) {
				String s = macroName;
				macroNamespace = s.substring(0, index);
				macroName = s.substring(index + 1, s.length());
			}
			this.macroDirective = session.getResourceContainer()
					.getMacroManager().getMacro(macroName, macroNamespace,
							session);
			if (!mainElements.get(1).equals("with"))
				throw new ParseException(contentStartPosition,
						"Invalid call directive.  Should be [#call macroName with macroVariable/]");

			withElement = new VariableElement(true, false,
					(String) mainElements.get(2), session, contentStartPosition);
		}

		if (null != additionalParameters) {
			this.additionalAttributes = new ArrayList();
			ElementAttribute attribute = getAttribute(additionalParameters,
					session);
			while (null != attribute) {
				additionalAttributes.add(attribute);
				attribute = getAttribute(additionalParameters, session);
			}
		}

		if (getParsingSession().isDebug()) {
			System.out.println("Parsed: " + this.toString());
			if (null != additionalAttributes) {
				for (Iterator i = additionalAttributes.iterator(); i.hasNext();) {
					System.out.println("\t" + i.next());
				}
			}
		}
	}

	protected ElementAttribute getAttribute(List elements,
			ParsingSession session) throws ParseException {
		if (elements.size() == 0)
			return null;

		String name = null;
		Element e;
		e = (Element) elements.remove(0);
		if (e instanceof SpecialStringElement)
			name = ((SpecialStringElement) e).getTokenValue();
		else if (e instanceof TextElement)
			name = ((TextElement) e).getText();
		else
			throw new ParseException(this,
					"Unexpected element, expecting macro attribute name.  Found '"
							+ e + "'");
		// validate name
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-'))
				throw new ParseException(this, "Invalid macro attribute name '"
						+ name + "'");
		}

		// attribute properties
		if (elements.size() > 0) {
			e = (Element) elements.get(0);
			if (e instanceof AssignmentElement) {
				elements.remove(0);
				if (elements.size() == 0) {
					throw new ParseException(this, "Unexpected content '" + e
							+ "'");
				} else {
					// value
					e = (Element) elements.remove(0);
					if (e instanceof AssignmentElement) {
						throw new ParseException(this, "Unexpected content '"
								+ e + "'");
					} else {
						ElementAttribute attribute = new ElementAttribute(name,
								e);
						return attribute;
					}
				}
			} else {
				throw new ParseException(this,
						"Unexpected element, expecting '='.  Found '" + e + "'");
			}
		} else {
			throw new ParseException(this, "Missing macro value for '" + name
					+ "' in " + this.toString());
		}
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		if (getParsingSession().isDebug()) {
			System.out.println("Executing: " + this.toString());
			if (null != additionalAttributes) {
				for (Iterator i = additionalAttributes.iterator(); i.hasNext();) {
					System.out.println("\t" + i.next());
				}
			}
		}

		Object obj = withElement.objectValue(context);
		if (obj instanceof MacroInstanceEntity) {
			MacroInstanceEntity callInput = (MacroInstanceEntity) obj;
			MacroOrientedElement macroInstance = callInput.getMacroInstance();
			MacroInstanceExecutor executor = new MacroInstanceExecutor(
					macroInstance, context);
			if (null == additionalAttributes) {
				getMacroDirective(context.getParsingSession()).executeMacro(
						context, macroInstance.isOrdinal(),
						macroInstance.getAttributes(), executor, sw);
			} else {
				List l = new ArrayList(additionalAttributes.size()
						+ macroInstance.getAttributes().size());
				l.addAll(additionalAttributes);
				l.addAll(macroInstance.getAttributes());
				getMacroDirective(context.getParsingSession()).executeMacro(
						context, macroInstance.isOrdinal(), l, executor, sw);
			}
		} else {
			System.out.println("The object is: " + obj);
			throw new ExecutionException(
					"Invalid call: a macro instance must be passed", this);
		}
	}

	protected MacroDirective getMacroDirective(ParsingSession session) {
		if (null == macroDirective) {
			// we might have to lazy load
			MacroProvider macroProvider = session;
			if (null != inMacroDirective.getMacroLibrary())
				macroProvider = inMacroDirective.getMacroLibrary();
			macroDirective = session.getResourceContainer().getMacroManager()
					.getMacro(macroName, macroNamespace, macroProvider);
		}
		if (null == macroDirective) {
			throw new ExecutionException("Unknown macro '" + macroName + "'",
					this);
		}
		return macroDirective;
	}

	protected PatternMatcher[] getContentParsingPatternMatchers() {
		return MATCHERS;
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		return null;
	}

	public List getChildren() {
		return null;
	}

	public String toString() {
		return "[#call " + macroName + " with " + withElement + "]";
	}
}