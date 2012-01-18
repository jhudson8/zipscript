/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.data;

public class LinePosition {

	public int line;
	public int position;
	public long absolutePosition;

	public LinePosition(int line, int position, long absolutePosition) {
		this.line = line;
		this.position = position;
		this.absolutePosition = absolutePosition;
	}
}
