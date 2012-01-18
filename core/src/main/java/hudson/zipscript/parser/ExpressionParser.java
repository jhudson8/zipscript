/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser;

import hudson.zipscript.ResourceContainer;
import hudson.zipscript.ext.data.DefaultElementContainer;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ElementParsingSession;
import hudson.zipscript.parser.template.data.ParseParameters;
import hudson.zipscript.parser.template.data.ParsingResult;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.DefaultElementFactory;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.component.Component;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableElementImpl;
import hudson.zipscript.parser.util.ElementNormalizer;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class ExpressionParser {

	private static final ExpressionParser INSTANCE = new ExpressionParser();

	public static final ExpressionParser getInstance() {
		return INSTANCE;
	}

	private ExpressionParser() {
	}

	/**
	 * Parse the given expression using the components for pattern matching
	 * 
	 * @param contents
	 *            the template expression contents
	 * @param components
	 *            pattern matcher components
	 * @param defaultElementFactory
	 *            factory to provide elements for non-matching elements
	 * @param startPosition
	 *            the start position for element positioning (0 for a new
	 *            document)
	 * @param macroManager
	 *            the macro manager
	 * @throws ParseException
	 */
	public ParsingResult parse(String contents, Component[] components,
			DefaultElementFactory defaultElementFactory, int startPosition,
			ResourceContainer resourceContainer) throws ParseException {
		ParseParameters parameters = new ParseParameters(resourceContainer,
				false, false);
		ParsingSession session = new ParsingSession(parameters);
		return parse(CharBuffer.wrap(contents.toCharArray()),
				getStartTokens(components), defaultElementFactory, session,
				startPosition);
	}

	public ParsingResult parse(String contents, Component[] components,
			DefaultElementFactory defaultElementFactory, int startPosition,
			ResourceContainer resourceContainer, int parsingContext) throws ParseException {
		ParseParameters parameters = new ParseParameters(resourceContainer,
				false, false);
		ParsingSession session = new ParsingSession(parameters, parsingContext);
		return parse(CharBuffer.wrap(contents.toCharArray()),
				getStartTokens(components), defaultElementFactory, session,
				startPosition);
	}
	
	/**
	 * Parse the given expression using the components for pattern matching
	 * 
	 * @param contents
	 *            the template expression contents
	 * @param components
	 *            pattern matcher components
	 * @param defaultElementFactory
	 *            factory to provide elements for non-matching elements
	 * @param startPosition
	 *            the start position for element positioning (0 for a new
	 *            document)
	 * @param session
	 *            the parsing session
	 * @throws ParseException
	 */
	public ParsingResult parse(String contents, Component[] components,
			DefaultElementFactory defaultElementFactory, int startPosition,
			ParsingSession session) throws ParseException {
		return parse(CharBuffer.wrap(contents.toCharArray()),
				getStartTokens(components), defaultElementFactory, session,
				startPosition);
	}

	/**
	 * Parse the given expression to a unique element
	 * 
	 * @param contents
	 *            the template expression contents
	 * @param components
	 *            pattern matcher components
	 * @param defaultElementFactory
	 *            defaultElementFactory factory to provide elements for
	 *            non-matching elements
	 * @param startPosition
	 *            defaultElementFactory factory to provide elements for
	 *            non-matching elements
	 * @param macroManager
	 *            the macro manager
	 * @throws ParseException
	 *             if parsing error or the document can not be boiled down to a
	 *             single element
	 */
	public Element parseToElement(String contents, Component[] components,
			DefaultElementFactory defaultElementFactory, int startPosition,
			ResourceContainer resourceContainer) throws ParseException {
		ParseParameters parameters = new ParseParameters(resourceContainer,
				true, true);
		ParsingSession session = new ParsingSession(parameters);
		ParsingResult data = parse(CharBuffer.wrap(contents),
				getStartTokens(components), defaultElementFactory, session,
				startPosition);
		if (data.getElements().size() == 1)
			return (Element) data.getElements().get(0);
		else
			return null;
	}

	/**
	 * Parse the given expression using the pattern matchers for pattern
	 * matching
	 * 
	 * @param contents
	 *            the template expression contents
	 * @param matchers
	 *            pattern matchers
	 * @param defaultElementFactory
	 *            factory to provide elements for non-matching elements
	 * @param startPosition
	 *            the start position for element positioning (0 for a new
	 *            document)
	 * @param macroManager
	 *            the macro manager
	 * @throws ParseException
	 */
	public ElementParsingSession parseToElement(String contents,
			PatternMatcher[] matchers,
			DefaultElementFactory defaultElementFactory, int startPosition,
			ResourceContainer resourceContainer) throws ParseException {
		ParseParameters parameters = new ParseParameters(resourceContainer,
				true, true);
		ParsingSession session = new ParsingSession(parameters);
		ParsingResult data = parse(CharBuffer.wrap(contents),
				getStartTokens(matchers), defaultElementFactory, session,
				startPosition);

		// check for special cases
		List elements = data.getElements();
		if (elements.size() > 1) {
			if (elements.get(0) instanceof SpecialVariableElementImpl) {
				SpecialVariableElementImpl e = (SpecialVariableElementImpl) elements
						.remove(0);
				e.setShouldEvaluateSeparators(true);
				ElementIndex ei = e.normalize(0, elements, session);
				if (null == ei) {
					elements.add(0, e);
				} else {
					int i = ei.getIndex();
					if (i >= 0)
						elements.add(i, ei.getElement());
				}
			}
		}

		if (data.getElements().size() == 1)
			return new ElementParsingSession((Element) data.getElements()
					.get(0), session);
		else if (data.getElements().size() == 0) {
			throw new ParseException(null, "Invlalid expression detected '"
					+ contents + "'");
		} else {
			throw new ParseException((Element) data.getElements().get(1),
					"Invlalid element detected '" + data.getElements().get(1)
							+ "'");
		}
	}

	public ParsingResult parse(String contents, PatternMatcher[] matchers,
			DefaultElementFactory defaultElementFactory,
			ParsingSession session, int startPosition) throws ParseException {
		ParsingResult data = parse(CharBuffer.wrap(contents),
				getStartTokens(matchers), defaultElementFactory, session,
				startPosition);
		return data;
	}

	private ParsingResult parse(CharBuffer buffer,
			StartTokenEntry[] startTokens,
			DefaultElementFactory defaultElementFactory,
			ParsingSession session, int startPosition) throws ParseException {
		List elements = new ArrayList();
		List lineBreaks = loadLineBreaks(buffer);
		buffer.position(0);
		StringBuffer unmatchedChars = new StringBuffer();
		StartTokenEntry startTokenEntry = null;
		try {
			char previousChar = Character.MIN_VALUE;
			while (buffer.hasRemaining()) {
				boolean match = false;
				char c = buffer.get();
				for (int i = 0; i < startTokens.length; i++) {
					if (c == startTokens[i].startToken[0]) {
						// possible start token match
						startTokenEntry = startTokens[i];
						int position = buffer.position();
						match = isMatch(buffer, startTokenEntry.startToken);
						if (match) {
							// we've got a start token match - remove remaining
							// start tokens from buffer
							for (int j = 1; j < startTokenEntry.startToken.length; j++)
								buffer.get();
							Element e = startTokenEntry.patternMatcher.match(
									previousChar, startTokenEntry.startToken,
									buffer, session, elements, unmatchedChars);

							if (null != e) {
								e.setElementPosition(position + startPosition);
								e
										.setElementLength((int) (buffer
												.position() - position));
								UnmatchedElementContainer uec = recordUnmatchedChars(e, buffer
										.position()
										+ startPosition, unmatchedChars,
										elements, session,
										defaultElementFactory);
								unmatchedChars = uec.stringBuffer;
								if (null != uec.nextElement) {
									elements.add(e);
								}
								match = true;
								buffer.position(buffer.position() - 1);
								previousChar = buffer.get();
								break;
							} else {
								// remove start tokens that were read from the
								// buffer
								buffer.position(position);
								match = false;
							}
						}
					}
				}
				if (!match && defaultElementFactory.doAppend(c)) {
					unmatchedChars.append(c);
					previousChar = c;
				}
			}
			recordUnmatchedChars(null, buffer.position() + startPosition,
					unmatchedChars, elements, session, defaultElementFactory);

			ElementNormalizer.normalize(elements, session, true);
			return getParsingResult(elements, lineBreaks, session);
		} catch (ExecutionException e) {
			e.setParsingResult(getParsingResult(elements, lineBreaks, session));
			throw e;
		} catch (ParseException e) {
			e.setParsingResult(getParsingResult(elements, lineBreaks, session));
			throw e;
		}
	}

	private List loadLineBreaks(CharBuffer buffer) {
		List lineBreaks = new ArrayList();
		for (long i = 0; buffer.hasRemaining(); i++) {
			char c = buffer.get();
			if (c == '\n')
				lineBreaks.add(new Long(i + 1));
		}
		return lineBreaks;
	}

	private ParsingResult getParsingResult(List elements, List lineBreaks,
			ParsingSession parsingSession) {
		long[] lineBreakArr = new long[lineBreaks.size()];
		for (int i = 0; i < lineBreaks.size(); i++)
			lineBreakArr[i] = ((Long) lineBreaks.get(i)).longValue();
		return new ParsingResult(elements, lineBreakArr, parsingSession);
	}

	private UnmatchedElementContainer recordUnmatchedChars(Element nextElement, int position, StringBuffer sb,
			List elements, ParsingSession session, DefaultElementFactory factory)
			throws ParseException {
		// we've got an element match - record it
		if (sb.length() > 0) {
			// record any unmatched characters as an element
			DefaultElementContainer dec = factory.createDefaultElement(nextElement, sb.toString(), session,
					position - sb.length());
			elements.add(dec.defaultElement);
			return new UnmatchedElementContainer(new StringBuffer(), dec.nextElement); 
		} else
			return new UnmatchedElementContainer(sb, nextElement);
	}

	private boolean isMatch(CharBuffer cb, char[] tokens) {
		if (tokens.length == 1)
			return true;
		if (tokens.length > cb.length() + 1)
			return false;
		for (int i = 1; i < tokens.length; i++) {
			if (tokens[i] != cb.charAt(i - 1))
				return false;
		}
		return true;
	}

	private StartTokenEntry[] getStartTokens(Component[] components) {
		List l = new ArrayList();
		for (int i = 0; i < components.length; i++) {
			PatternMatcher[] patternMatchers = components[i]
					.getPatternMatchers();
			for (int j = 0; j < patternMatchers.length; j++) {
				if (null != patternMatchers[j].getStartToken()) {
					l.add(new StartTokenEntry(components[i],
							patternMatchers[j], patternMatchers[j]
									.getStartToken()));
				} else if (null != patternMatchers[j].getStartTokens()) {
					char[][] startTokens = patternMatchers[j].getStartTokens();
					for (int k = 0; k < startTokens.length; k++) {
						l.add(new StartTokenEntry(components[i],
								patternMatchers[j], startTokens[k]));
					}
				}
			}
		}
		return (StartTokenEntry[]) l.toArray(new StartTokenEntry[l.size()]);
	}

	private StartTokenEntry[] getStartTokens(PatternMatcher[] patternMatchers) {
		List l = new ArrayList();
		for (int j = 0; j < patternMatchers.length; j++) {
			if (null != patternMatchers[j].getStartToken()) {
				l.add(new StartTokenEntry(null, patternMatchers[j],
						patternMatchers[j].getStartToken()));
			} else if (null != patternMatchers[j].getStartTokens()) {
				char[][] startTokens = patternMatchers[j].getStartTokens();
				for (int k = 0; k < startTokens.length; k++) {
					l.add(new StartTokenEntry(null, patternMatchers[j],
							startTokens[k]));
				}
			}
		}
		return (StartTokenEntry[]) l.toArray(new StartTokenEntry[l.size()]);
	}

	private class StartTokenEntry {
		private char[] startToken;
		private PatternMatcher patternMatcher;

		public StartTokenEntry(Component component,
				PatternMatcher patternMatcher, char[] startToken) {
			this.patternMatcher = patternMatcher;
			this.startToken = startToken;
		}
	}

	private class UnmatchedElementContainer {
		public StringBuffer stringBuffer;
		public Element nextElement;
		public UnmatchedElementContainer (
				StringBuffer stringBuffer, Element nextElement) {
			this.stringBuffer = stringBuffer;
			this.nextElement = nextElement;
		}
	}
}
