/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser.template.element.directive.macrodir;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.context.MacroInstanceEntityContext;
import hudson.zipscript.parser.context.ZSContextRequiredGetter;
import hudson.zipscript.parser.template.element.lang.variable.adapter.RetrievalContext;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MacroInstanceEntity implements ZSContextRequiredGetter {

	private MacroOrientedElement macroInstance;
	private MacroInstanceEntityContext context;
	private boolean initialized = false;

	public MacroInstanceEntity(MacroOrientedElement macroInstance,
			ExtendedContext context, Map additionalContextEntries) {
		this.macroInstance = macroInstance;

		Map clonedEntries = new HashMap();
		// save any looping context values
		if (null != additionalContextEntries)
			for (Iterator i = additionalContextEntries.entrySet().iterator(); i
					.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				clonedEntries.put(entry.getKey(), entry.getValue());
			}
		this.context = new MacroInstanceEntityContext(macroInstance, context,
				clonedEntries, macroInstance.getAttributes(), macroInstance
						.getMacroDefinitionAttributes(context));
	}

	public MacroOrientedElement getMacroInstance() {
		return macroInstance;
	}

	public Object get(String key, RetrievalContext retrievalContext,
			String contextHint, ExtendedContext context) {
		if (key.equals(Constants.BODY)) {
			if (macroInstance.isBodyEmpty()) {
				return null;
			} else {
				initialize(context, true);
				return macroInstance.getNestedContent(this.context);
			}
		} else if (key.equals(Constants.HEADER)) {
			initialize(context, false);
			return macroInstance.getHeader();
		} else if (key.equals(Constants.FOOTER)) {
			initialize(context, false);
			return macroInstance.getFooter();
		} else {
			initialize(context, false);
			return this.context.get(key, retrievalContext, contextHint);
		}
	}

	public String toString() {
		if (null != macroInstance)
			return macroInstance.toString();
		else
			return super.toString();
	}

	public void put(String key, Object value) {
		if (macroInstance.isInTemplate()) {
			this.context.getTemplateContext().put(key, value);
		}
		else {
			this.context.put(key, value, true);
		}
	}

	public MacroInstanceEntityContext getContext() {
		return context;
	}

	private void initialize(ExtendedContext context, boolean force) {
		if (!initialized || force) {
			this.context.setPostMacroContext(context);
			initialized = true;
		}
	}
}