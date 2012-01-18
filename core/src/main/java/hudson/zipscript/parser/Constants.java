/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript.parser;

import hudson.zipscript.parser.template.element.PatternMatcher;
import hudson.zipscript.parser.template.element.comparator.ComparatorPatternMatcher;
import hudson.zipscript.parser.template.element.comparator.InComparatorPatternMatcher;
import hudson.zipscript.parser.template.element.comparator.logic.AndLogicPatternMatcher;
import hudson.zipscript.parser.template.element.comparator.logic.OrLogicPatternMatcher;
import hudson.zipscript.parser.template.element.comparator.math.MathPatternMatcher;
import hudson.zipscript.parser.template.element.group.GroupPatternMatcher;
import hudson.zipscript.parser.template.element.group.ListPatternMatcher;
import hudson.zipscript.parser.template.element.group.MapPatternMatcher;
import hudson.zipscript.parser.template.element.lang.AssignmentPatternMatcher;
import hudson.zipscript.parser.template.element.lang.CommaPatternMatcher;
import hudson.zipscript.parser.template.element.lang.DotPatternMatcher;
import hudson.zipscript.parser.template.element.lang.WhitespacePatternMatcher;
import hudson.zipscript.parser.template.element.lang.variable.VarDefaultElementPatternMatcher;
import hudson.zipscript.parser.template.element.lang.variable.VariablePatternMatcher;
import hudson.zipscript.parser.template.element.lang.variable.format.VarFormattingElementPatternMatcher;
import hudson.zipscript.parser.template.element.lang.variable.special.VarSpecialElementPatternMatcher;
import hudson.zipscript.parser.template.element.special.BooleanPatternMatcher;
import hudson.zipscript.parser.template.element.special.NullPatternMatcher;
import hudson.zipscript.parser.template.element.special.NumericPatternMatcher;
import hudson.zipscript.parser.template.element.special.StringPatternMatcher;
import hudson.zipscript.resource.ClasspathResourceLoader;
import hudson.zipscript.resource.FileResourceLoader;
import hudson.zipscript.resource.StringResourceLoader;
import hudson.zipscript.resource.URLResourceLoader;
import hudson.zipscript.resource.WebInfResourceLoader;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	// options
	public static final String SUPPRESS_NULL_ERRORS = "suppressNullErrors";
	public static final String TRIM_MACRO_BODY = "trimMacroBody";

	// context parameters
	public static final String BODY = "body";
	public static final String HEADER = "header";
	public static final String FOOTER = "footer";
	public static final String THIS = "this";
	public static final String SUPER = "super";

	public static final String NOW = "Now";
	public static final String VARS = "Vars";
	public static final String GLOBAL = "Global";
	public static final String UNIQUE_ID = "UniqueId";
	public static final String MATH = "Math";
	public static final String RESOURCE = "Resource";

	public static final String TEMPLATE_RESOURCE_LOADER_CLASS = "templateResourceLoader.class";
	public static final String TEMPLATE_RESOURCE_LOADER_TYPE = "templateResourceLoader.type";
	public static final String MACROLIB_RESOURCE_LOADER_CLASS = "macroLibResourceLoader.class";
	public static final String MACROLIB_RESOURCE_LOADER_TYPE = "macroLibResourceLoader.type";
	public static final String EVAL_RESOURCE_LOADER_CLASS = "evalResourceLoader.class";
	public static final String EVAL_RESOURCE_LOADER_TYPE = "includeResourceLoader.type";
	public static final String INCLUDE_RESOURCE_LOADER_CLASS = "includeResourceLoader.class";
	public static final String INCLUDE_RESOURCE_LOADER_TYPE = "includeResourceLoader.type";
	public static final String UNIQUE_ID_GENERATOR_CLASS = "uniqueIdGenerator.class";
	public static final String VARIABLE_ADAPTER_FACTORY_CLASS = "variableAdapterFactory.class";

	public static final String MACROLIB_RESOURCE_LOADER_PARAMETER = "macroLibResourceLoaderParameter";
	public static final String INCLUDE_RESOURCE_LOADER_PARAMETER = "includeResourceLoaderParameter";

	public static final String REFRESH_TEMPLATES = "refreshTemplates";

	public static final char NAMESPACE_SEPARATOR = ':';

	public static final Map RESOURCE_LOADER_TYPES = new HashMap();
	static {
		RESOURCE_LOADER_TYPES.put("classpath", ClasspathResourceLoader.class);
		RESOURCE_LOADER_TYPES.put("file", FileResourceLoader.class);
		RESOURCE_LOADER_TYPES.put("url", URLResourceLoader.class);
		RESOURCE_LOADER_TYPES.put("URL", URLResourceLoader.class);
		RESOURCE_LOADER_TYPES.put("string", StringResourceLoader.class);
		RESOURCE_LOADER_TYPES.put("web-inf", WebInfResourceLoader.class);
		RESOURCE_LOADER_TYPES.put("WEB-INF", WebInfResourceLoader.class);
	}

	public static final PatternMatcher[] VARIABLE_MATCHERS = new PatternMatcher[] {
			new VariablePatternMatcher(), new ListPatternMatcher(),
			new NumericPatternMatcher(), new CommaPatternMatcher(),
			new StringPatternMatcher(), new InComparatorPatternMatcher(),
			new AndLogicPatternMatcher(), new OrLogicPatternMatcher(),
			new BooleanPatternMatcher(), new ComparatorPatternMatcher(),
			new MathPatternMatcher(), new NullPatternMatcher(),
			new GroupPatternMatcher(), new WhitespacePatternMatcher(),
			new MapPatternMatcher(), new DotPatternMatcher(),
			new AssignmentPatternMatcher(),
			new VarDefaultElementPatternMatcher(),
			new VarSpecialElementPatternMatcher(),
			new VarFormattingElementPatternMatcher() };
}
