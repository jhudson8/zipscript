/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.plugin;

import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.context.Context;
import hudson.zipscript.parser.context.ExtendedContext;
import hudson.zipscript.parser.exception.InitializationException;
import hudson.zipscript.parser.template.element.component.Component;
import hudson.zipscript.parser.template.element.lang.variable.adapter.VariableAdapterFactory;

import java.util.Map;

public interface Plugin {

	/**
	 * Initialize this plug-in and optionally perform additional operations to
	 * the ZipEngine instance
	 * 
	 * @param zipEngine
	 *            the ZipEngine instance
	 * @param initParameters
	 *            the initialization parameters
	 */
	public void initialize(ZipEngine zipEngine, Map initParameters)
			throws InitializationException;

	/**
	 * Initialize a wrapped context that is about to be used for
	 * merging/evaluation
	 * 
	 * @param context
	 *            the context
	 */
	public void initialize(ExtendedContext context)
			throws InitializationException;

	/**
	 * Wrap an object which will be used for the context
	 * 
	 * @param object
	 *            the object to use as the context
	 * @return null if unable to wrap or a wrapped context
	 */
	public Context wrapContextObject(Object object);

	/**
	 * Return all additional components to extend known directives /
	 * functionality or null if no syntax is to be modified
	 */
	public Component[] getComponents();

	/**
	 * Return a variable adapter factory or null if N/A
	 */
	public VariableAdapterFactory getVariableAdapterFactory();
}
