/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.ExpressionParser;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParseParameters;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NoAutoEscapeElement;
import hudson.zipscript.parser.template.element.ToStringWithContextElement;
import hudson.zipscript.parser.template.element.comparator.ComparatorElement;
import hudson.zipscript.parser.template.element.comparator.math.AbstractMathExpression;
import hudson.zipscript.parser.template.element.group.GroupElement;
import hudson.zipscript.parser.template.element.group.MapElement;
import hudson.zipscript.parser.template.element.lang.CommaElement;
import hudson.zipscript.parser.template.element.lang.DotElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.WhitespaceElement;
import hudson.zipscript.parser.template.element.lang.variable.adapter.HashAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.ObjectAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;
import hudson.zipscript.parser.template.element.lang.variable.adapter.SequenceAdapter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.VariableAdapterFactory;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;
import hudson.zipscript.parser.template.element.lang.variable.special.VarSpecialElement;
import hudson.zipscript.parser.template.element.special.SpecialElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;
import hudson.zipscript.parser.util.PropertyUtil;
import hudson.zipscript.parser.util.StringUtil;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VariableElement extends AbstractElement implements Element {

	boolean isSilenced;
	boolean isFormal;
	protected VariableChild[] children;
	private String pattern;
	private VariableTokenSeparatorElement[] specialElements;
	private SpecialMethod[] escapeMethods;
	private RetrievalContext retrievalContext = RetrievalContext.SCALAR;
	private String contextHint;

	private boolean suppressNullErrors;

	public VariableElement () {
		this.isFormal = false;
		this.isSilenced = false;
	}

	public VariableElement(boolean isFormal, boolean isSilenced,
			String pattern, ParsingSession session, int contentIndex)
			throws ParseException {
		setElementPosition(contentIndex);
		setElementLength(pattern.length());
		this.isFormal = isFormal;
		this.isSilenced = isSilenced;
		setPattern(pattern, session, contentIndex);
		this.suppressNullErrors = PropertyUtil.getProperty(
				Constants.SUPPRESS_NULL_ERRORS, false, session.getParameters()
						.getInitParameters());
	}

	public VariableElement(List elements, RetrievalContext retrievalContext,
			String contextHint, ParsingSession session) throws ParseException {
		this.retrievalContext = retrievalContext;
		this.contextHint = contextHint;
		this.children = parse(elements, session);
		normalize(0, Collections.EMPTY_LIST, session);
	}

	public void setPattern(String pattern, ParsingSession session,
			int contentIndex) throws ParseException {
		pattern = pattern.trim();
		if (!quickScan(pattern, session)) {
			if (session.isVariablePatternRecognized(pattern))
				throw new ParseException(this, "Invalid variable reference '"
						+ pattern + "'");
			session.setReferencedVariable(pattern);
			ParseParameters parameters = new ParseParameters(session
					.getResourceContainer(), false, true);
			ParseParameters currentParameters = session.getParameters();
			session.setParameters(parameters);
			java.util.List elements = ExpressionParser.getInstance().parse(
					pattern, Constants.VARIABLE_MATCHERS,
					SpecialVariableDefaultEelementFactory.INSTANCE, session,
					contentIndex).getElements();
			session.setParameters(currentParameters);
			this.children = parse(elements, session);
			session.markValidVariablePattern(pattern);
		}
	}

	private boolean quickScan(String pattern, ParsingSession parsingSession)
			throws ParseException {
		int trimIndex = 0;
		if (pattern.startsWith("$"))
			trimIndex++;
		if (pattern.indexOf('!') == trimIndex)
			trimIndex++;
		if (pattern.indexOf('{') == trimIndex)
			trimIndex++;
		if (trimIndex > 0) {
			if (pattern.length() > 1)
				pattern = pattern.substring(trimIndex, pattern.length() - 1);
			else
				throw new ParseException(this, "Invalid variable reference '"
						+ this + "'");
		}

		for (int i = 0; i < pattern.length(); i++) {
			char c = pattern.charAt(i);
			if (!(Character.isLetterOrDigit(c) || c == '_' || c == '-' || c == ':'))
				return false;
		}
		this.children = new VariableChild[] { new RootChild(pattern,
				parsingSession.getResourceContainer()
						.getVariableAdapterFactory()) };
		return true;
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		Object obj = objectValue(context);
		if (null != obj) {
			if (obj instanceof ToStringWithContextElement) {
				((ToStringWithContextElement) obj).append(context, sw);
			} else {
				StringUtil.append(obj.toString(), sw);
			}
		} else {
			if (!isSilenced) {
				if (suppressNullErrors)
					StringUtil.append(toString(), sw);
				else {
					obj = objectValue(context);
					throw new ExecutionException("Value evaluated as null "
							+ this.toString(), this);
				}
			}
		}
	}

	public Object objectValue(ExtendedContext context)
			throws ExecutionException {
		try {
			Object rtn = null;
			int count = 0;
			boolean isNullAllowed = false;
			if (null != children) {
				for (int i = 0; i < this.children.length; i++) {
					rtn = children[i].execute(rtn, context);
					if (!children[i].shouldReturnSomething())
						isNullAllowed = true;
					if (null == rtn) {
						break;
					}
					count++;
				}
			} else {
				// bypass path and get the full path from the context
				rtn = context.get(pattern, retrievalContext, contextHint);
			}
			if (null == rtn && count == 0) {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < this.children.length; i++) {
					if (sb.length() > 0)
						sb.append('.');
					if (null != children[i].getPropertyName())
						sb.append(children[i].getPropertyName());
				}
				if (sb.length() > 0) {
					pattern = sb.toString();
					rtn = context.get(pattern, retrievalContext, contextHint);
				}
			}
			if (null != specialElements) {
				if (null == rtn && (specialElements[0].requiresInput())) {
					return null;
				}

				for (int i = 0; i < specialElements.length; i++) {
					boolean requiresInput = specialElements[i].requiresInput();
					if (requiresInput || (null == rtn && !requiresInput)) {
						if (specialElements.length > i + 1
								&& specialElements[i + 1].requiresInput()) {
							rtn = specialElements[i].execute(rtn,
									specialElements[i + 1].getExpectedType(),
									null, context);
						} else {
							rtn = specialElements[i].execute(rtn,
									retrievalContext, contextHint, context);
						}
					}
				}
			}
			if (null != escapeMethods && !(rtn instanceof NoAutoEscapeElement)) {
				try {
					for (int i = 0; i < escapeMethods.length; i++) {
						rtn = escapeMethods[i].execute(rtn,
								RetrievalContext.TEXT, null, context);
					}
				} catch (Exception e) {
					throw new ExecutionException(e.getMessage(), this);
				}
			}
			if (isNullAllowed && null == rtn)
				return "";
			return rtn;
		} catch (ExecutionException e) {
			e.setElement(this);
			throw e;
		}
	}

	private static final int TYPE_CONTEXT = 1;
	private static final int TYPE_SEQUENCE = 2;
	private static short TYPE_MAP = 3;
	private static short TYPE_OBJECT = 4;

	private short type = Short.MIN_VALUE;
	private boolean doTypeChecking = false;

	private HashAdapter mapAdapter;
	private SequenceAdapter sequenceAdapter;
	private ObjectAdapter objectAdapter;

	public void put(Object value, ExtendedContext context) {
		Object parent = null;
		for (int i = 0; i < children.length - 1; i++) {
			parent = children[i].execute(parent, context);
			if (null == parent) {
				throw new ExecutionException("Null parent for set '" + this
						+ "'", this);
			}
		}

		// determine the key
		VariableChild c = (VariableChild) children[children.length - 1];
		Object key = null;
		if (c instanceof MapChild) {
			// hash or sequence set
			key = ((MapChild) c).getKeyElement().objectValue(context);
		} else if (c instanceof PropertyChild) {
			// property set
			key = c.getPropertyName();
		} else {
			throw new ExecutionException("Invalid set expression '" + this
					+ "'", this);
		}

		// determine the type
		if (doTypeChecking || type == Short.MIN_VALUE) {
			type = Short.MIN_VALUE;
			if (parent instanceof Context)
				type = TYPE_CONTEXT;
			if (type == Short.MIN_VALUE) {
				if (null != mapAdapter && mapAdapter.appliesTo(parent)) {
					type = TYPE_MAP;
				}
				if (type == Short.MIN_VALUE) {
					mapAdapter = context.getResourceContainer()
							.getVariableAdapterFactory().getHashAdapter(parent);
					if (null != mapAdapter) {
						type = TYPE_MAP;
					} else {
						if (null != sequenceAdapter
								&& sequenceAdapter.appliesTo(parent)) {
							type = TYPE_SEQUENCE;
						}
						if (type == Short.MIN_VALUE) {
							sequenceAdapter = context.getResourceContainer()
									.getVariableAdapterFactory()
									.getSequenceAdapter(parent);
							if (null != sequenceAdapter) {
								type = TYPE_SEQUENCE;
							} else {
								objectAdapter = context.getResourceContainer()
										.getVariableAdapterFactory()
										.getObjectAdapter(parent);
								type = TYPE_OBJECT;
							}
						}
					}
				}
			}
		}
		if (doTypeChecking) {
			put(parent, key, value, context);
		} else {
			try {
				put(parent, key, value, context);
			} catch (ClassCastException e) {
				this.doTypeChecking = true;
				put(value, context);
			}
		}
	}

	private void put(Object parent, Object key, Object value,
			ExtendedContext context) {
		if (type == TYPE_CONTEXT) {
			((Context) parent).put(key, value);
		} else if (type == TYPE_MAP) {
			mapAdapter.put(key, value, parent);
		} else if (type == TYPE_SEQUENCE) {
			if (key instanceof Number) {
				sequenceAdapter.setItemAt(((Number) key).intValue(), value,
						parent);
			} else {
				throw new ExecutionException("Invalid sequence identifier '"
						+ this + "'", this);
			}
		} else if (type == TYPE_OBJECT) {
			objectAdapter.set(key.toString(), value, parent);
		}
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		Object obj = objectValue(context);
		if (null == obj)
			throw new ExecutionException("The variable '" + this
					+ "' is null and can not be evaluated to a boolean", this);
		else if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		else
			throw new ExecutionException("The variable " + this
					+ " could not be evaluated to a boolean", this);
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {

		List specialElements = new ArrayList();
		if (null != this.specialElements) {
			for (int i = 0; i < this.specialElements.length; i++) {
				specialElements.add(this.specialElements[i]);
			}
		}
		List escapeMethods = new ArrayList();

		// check for maps or groups
		if (elementList.size() > index) {
			Element nextElement = (Element) elementList.get(index);
			while (nextElement instanceof MapElement
					|| nextElement instanceof GroupElement) {
				Element e = (Element) elementList.remove(index);
				ElementIndex ei = e.normalize(index, elementList, session);
				if (null != ei) {
					index = ei.getIndex();
					e = ei.getElement();
				}
			}
		}
		
		// if the next element over is a special char - process for variable
		if (elementList.size() > index) {
			Element nextElement = (Element) elementList.get(index);
			while (nextElement instanceof VarDefaultElement
					|| nextElement instanceof VarSpecialElement) {
				Element e = (Element) elementList.remove(index);
				ElementIndex ei = e.normalize(index, elementList, session);
				if (null != ei) {
					index = ei.getIndex();
					e = ei.getElement();
				}
				specialElements.add(e);
			}
		}
		// check for directive escaping
		if (session.getEscapeMethods().size() > 0) {
			escapeMethods = new ArrayList(1);
			for (int i = 0; i < session.getEscapeMethods().size(); i++) {
				SpecialMethod sm = (SpecialMethod) session.getEscapeMethods()
						.get(i);
				escapeMethods.add(sm);
			}
		}

		// now deal with retrieval contexts
		RetrievalContext normalElementLastRetrievalContext = this.retrievalContext;

		if (specialElements.size() > 0) {
			this.specialElements = (VariableTokenSeparatorElement[]) specialElements
					.toArray(new VariableTokenSeparatorElement[specialElements
							.size()]);
			if (this.specialElements[0].requiresInput()) {
				normalElementLastRetrievalContext = this.specialElements[0]
						.getExpectedType();
			}
		}
		if (escapeMethods.size() > 0) {
			this.escapeMethods = (SpecialMethod[]) escapeMethods
					.toArray(new SpecialMethod[escapeMethods.size()]);
		}

		if (null != specialElements && specialElements.size() > 0)
			normalElementLastRetrievalContext = ((VariableTokenSeparatorElement) specialElements
					.get(0)).getExpectedType();
		for (int i = 0; i < children.length; i++) {
			if (i == children.length - 1) {
				children[i]
						.setRetrievalContext(normalElementLastRetrievalContext);
				children[i].setContextHint(contextHint);
			} else {
				children[i].setRetrievalContext(RetrievalContext.HASH);
			}
		}

		return null;
	}

	public String getPattern() {
		if (null == this.pattern) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < children.length; i++) {
				if (i > 0)
					sb.append(".");
				sb.append(children[i]);
			}
			return sb.toString();
		} else {
			return pattern;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append('$');
		if (isSilenced)
			sb.append('!');
		if (isFormal)
			sb.append('{');
		sb.append(getPattern());
		if (isFormal)
			sb.append('}');
		return sb.toString();
	}

	protected VariableChild[] parse(List elements, ParsingSession session)
			throws ParseException {
		List children = new ArrayList();
		List specialElements = new ArrayList();

		boolean started = false;
		boolean wasWhitespace = false;
		boolean wasSeparator = false;
		for (int i = 0; i < elements.size(); i++) {
			Element e = (Element) elements.get(i);
			if (!started && e instanceof WhitespaceElement)
				continue;
			started = true;

			if (e instanceof WhitespaceElement) {
				wasWhitespace = true;
			} else if (e instanceof DotElement) {
				wasSeparator = true;
				wasWhitespace = false;
			} else if (e instanceof SpecialElement) {
				if (wasWhitespace)
					throw new ParseException(e, "Invalid whitespace");
				if (!wasSeparator && children.size() > 0)
					throw new ParseException(e,
							"Invalid sequence after sparator '" + e.toString()
									+ "'");
				wasWhitespace = false;
				wasSeparator = false;
				if (children.size() == 0)
					children.add(new RootChild(((SpecialElement) e)
							.getTokenValue(), session.getResourceContainer()
							.getVariableAdapterFactory()));
				else
					children.add(new PropertyChild(((SpecialElement) e)
							.getTokenValue()));
			} else if (e instanceof SpecialStringElement) {
				if (wasWhitespace)
					throw new ParseException(e, "Invalid whitespace");
				if (!wasSeparator && children.size() > 0)
					throw new ParseException(e,
							"Invalid sequence after sparator '" + e.toString()
									+ "'");
				wasWhitespace = false;
				wasSeparator = false;
				addChildProperty(((SpecialStringElement) e).getTokenValue(),
						children, session.getResourceContainer()
								.getVariableAdapterFactory());
				if (e instanceof VariableElement) {
					// check for special directives
					if (null != ((VariableElement) e).specialElements) {
						for (int j = 0; j < ((VariableElement) e).specialElements.length; j++)
							specialElements
									.add(((VariableElement) e).specialElements[j]);
					}
				}
			} else if (e instanceof TextElement) {
				if (wasWhitespace)
					throw new ParseException(e, "Invalid whitespace");
				if (!wasSeparator && children.size() > 0)
					throw new ParseException(e,
							"Invalid sequence after sparator '" + e.toString()
									+ "'");
				wasWhitespace = false;
				wasSeparator = false;
				if (children.size() == 0) {
					// qualified path to use as context key
					children.add(new TextElementRootChild(((TextElement) e)));
				} else {
					addChildProperty(((TextElement) e).getText(), children,
							session.getResourceContainer()
									.getVariableAdapterFactory());
				}
			} else if (e instanceof VariableElement) {
				if (wasSeparator) {
					// dynamic path
					children.add(new DynamicChild(e));
					wasSeparator = false;
				} else {
					if (elements.size() == 1) {
						// single variable element - just use it
						return ((VariableElement) e).children;
					} else {
						throw new ParseException(
								e,
								"Invalid dynamic variable element '"
										+ e.toString()
										+ "'.  This must be the first token or follow a separator");
					}
				}
			} else if (e instanceof GroupElement) {
				if (children.size() == 0)
					throw new ParseException(e, "Invalid element '"
							+ e.toString() + "'");
				else if (children.size() == 1) {
					VariableChild child = (VariableChild) children.remove(0);
					if (null == child.getPropertyName())
						throw new ParseException(e, "Invalid sequence '"
								+ e.toString() + "'");
					List parameters = getMethodParameters((GroupElement) e,
							session);
					children.add(new AssumedGetRoot(child.getPropertyName(),
							parameters, this, session.getResourceContainer()
									.getVariableAdapterFactory()));
				} else if (wasWhitespace)
					throw new ParseException(e,
							"Invalid sequence after whitespace '"
									+ e.toString() + "'");
				else if (wasSeparator)
					throw new ParseException(e,
							"Invalid sequence after separator '" + e.toString()
									+ "'");
				else {
					VariableChild child = (VariableChild) children
							.remove(children.size() - 1);
					if (null == child.getPropertyName())
						throw new ParseException(e, "Invalid sequence '"
								+ e.toString() + "'");
					List parameters = getMethodParameters((GroupElement) e,
							session);
					children.add(new MethodChild(child.getPropertyName(),
							parameters));
				}
				wasWhitespace = false;
			} else if (e instanceof VarDefaultElement
					|| e instanceof VarSpecialElement) {
				if (null == specialElements)
					specialElements = new ArrayList();
				specialElements.add(elements.get(i));
			} else if (e instanceof MapElement) {
				MapElement me = (MapElement) e;
				if (me.getChildren().size() == 1) {
					children
							.add(new MapChild(me, (Element) me.getChildren().get(0)));
				} else {
					children.add(new MapChild(me, new VariableElement(me
							.getChildren(), RetrievalContext.HASH, null,
							session)));
				}
			} else if (e instanceof ComparatorElement && elements.size() == 1) {
				children.add(new ElementWrapperChild(e));
			} else if (e instanceof AbstractMathExpression) {
				// we're using math
				throw new ParseException(e, "Need to support math here '"
						+ e.toString() + "'");
			} else {
				throw new ParseException(e, "Invalid element detected '"
						+ e.toString() + "'");
			}
		}

		if (specialElements.size() > 0)
			this.specialElements = (VariableTokenSeparatorElement[]) specialElements
					.toArray(new VariableTokenSeparatorElement[specialElements
							.size()]);
		return (VariableChild[]) children.toArray(new VariableChild[children
				.size()]);
	}

	private void addChildProperty(String s, List children,
			VariableAdapterFactory variableAdapterFactory) {
		if (children.size() == 0)
			children.add(new RootChild(s, variableAdapterFactory));
		else
			children.add(new PropertyChild(s));
	}

	private List getMethodParameters(GroupElement ge, ParsingSession parseData)
			throws ParseException {
		List parameters = new ArrayList();
		List t = new ArrayList();
		CommaElement lastSeparator = null;
		boolean lastElementWasComma = false;
		for (int i = 0; i < ge.getChildren().size(); i++) {
			Element e = (Element) ge.getChildren().get(i);
			if (e instanceof CommaElement) {
				Element mpe = getMethodParameterElement(t, parseData);
				if (null != mpe)
					parameters.add(mpe);
				t.clear();
				lastSeparator = (CommaElement) e;
				lastElementWasComma = true;
			} else {
				if (lastElementWasComma && e instanceof WhitespaceElement)
					continue;
				t.add(e);
				lastElementWasComma = false;
			}
		}

		boolean whitespaceOnly = true;
		for (int i = 0; i < t.size(); i++)
			if (!(t.get(i) instanceof WhitespaceElement))
				whitespaceOnly = false;
		if (whitespaceOnly && null != lastSeparator)
			throw new ParseException(lastSeparator, "Invalid sequence '"
					+ lastSeparator.toString() + "'");
		if (!whitespaceOnly) {
			Element mpe = getMethodParameterElement(t, parseData);
			if (null != mpe)
				parameters.add(mpe);
		}
		return parameters;
	}

	private Element getMethodParameterElement(List elements,
			ParsingSession parseData) throws ParseException {
		if (null == elements || elements.size() == 0)
			return null;
		if (elements.size() == 1) {
			Element e = (Element) elements.get(0);
			if (e instanceof SpecialStringElement) {
				return new VariableElement(true, false,
						((SpecialStringElement) e).getTokenValue(), parseData,
						(int) e.getElementPosition());
			} else {
				return e;
			}
		} else {
			return new VariableElement(elements, RetrievalContext.HASH, null,
					parseData);
		}
	}

	private Boolean isStatic;
	public boolean isStatic() {
		if (null == isStatic) {
		if (children.length == 1) {
			if (children[0] instanceof TextElementRootChild) {
				isStatic = new Boolean(
						((TextElementRootChild) children[0]).getTextElement().isStatic());
			}
			else {
				isStatic = Boolean.FALSE;
			}
		}
		else
			isStatic = Boolean.FALSE;
		}
		return isStatic.booleanValue();
	}

	protected void addSpecialElement(VariableTokenSeparatorElement e) {
		if (null == specialElements) {
			specialElements = new VariableTokenSeparatorElement[1];
			specialElements[0] = e;
		} else {
			VariableTokenSeparatorElement[] newElements = new VariableTokenSeparatorElement[specialElements.length + 1];
			System.arraycopy(specialElements, 0, newElements, 0,
					specialElements.length);
			newElements[newElements.length - 1] = e;
			specialElements = newElements;
		}
	}

	public List getChildren() {
		return null;
	}
}