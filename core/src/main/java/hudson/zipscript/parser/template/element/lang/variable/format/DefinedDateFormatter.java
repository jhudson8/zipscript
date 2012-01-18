/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.format;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DefinedDateFormatter implements Formatter {

	private String format;
	private Locale locale;
	private DateFormat formatter;
	private Map localeFormatters;
	private char separator = '_';

	public DefinedDateFormatter(String format, Locale locale) {
		this.format = format;
		this.locale = locale;
		this.formatter = getDateFormatter(format, locale);
	}

	public String format(Object object, ExtendedContext context)
			throws Exception {
		if (null == context.getLocale() || null == this.locale
				|| this.locale.equals(context.getLocale())) {
			return formatter.format((Date) object);
		} else {
			if (null == localeFormatters)
				localeFormatters = new HashMap(2);
			DateFormat formatter = (DateFormat) localeFormatters.get(context
					.getLocale());
			if (null == formatter)
				formatter = getDateFormatter(format, context.getLocale());
			localeFormatters.put(context.getLocale(), formatter);
			return formatter.format((Date) object);
		}
	}

	protected DateFormat getDateFormatter(String style, Locale locale) {
		String p1 = null;
		String p2 = null;
		if (style.indexOf(separator) > 0) {
			int index = style.indexOf(separator);
			p1 = style.substring(0, index);
			p2 = style.substring(index + 1, style.length());
		} else {
			p1 = style;
		}
		if (null == p2) {
			// assume date
			return DateFormat.getDateInstance(normalizeStyle(style), locale);
		} else {
			if (p1.length() == 1) {
				// only date or time format
				if (p1.equals("d")) {
					return DateFormat.getDateInstance(normalizeStyle(p2),
							locale);
				} else if (p1.equals("t")) {
					return DateFormat.getTimeInstance(normalizeStyle(p2),
							locale);
				}
				// we'll throw an error here
				normalizeStyle(p1);
				return null;
			} else {
				// date and time format supplied
				return DateFormat.getDateTimeInstance(normalizeStyle(p1),
						normalizeStyle(p2), locale);
			}
		}
	}

	private int normalizeStyle(String style) {
		if (style.equals("short"))
			return DateFormat.SHORT;
		else if (style.equals("medium"))
			return DateFormat.MEDIUM;
		else if (style.equals("long"))
			return DateFormat.LONG;
		else {
			throw new ExecutionException("Unknown date format '" + style + "'",
					null);
		}
	}
}
