/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.escape.translate;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.template.element.directive.NestableDirective;

import java.io.Writer;

public class EndTranslateDirective extends NestableDirective {

	public EndTranslateDirective(String contents) {
	}

	public String toString() {
		return "[/#translate]";
	}

	public void merge(ExtendedContext context, Writer sw)
			throws ExecutionException {
		throw new ExecutionException("Invalid translate directive", this);
	}
}
