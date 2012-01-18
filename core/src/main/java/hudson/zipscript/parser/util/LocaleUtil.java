/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class LocaleUtil {

	public static char getDecimalSeparator(Locale locale) {
		return new DecimalFormatSymbols(locale).getDecimalSeparator();
	}
}
