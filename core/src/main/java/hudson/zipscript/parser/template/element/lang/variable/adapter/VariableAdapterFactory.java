/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.lang.variable.adapter;

import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.template.data.ParsingSession;
import hudson.zipscript.parser.template.element.Element;
import hudson.zipscript.parser.template.element.lang.variable.format.Formatter;
import hudson.zipscript.parser.template.element.lang.variable.special.SpecialMethod;

public interface VariableAdapterFactory {

	public HashAdapter getHashAdapter(Object map);

	public SequenceAdapter getSequenceAdapter(Object sequence);

	public ObjectAdapter getObjectAdapter(Object object);

	public SpecialMethod getSpecialMethod(String name, Element[] parameters,
			ParsingSession session, Element element);

	public Formatter getFormatter (String format, String formatFunction,
			Object source, ExtendedContext context);

	public SpecialMethod getStringEscapingStringMethod(String method,
			ParsingSession session);

	public String getDefaultGetterMethod(Object obj);

	public String[] getReservedContextAttributes();
}
