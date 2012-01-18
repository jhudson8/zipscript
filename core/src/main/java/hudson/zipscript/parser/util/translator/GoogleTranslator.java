/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util.translator;

import hudson.zipscript.parser.exception.InitializationException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.util.NetUtil;
import hudson.zipscript.parser.util.PropertyUtil;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Not an exceptional implementation of the gooogle translator because there
 * isn't much error detection. This is essentially reverse engineered from the
 * google translator widget. Special thanks to Firefox and Firebug :)
 * 
 * @author Joe Hudson
 */
public class GoogleTranslator extends AbstractTranslator {

	public static final String DEFAULT_URL = "http://www.google.com/translate_a/t?client=ig";
	public static final String DEFAULT_TEXT_PARAM = "text";
	public static final String DEFAULT_FROM_PARAM = "sl";
	public static final String DEFAULT_TO_PARAM = "tl";
	public static final String DEFAULT_BASE_LOCALE = "en";

	private String translatorURL;
	private String textParam;
	private String fromParam;
	private String toParam;
	private String baseLocaleKey;
	private boolean startWithQM;

	public List translate(List elementsOrText, Locale to) throws Exception {

		// create text to translate
		int varIndex = 0;
		StringBuffer sb = new StringBuffer();
		List variableElements = new ArrayList();
		for (Iterator i = elementsOrText.iterator(); i.hasNext();) {
			Object obj = i.next();
			if (obj instanceof String) {
				sb.append(obj);
			} else {
				sb.append("[" + (varIndex++) + "]");
				variableElements.add(obj);
			}
		}

		// trim the text
		String text = sb.toString();
		StringBuffer lpad = new StringBuffer();
		StringBuffer rpad = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				lpad.append(text.charAt(i));
			} else
				break;
		}
		for (int i = text.length() - 1; i >= 0; i--) {
			if (Character.isWhitespace(text.charAt(i))) {
				rpad.append(text.charAt(i));
			} else
				break;
		}
		String trimmedText = text.trim();
		if (trimmedText.length() == 0)
			return null;

		// do the translation
		StringBuffer url = new StringBuffer();
		url.append(translatorURL);
		if (startWithQM)
			url.append("?");
		else
			url.append("&");
		url.append(textParam);
		url.append("=");
		url.append(URLEncoder.encode(trimmedText, "UTF-8").replaceAll("\\+",
				"%20"));
		url.append("&");
		url.append(fromParam);
		url.append("=");
		url.append(baseLocaleKey);
		url.append("&");
		url.append(toParam);
		url.append("=");
		url.append(getLocaleKey(to));
		String response = NetUtil.getResponseText(url.toString());
		if (null == response || response.equals(trimmedText)) {
			// no translation was required - or couldn't be done
			return null;
		} else {
			return convertToElements(lpad.toString() + response
					+ rpad.toString(), variableElements);
		}
	}

	protected String getElementToken(int i, Element element) {
		return "[" + i + "]";
	}

	public void configure(Map properties) throws InitializationException {
		this.translatorURL = PropertyUtil.getProperty("url", DEFAULT_URL,
				properties);
		this.textParam = PropertyUtil.getProperty("textParam",
				DEFAULT_TEXT_PARAM, properties);
		this.fromParam = PropertyUtil.getProperty("fromParam",
				DEFAULT_FROM_PARAM, properties);
		this.toParam = PropertyUtil.getProperty("toParam", DEFAULT_TO_PARAM,
				properties);
		this.baseLocaleKey = PropertyUtil.getProperty("baseLocale",
				DEFAULT_BASE_LOCALE, properties);
		this.startWithQM = PropertyUtil.getProperty("startWithQM", false,
				properties);
	}

	public String getBaseLocaleKey() {
		return baseLocaleKey;
	}

	protected String getLocaleKey(Locale locale) {
		String s = locale.getLanguage();
		if (null == s)
			return locale.toString();
		else
			return s;
	}
}
