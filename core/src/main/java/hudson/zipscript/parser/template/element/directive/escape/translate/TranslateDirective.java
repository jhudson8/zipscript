/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.escape.translate;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NestableElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.variable.VariableElement;

import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TranslateDirective extends NestableElement {

	private static final int START_TOKEN = 4545456;

	private Map translatedLocales = new HashMap();

	public TranslateDirective(String contents, int contentIndex,
			ParsingSession parsingSession) throws ParseException {
	}

	public void validate(ParsingSession session) throws ParseException {
		// we can only have text and variable elements
		super.validate(session);
		if (null != getChildren()) {
			for (Iterator i = getChildren().iterator(); i.hasNext();) {
				Element e = (Element) i.next();
				if (e instanceof VariableElement || e instanceof TextElement) {
					// we're ok here
				} else {
					throw new ParseException(this,
							"Only text and interpolations are allowed in a translate directive");
				}
			}
		}
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		String localeKey = null;
		List elements = getChildren();
		if (null != context.getLocale())
			localeKey = getLocaleKey(context.getLocale());
		if (!localeKey.equals(context.getResourceContainer().getTranslator()
				.getBaseLocaleKey())) {
			// we have to translate
			Object maybeElements = translatedLocales.get(context.getLocale());
			if (null != maybeElements) {
				if (!maybeElements.equals(Boolean.FALSE))
					elements = (List) maybeElements;

			} else {
				// we haven't already translated yet
				elements = doTranslation(context, localeKey);
				if (null != elements)
					translatedLocales.put(localeKey, elements);
				else
					translatedLocales.put(localeKey, Boolean.FALSE);
			}
		}
		appendElements(elements, context, sw);
	}

	protected List doTranslation(ExtendedContext context, String localeKey) {
		if (null != getChildren()) {
			try {
				List l = new ArrayList();
				for (Iterator i = getChildren().iterator(); i.hasNext();) {
					Element e = (Element) i.next();
					if (e instanceof TextElement)
						l.add(((TextElement) e).getText());
					else
						l.add(e);
				}

				return context.getResourceContainer().getTranslator()
						.translate(l, context.getLocale());
			} catch (ExecutionException e) {
				e.setElement(this);
				throw e;
			} catch (Exception e) {
				throw new ExecutionException(e.getMessage(), this, e);
			}
		} else {
			return null;
		}
	}

	protected String getLocaleKey(Locale locale) {
		String s = locale.getLanguage();
		if (null == s)
			return locale.toString();
		else
			return s;
	}

	protected boolean isStartElement(Element e) {
		return (e instanceof TranslateDirective);
	}

	protected boolean isEndElement(Element e) {
		return (e instanceof EndTranslateDirective);
	}

	public String toString() {
		return "[#translate]";
	}
}