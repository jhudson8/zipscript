/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.NonOutputElement;
import hudson.zipscript.parser.template.element.lang.TextElement;
import hudson.zipscript.parser.template.element.lang.WhitespaceElement;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringUtil {

	private static final char SEPARATOR = ';';
	private static final char ASSIGNMENT = '=';

	public static void append(char c, Writer writer) {
		try {
			writer.write(c);
		} catch (IOException e) {
			throw new ExecutionException(e.getMessage(), null, e);
		}
	}

	public static void append(String s, Writer writer) {
		try {
			for (int i = 0; i < s.length(); i++)
				writer.write(s.charAt(i));
		} catch (IOException e) {
			throw new ExecutionException(e.getMessage(), null, e);
		}
	}

	public static String firstLetterUpperCase(String s) {
		if (null == s || s.length() == 1)
			return s;
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toUpperCase(s.charAt(0)));
		for (int i = 1; i < s.length(); i++)
			sb.append(s.charAt(i));
		return sb.toString();
	}

	public static String firstLetterLowerCase(String s) {
		if (null == s || s.length() == 1)
			return s;
		StringBuffer sb = new StringBuffer();
		sb.append(Character.toLowerCase(s.charAt(0)));
		for (int i = 1; i < s.length(); i++)
			sb.append(s.charAt(i));
		return sb.toString();
	}

	public static String humpbackCase(String s) {
		StringBuffer sb = new StringBuffer();
		boolean doUpper = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			while (Character.isWhitespace(c) || c == '_' || c == '-') {
				doUpper = true;
				if (s.length() > i)
					c = s.charAt(++i);
				else
					return sb.toString();
			}
			if (Character.isLetterOrDigit(c)) {
				if (doUpper && sb.length() > 0)
					sb.append(Character.toUpperCase(c));
				else
					sb.append(Character.toLowerCase(c));
				doUpper = false;
			}
		}
		return sb.toString();
	}

	public static String trimLastEmptyLine(StringBuffer sb) {
		for (int i = sb.length() - 1; i >= 0; i--) {
			char c = sb.charAt(i);
			if (c == '\n') {
				if (i > 0 && sb.charAt(i - 1) == '\r')
					i--;
				String rtn = sb.substring(i, sb.length());
				sb.delete(i, sb.length());
				return rtn;
			} else if (c != ' ' && c != '\t')
				return null;
		}
		return null;
	}

	public static String trimFirstEmptyLine(CharBuffer reader) {
		for (int i = 0; reader.length() > i; i++) {
			char c = reader.charAt(i);
			if (c == '\r') {
				if (reader.length() > i && reader.charAt(i + 1) == '\n')
					i++;
				char[] carr = new char[i + 1];
				reader.position(reader.position() + i + 1);
				return new String(carr);
			} else if (c == '\n') {
				char[] carr = new char[i - 1];
				reader.position(reader.position() + i + 1);
				return new String(carr);
			} else if (c != ' ' && c != '\t') {
				return null;
			}
		}
		return null;
	}

	public static String trimLastEmptyLine(List elements) {
		if (null != elements && elements.size() > 0) {
			Element e = (Element) elements.get(elements.size() - 1);
			if (e instanceof TextElement) {
				return trimLastEmptyLine((TextElement) e);
			}
		}
		return null;
	}

	public static String trimLastEmptyLine(List elements, int index) {
		if (index == 0)
			return null;
		if (null != elements && elements.size() > 0) {
			Element e = (Element) elements.get(index - 1);
			if (e instanceof TextElement) {
				return trimLastEmptyLine((TextElement) e);
			}
		}
		return null;
	}

	public static String trimLastEmptyLine(TextElement element) {
		String text = element.getText();
		for (int i = text.length() - 1; i >= 0; i--) {
			char c = text.charAt(i);
			if (c == '\n') {
				if (i > 0 && text.charAt(i - 1) == '\r')
					i--;
				String rtn = text.substring(i, text.length());
				element.setText(text.substring(0, i));
				return rtn;
			} else if (c != ' ' && c != '\t') {
				return null;
			}
		}
		return null;
	}

	/**
	 * Trim the element list (but leave elements that do not affect output in)
	 * 
	 * @param children
	 * @return true if is empty and false if not
	 */
	public static boolean trim(List children) {
		if (null == children || children.size() == 0)
			return true;
		Element trimFirst = null;
		Element trimLast = null;
		boolean isEmpty = true;
		for (int i = 0; children.size() > i;) {
			Element e = (Element) children.get(i);
			if (e instanceof WhitespaceElement
					|| (e instanceof TextElement && ((TextElement) e).getText()
							.trim().length() == 0)) {
				children.remove(i);
			} else if (e instanceof NonOutputElement
					&& !((NonOutputElement) e).generatesOutput()) {
				i++;
			} else {
				trimFirst = e;
				isEmpty = false;
				break;
			}
		}
		for (int i = children.size() - 1; i >= 0;) {
			Element e = (Element) children.get(i);
			if (e instanceof WhitespaceElement
					|| (e instanceof TextElement && ((TextElement) e).getText()
							.trim().length() == 0)) {
				children.remove(i);
				i--;
			} else if (e instanceof NonOutputElement
					&& !((NonOutputElement) e).generatesOutput()) {
				i--;
			} else {
				trimLast = e;
				isEmpty = false;
				break;
			}
		}
		if (trimFirst instanceof TextElement) {
			String text = ((TextElement) trimFirst).getText();
			for (int i = 0; i < text.length(); i++) {
				if (!Character.isWhitespace(text.charAt(i))) {
					text = text.substring(i);
					((TextElement) trimFirst).setText(text);
					break;
				}
			}
		}
		if (trimLast instanceof TextElement) {
			String text = ((TextElement) trimLast).getText();
			for (int i = text.length() - 1; i >= 0; i--) {
				if (!Character.isWhitespace(text.charAt(i))) {
					text = text.substring(0, i + 1);
					((TextElement) trimLast).setText(text);
					break;
				}
			}
		}
		return isEmpty;
	}

	public static boolean isEscaped(StringBuffer sb) {
		int numSlashes = 0;
		for (int i = sb.length() - 1; i >= 0; i--) {
			if (sb.charAt(i) == '\\')
				numSlashes++;
			else
				break;
		}
		if (numSlashes == 0)
			return false;
		int remove = numSlashes / 2;
		boolean isEven = numSlashes % 2 == 0;
		if (!isEven)
			remove++;
		if (remove > 0)
			sb.delete(sb.length() - remove, sb.length());
		if (isEven)
			return false;
		else
			return true;
	}

	public static Map getProperties(String s) {
		StringBuffer key = new StringBuffer();
		StringBuffer value = new StringBuffer();
		StringBuffer currentBuffer = key;
		Map props = null;

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == ASSIGNMENT) {
				currentBuffer = value;
			} else if (c == SEPARATOR) {
				props = setProperty(key, value, props);
				key = new StringBuffer();
				value = new StringBuffer();
				currentBuffer = key;
			} else
				currentBuffer.append(c);
		}
		props = setProperty(key, value, props);
		return props;
	}

	private static Map setProperty(StringBuffer key, StringBuffer value, Map map) {
		String keyS = key.toString().trim();
		String valueS = value.toString().trim();
		if (keyS.length() > 0 && valueS.length() > 0) {
			if (null == map)
				map = new HashMap();
			map.put(keyS, valueS);
		}
		return map;
	}
}