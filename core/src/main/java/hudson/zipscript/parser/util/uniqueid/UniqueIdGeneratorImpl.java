/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.util.uniqueid;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.InitializationException;
import hudson.zipscript.parser.util.StringUtil;

import java.io.Writer;
import java.util.Map;

public class UniqueIdGeneratorImpl implements UniqueIdGenerator {

	private long uniqueId = Long.MIN_VALUE;

	public String toString(ExtendedContext context) {
		if (Long.MIN_VALUE == uniqueId) {
			uniqueId = generateStartId();
		} else {
			uniqueId = uniqueId + 1;
		}
		return "i" + Long.toString(uniqueId);
	}

	public void append(ExtendedContext context, Writer writer) {
		StringUtil.append(toString(context), writer);
	}

	public String toString() {
		return toString(null);
	}

	public void configure(Map properties) throws InitializationException {
	}

	private long generateStartId () {
		return (long) (Math.random() * 100000000);
	}
}