/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.format;

import hudson.zipscript.parser.context.ExtendedContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomDateFormatter implements Formatter {

	private String format;
	private Locale locale;
	private SimpleDateFormat formatter;
	private Map localeFormatters;

	public CustomDateFormatter(String format, Locale locale) {
		this.format = format;
		this.formatter = new SimpleDateFormat(format, locale);
		this.locale = locale;
	}

	public String format(Object object, ExtendedContext context)
			throws Exception {
		if (null == context.getLocale() || null == this.locale
				|| this.locale.equals(context.getLocale())) {
			return formatter.format((Date) object);
		} else {
			if (null == localeFormatters)
				localeFormatters = new HashMap(2);
			SimpleDateFormat formatter = (SimpleDateFormat) localeFormatters
					.get(locale);
			if (null == formatter) {
				formatter = new SimpleDateFormat(format, locale);
				localeFormatters.put(locale, formatter);
			}
			return formatter.format((Date) object);
		}
	}
}
