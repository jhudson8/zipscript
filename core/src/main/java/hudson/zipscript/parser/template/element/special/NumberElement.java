/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.special;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementIndex;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.AbstractElement;
import hudson.zipscript.parser.template.element.lang.DotElement;
import hudson.zipscript.parser.util.LocaleUtil;
import hudson.zipscript.parser.util.StringUtil;

import java.io.Writer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class NumberElement extends AbstractElement implements SpecialElement {

	private int type;
	private Object number;
	private String tokenValue;

	public static char TYPE_SHORT = 's';
	public static char TYPE_INTEGER = 'i';
	public static char TYPE_LONG = 'l';
	public static char TYPE_FLOAT = 'f';
	public static char TYPE_DOUBLE = 'd';
	public static char TYPE_BIG_DECIMAL = 'b';

	public static char[] TYPES = new char[] { TYPE_SHORT, TYPE_INTEGER,
			TYPE_LONG, TYPE_FLOAT, TYPE_DOUBLE, TYPE_BIG_DECIMAL };

	public NumberElement(String numberPart, Locale locale)
			throws ParseException {
		this(numberPart, Character.MIN_VALUE, locale);
		this.tokenValue = numberPart;
	}

	public NumberElement(String numberPart, char type, Locale locale)
			throws ParseException {
		if (type == Character.MIN_VALUE) {
			type = determineType(numberPart, locale);
			this.tokenValue = numberPart;
		} else {
			this.tokenValue = numberPart + type;
		}
		if (type == TYPE_SHORT) {
			number = new Short(numberPart);
		} else if (type == TYPE_INTEGER) {
			number = new Integer(numberPart);
		} else if (type == TYPE_LONG) {
			number = new Long(numberPart);
		} else if (type == TYPE_FLOAT) {
			number = new Float(numberPart);
		} else if (type == TYPE_DOUBLE) {
			number = new Double(numberPart);
		} else if (type == TYPE_BIG_DECIMAL) {
			number = new BigDecimal(numberPart);
		} else
			throw new ParseException(this, "Invalid number identifier '" + type
					+ "'");
	}

	public static char determineType(String numberPart, Locale locale) {
		char c = LocaleUtil.getDecimalSeparator(locale);
		for (int i = 0; i < numberPart.length(); i++)
			if (numberPart.charAt(i) == c)
				return TYPE_FLOAT;
		return TYPE_INTEGER;
	}

	public int getType() {
		return type;
	}

	public ElementIndex normalize(int index, List elementList,
			ParsingSession session) throws ParseException {
		if (elementList.size() > index) {
			if (elementList.get(index) instanceof DotElement) {
				elementList.remove(index);
				if (elementList.get(index) instanceof NumberElement) {
					// we've got a decimal value
					NumberElement e = (NumberElement) elementList.remove(index);
					this.tokenValue = this.tokenValue + '.' + e.getTokenValue();
					this.type = e.type;
				}
			}
		}
		return null;
	}

	public void merge(ExtendedContext context, Writer sw) {
		StringUtil.append(number.toString(), sw);
	}

	public boolean booleanValue(ExtendedContext context)
			throws ExecutionException {
		throw new ExecutionException("numbers can not be evaluated as boolean",
				this);
	}

	public Object objectValue(ExtendedContext context) {
		return number;
	}

	public String toString() {
		return number.toString();
	}

	public String getTokenValue() {
		return tokenValue;
	}

	public List getChildren() {
		return null;
	}
}