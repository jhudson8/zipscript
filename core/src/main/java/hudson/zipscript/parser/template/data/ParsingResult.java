/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.data;

import java.util.List;

public class ParsingResult {

	private List elements;
	private long[] lineBreaks;
	private ParsingSession parsingSession;

	public ParsingResult(List elements, long[] lineBreaks,
			ParsingSession parsingSession) {
		this.elements = elements;
		this.lineBreaks = lineBreaks;
		this.parsingSession = parsingSession;
	}

	public List getElements() {
		return elements;
	}

	public long[] getLineBreaks() {
		return lineBreaks;
	}

	public LinePosition getLinePosition(long position) {
		int lineCount = 1;
		long lineBreakPosition = 0;
		for (int i = 0; lineBreaks.length > i; i++) {
			if (lineBreaks[i] <= position)
				lineBreakPosition = lineBreaks[i];
			else
				break;
			lineCount++;
		}
		int lbpos = (int) (position - lineBreakPosition);
		if (lbpos > 0)
			lbpos--;
		return new LinePosition(lineCount, lbpos, position);
	}

	public ParsingSession getParsingSession() {
		return parsingSession;
	}
}