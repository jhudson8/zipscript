/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.ExpressionParser;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementParsingSession;
import hudson.zipscript.parser.template.data.ParseParameters;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.special.DefaultVariablePatternMatcher;
import hudson.zipscript.parser.template.element.special.SpecialElement;
import hudson.zipscript.parser.template.element.special.SpecialStringElement;

import java.util.List;

public abstract class AbstractElement implements Element {

	private long elementPosition;
	private int elementLength;
	private ParsingSession parsingSession;

	public AbstractElement() {
	}

	public String toString() {
		if (this instanceof SpecialElement) {
			return ((SpecialElement) this).getTokenValue();
		} else if (this instanceof SpecialStringElement) {
			return "'" + ((SpecialStringElement) this).getTokenValue() + "'";
		} else
			return super.toString();
	}

	public long getElementPosition() {
		return elementPosition;
	}

	public void setElementPosition(long elementPosition) {
		this.elementPosition = elementPosition;
	}

	public int getElementLength() {
		return elementLength;
	}

	public void setElementLength(int elementLength) {
		this.elementLength = elementLength;
	}

	protected Element parseElement(String contents,
			ParsingSession parsingSession, int startPosition)
			throws ParseException {
		ElementParsingSession eps = ExpressionParser.getInstance()
				.parseToElement(contents, getContentParsingPatternMatchers(),
						getContentParsingDefaultElementFactory(),
						startPosition, parsingSession.getResourceContainer());
		if (null != eps)
			return eps.element;
		else
			return null;
	}

	protected List parseElements(String contents, ParsingSession session,
			int startPosition) throws ParseException {
		return parseElements(contents, session, startPosition, true);
	}

	protected List parseElements(String contents, ParsingSession session,
			int startPosition, DefaultElementFactory defaultElementFactory) throws ParseException {
		return parseElements(contents, session, startPosition, true, defaultElementFactory);
	}

	protected List parseElements(String contents, ParsingSession session,
			int startPosition, boolean hideEscaping) throws ParseException {
		return parseElements(contents, session, startPosition,
				hideEscaping, getContentParsingDefaultElementFactory());
	}
	
	protected List parseElements(String contents, ParsingSession session,
			int startPosition, boolean hideEscaping, DefaultElementFactory defaultElementFactory) throws ParseException {
		session = session.clone(new ParseParameters(session
				.getResourceContainer(), true, true));
		if (hideEscaping)
			session.setHideEscapeMethods(true);
		List rtn = ExpressionParser.getInstance().parse(contents,
				getContentParsingPatternMatchers(),
				defaultElementFactory, session,
				startPosition).getElements();
		return rtn;
	}

	protected PatternMatcher[] getContentParsingPatternMatchers() {
		return Constants.VARIABLE_MATCHERS;
	}

	protected DefaultElementFactory getContentParsingDefaultElementFactory() {
		return DefaultVariablePatternMatcher.getInstance();
	}

	public ParsingSession getParsingSession() {
		return parsingSession;
	}

	public void setParsingSession(ParsingSession parsingSession) {
		this.parsingSession = parsingSession;
	}

	public void validate(ParsingSession session) throws ParseException {
	}
}