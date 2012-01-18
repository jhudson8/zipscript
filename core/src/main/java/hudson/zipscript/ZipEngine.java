/*
 * Copyright (c) 2008 Joe Hudson.  All rights reserved.
 * License: LGPL <http://www.gnu.org/licenses/lgpl.html>
 */

package hudson.zipscript;

import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.ExpressionParser;
import hudson.zipscript.parser.exception.InitializationException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.template.data.ElementParsingSession;
import hudson.zipscript.parser.template.data.ParseParameters;
import hudson.zipscript.parser.template.element.comment.CommentComponent;
import hudson.zipscript.parser.template.element.component.Component;
import hudson.zipscript.parser.template.element.directive.breakdir.BreakComponent;
import hudson.zipscript.parser.template.element.directive.calldir.CallComponent;
import hudson.zipscript.parser.template.element.directive.continuedir.ContinueComponent;
import hudson.zipscript.parser.template.element.directive.escape.EscapeComponent;
import hudson.zipscript.parser.template.element.directive.escape.translate.TranslateComponent;
import hudson.zipscript.parser.template.element.directive.foreachdir.ForeachComponent;
import hudson.zipscript.parser.template.element.directive.ifdir.IfComponent;
import hudson.zipscript.parser.template.element.directive.importdir.ImportComponent;
import hudson.zipscript.parser.template.element.directive.includedir.IncludeComponent;
import hudson.zipscript.parser.template.element.directive.initialize.InitializeComponent;
import hudson.zipscript.parser.template.element.directive.macrodir.MacroComponent;
import hudson.zipscript.parser.template.element.directive.setdir.GlobalComponent;
import hudson.zipscript.parser.template.element.directive.setdir.SetComponent;
import hudson.zipscript.parser.template.element.directive.whiledir.WhileComponent;
import hudson.zipscript.parser.template.element.lang.variable.SpecialVariableDefaultEelementFactory;
import hudson.zipscript.parser.template.element.lang.variable.VariableComponent;
import hudson.zipscript.parser.template.element.lang.variable.adapter.MultipleVariableAdapterFactory;
import hudson.zipscript.parser.template.element.lang.variable.adapter.StandardVariableAdapterFactory;
import hudson.zipscript.parser.template.element.lang.variable.adapter.VariableAdapterFactory;
import hudson.zipscript.parser.util.ClassUtil;
import hudson.zipscript.parser.util.IOUtil;
import hudson.zipscript.parser.util.ResourceUtil;
import hudson.zipscript.parser.util.TemplateResource;
import hudson.zipscript.plugin.Plugin;
import hudson.zipscript.resource.ClasspathResourceLoader;
import hudson.zipscript.resource.ResourceLoader;
import hudson.zipscript.resource.StringResourceLoader;
import hudson.zipscript.resource.macrolib.MacroManager;
import hudson.zipscript.template.Evaluator;
import hudson.zipscript.template.Template;
import hudson.zipscript.template.TemplateImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class used to retrieve templates and evaluators for the ZipScript
 * expression language. For default engine:
 * <code>ZipEngine.createInstance()...</code> For customized engine:
 * <code>ZipEngine.createInstance(Map properties)...</code>
 * <p>
 * The default behavior is to load template and macro resources from the
 * classpath and evaluate the string source directly that is passed to the
 * getEvaluator(String) method. Properties can be set to alter the behavior of
 * the resource locators and other functionality. Reference all available
 * properties <a href="http://www.zipscript.org/docs/current/bk02ch04.html">here</a>.
 * </p>
 * <p> // standard usage
 * 
 * <pre>
 * Properties props = new Properties();
 * // set properties
 * engine.init(props);
 * ZipEngine engine = ZipEngine.createInstance(props);
 * Map context = new HashMap();
 * // load the context
 * 
 * // performing template merging
 * Template t = engine.getTemplate(&quot;myResource.zs&quot;);
 * String mergeResults = t.merge(context);
 * 
 * // performing boolean evaluation
 * Evaluator e = engine.getEvaluator(&quot;foo &gt; bar&quot;);
 * boolean evalResult = e.booleanValue(context);
 * 
 * // object retrieval
 * Evaluator e = engine.getEvaluator(&quot;foo&quot;);
 * Object result = e.objectValue(context);
 * </pre>
 * 
 * </p>
 * 
 * @author Joe Hudson
 */
public class ZipEngine {

	private static final Component[] NON_OVERRIDEABLE_COMPONENTS = new Component[] {
			new CommentComponent(), new InitializeComponent(),
			new IncludeComponent(), new ImportComponent(),
			new EscapeComponent(), new BreakComponent(),
			new ContinueComponent(), new SetComponent(), new GlobalComponent(),
			new TranslateComponent() };
	private static final Component[] OVERRIDEABLE_COMPONENTS = new Component[] {
			new IfComponent(), new ForeachComponent(), new WhileComponent(),
			new MacroComponent(), new CallComponent(), new VariableComponent() };

	private ResourceContainer resourceContainer;
	private boolean doRefreshCheck;

	/**
	 * newInstance should be used
	 */
	private ZipEngine() {
	}

	/**
	 * Retrurn new instanceof ZipEngine
	 */
	public static ZipEngine createInstance() {
		return createInstance(null, null);
	}

	/**
	 * Retrurn new instanceof ZipEngine initialized with property map
	 */
	public static ZipEngine createInstance(Map properties) {
		return createInstance(properties, null);
	}

	/**
	 * Retrurn new instanceof ZipEngine initialized with property map and use
	 * the plugins
	 */
	public static ZipEngine createInstance(Map properties, Plugin[] plugins) {
		ZipEngine zipEngine = new ZipEngine();
		zipEngine.init(properties, plugins);
		return zipEngine;
	}

	/**
	 * Initialize with property map
	 * 
	 * @param properties
	 * @param plugins
	 *            engine plugins
	 */
	private void init(Map properties, Plugin[] plugins) {
		if (null == properties)
			properties = new HashMap();

		// defaults
		ResourceLoader templateResourceLoader = new ClasspathResourceLoader();
		ResourceLoader includeResourceLoader = null;
		ResourceLoader macroLibResourceLoader = null;
		ResourceLoader evalResourceLoader = new StringResourceLoader();

		List variableAdapterFactories = new ArrayList();
		VariableAdapterFactory variableAdapterFactory = new StandardVariableAdapterFactory();
		List components = new ArrayList();
		for (int i = 0; i < NON_OVERRIDEABLE_COMPONENTS.length; i++)
			components.add(NON_OVERRIDEABLE_COMPONENTS[i]);

		if (null != plugins) {
			// initialilze plugins
			for (int i = 0; i < plugins.length; i++) {
				plugins[i].initialize(this, properties);
				Component[] c = plugins[i].getComponents();
				if (null != c) {
					for (int j = 0; j < NON_OVERRIDEABLE_COMPONENTS.length; j++)
						components.add(c[j]);
				}
				VariableAdapterFactory vaf = plugins[i]
						.getVariableAdapterFactory();
				if (null != vaf)
					variableAdapterFactories.add(vaf);
			}
		}

		// get the default resource loader
		String s = (String) properties
				.get(Constants.TEMPLATE_RESOURCE_LOADER_CLASS);
		if (null == s)
			s = (String) properties
					.get(Constants.TEMPLATE_RESOURCE_LOADER_TYPE);
		if (null != s) {
			try {
				templateResourceLoader = (ResourceLoader) ClassUtil
						.loadResource("templateResourceLoader", properties,
								ResourceLoader.class,
								ClasspathResourceLoader.class,
								Constants.RESOURCE_LOADER_TYPES);
			} catch (ClassCastException e) {
				throw new InitializationException(
						"The resource loader '"
								+ s
								+ "' must extend hudson.zipscript.resource.ResourceLoader",
						e);
			}
		}
		s = (String) properties.get(Constants.MACROLIB_RESOURCE_LOADER_CLASS);
		if (null == s)
			s = (String) properties
					.get(Constants.MACROLIB_RESOURCE_LOADER_TYPE);
		if (null != s) {
			try {
				macroLibResourceLoader = (ResourceLoader) ClassUtil
						.loadResource("macroLibResourceLoader", properties,
								ResourceLoader.class, null,
								Constants.RESOURCE_LOADER_TYPES);
			} catch (ClassCastException e) {
				throw new InitializationException(
						"The resource loader '"
								+ s
								+ "' must extend hudson.zipscript.resource.ResourceLoader",
						e);
			}
		}
		s = (String) properties.get(Constants.EVAL_RESOURCE_LOADER_CLASS);
		if (null == s)
			s = (String) properties.get(Constants.EVAL_RESOURCE_LOADER_TYPE);
		if (null != s) {
			try {
				evalResourceLoader = (ResourceLoader) ClassUtil.loadResource(
						"evalResourceLoader", properties, ResourceLoader.class,
						StringResourceLoader.class,
						Constants.RESOURCE_LOADER_TYPES);
			} catch (ClassCastException e) {
				throw new InitializationException(
						"The resource loader '"
								+ s
								+ "' must extend hudson.zipscript.resource.ResourceLoader",
						e);
			}
		}
		s = (String) properties.get(Constants.INCLUDE_RESOURCE_LOADER_CLASS);
		if (null == s)
			s = (String) properties.get(Constants.INCLUDE_RESOURCE_LOADER_TYPE);
		if (null != s) {
			try {
				includeResourceLoader = (ResourceLoader) ClassUtil
						.loadResource("includeResourceLoader", properties,
								ResourceLoader.class, null,
								Constants.RESOURCE_LOADER_TYPES);
			} catch (ClassCastException e) {
				throw new InitializationException(
						"The resource loader '"
								+ s
								+ "' must extend hudson.zipscript.resource.ResourceLoader",
						e);
			}
		}

		s = (String) properties.get(Constants.VARIABLE_ADAPTER_FACTORY_CLASS);
		if (null != s) {
			VariableAdapterFactory vaf = (VariableAdapterFactory) ClassUtil
					.loadResource("variableAdapterFactory", properties,
							VariableAdapterFactory.class, null, null);
			VariableAdapterFactory[] arr = new VariableAdapterFactory[2];
			arr[0] = variableAdapterFactory;
			arr[1] = vaf;
			variableAdapterFactory = new MultipleVariableAdapterFactory(arr);
		}

		for (int i = 0; i < OVERRIDEABLE_COMPONENTS.length; i++)
			components.add(OVERRIDEABLE_COMPONENTS[i]);

		if (variableAdapterFactories.size() > 0) {
			variableAdapterFactories.add(variableAdapterFactory);
			variableAdapterFactory = new MultipleVariableAdapterFactory(
					(VariableAdapterFactory[]) variableAdapterFactories
							.toArray(new VariableAdapterFactory[variableAdapterFactories
									.size()]));
		}

		MacroManager macroManager = new MacroManager();
		macroManager.setResourceLoaderParameter(properties
				.get(Constants.INCLUDE_RESOURCE_LOADER_PARAMETER));

		resourceContainer = new ResourceContainer(this, plugins, macroManager,
				variableAdapterFactory, templateResourceLoader,
				includeResourceLoader, macroLibResourceLoader,
				evalResourceLoader, (Component[]) components
						.toArray(new Component[components.size()]), properties);

		// property accessor
		ParseParameters pp = new ParseParameters(resourceContainer, false,
				false);
		this.doRefreshCheck = pp.getPropertyAsBoolean(
				Constants.REFRESH_TEMPLATES, true);
	}

	/**
	 * Add a reference to a macro library
	 * 
	 * @param namespace
	 *            the namespace reference for macros within this resource
	 * @param resourcePath
	 *            the resource path for the macro library resource
	 * @throws ParseException
	 */
	public void addMacroLibrary(String namespace, String resourcePath)
			throws ParseException {
		resourceContainer.getMacroManager().addMacroLibrary(namespace,
				resourcePath, resourceContainer.getMacroLibResourceLoader(),
				resourceContainer.getVariableAdapterFactory());
	}

	/**
	 * Add a reference to a macro library file (or add a macro lib directory).
	 * All macro lib resource must end with '.zsm'
	 * 
	 * @param file
	 *            the file or directory name
	 * @throws FileNotFoundException
	 * @throws ParseException
	 */
	public void addMacroLibrary(File file) throws FileNotFoundException,
			ParseException {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				addMacroLibrary(files[i]);
			}
		} else if (file.getName().endsWith(".zsm")) {
			String namespace = file.getName().substring(0,
					file.getName().length() - 4);
			String contents = IOUtil.toString(new FileInputStream(file));
			resourceContainer.getMacroManager().addMacroLibrary(namespace,
					contents, StringResourceLoader.INSTANCE,
					resourceContainer.getVariableAdapterFactory());
		}
	}

	// internal cache
	private Map resourceMap = new HashMap();

	/**
	 * Return a template used for merging static data with an object model. This
	 * template will be cached as long as the same instance of the ZipEngine is
	 * used for template retrieval. The template resource loader will be used to
	 * load the template. This can be modified using the
	 * 'templateResourceLoader.class' init property.
	 * 
	 * @param source
	 *            the template resource source path & name
	 * @return the Template
	 * @throws ParseException
	 */
	public Template getTemplate(String source) throws ParseException {
		return getTemplate(source, null);
	}

	/**
	 * Return a template used for merging static data with an object model. This
	 * template will be cached as long as the same instance of the ZipEngine is
	 * used for template retrieval. The template resource loader will be used to
	 * load the template. This can be modified using the
	 * 'templateResourceLoader.class' init property.
	 * 
	 * @param source
	 *            the template resource source path & name
	 * @param properties
	 *            any additional input that might be used by the resource loader
	 * @return the Template
	 * @throws ParseException
	 */
	public Template getTemplate(String source, Object parameter)
			throws ParseException {
		try {
			TemplateResource tr = (TemplateResource) resourceMap.get(source);
			if (null == tr) {
				tr = ResourceUtil.loadTemplate(source, parameter,
						new ParseParameters(resourceContainer, false, false),
						resourceContainer);
				resourceMap.put(source, tr);
			} else if (doRefreshCheck && tr.resource.hasBeenModified()) {
				// reload the resource
				tr = ResourceUtil.loadTemplate(new ParseParameters(
						resourceContainer, false, false), resourceContainer,
						tr.resource);
				resourceMap.put(source, tr);
			}

			return tr.template;
		} catch (ParseException e) {
			e.setResource(source);
			throw e;
		}
	}

	/**
	 * Return an expression evaluator. The expression resource loader will be
	 * used to load the template. This can be modified using the
	 * 'evalResourceLoader.class' init property.
	 * 
	 * @param contents
	 *            the expression or resource reference (depending on resource
	 *            loader)
	 * @return the evaluator
	 * @throws ParseException
	 */
	public Evaluator getEvaluator(String contents) throws ParseException {
		try {
			ElementParsingSession eps = ExpressionParser.getInstance()
					.parseToElement(contents, Constants.VARIABLE_MATCHERS,
							SpecialVariableDefaultEelementFactory.INSTANCE, 0,
							resourceContainer);
			TemplateImpl evaluator = new TemplateImpl(eps.element,
					eps.parsingSession);
			evaluator.setResourceContainer(resourceContainer);
			return evaluator;
		} catch (ParseException e) {
			e.setResource(contents);
			throw e;
		}
	}

	/**
	 * Set the resource loader to retrieve templates
	 * 
	 * @param resourceLoader
	 */
	public void setTemplateResourceLoader(ResourceLoader resourceLoader) {
		resourceContainer.setTemplateResourceLoader(resourceLoader);
	}

	/**
	 * Set the resource loader to retrieve include templates
	 * 
	 * @param resourceLoader
	 */
	public void setIncludeResourceLoader(ResourceLoader resourceLoader) {
		resourceContainer.setIncludeResourceLoader(resourceLoader);
	}

	/**
	 * Set the resource loader to retrieve include templates
	 * 
	 * @param resourceLoader
	 */
	public void setMacroLibResourceLoader(ResourceLoader resourceLoader) {
		resourceContainer.setMacroLibResourceLoader(resourceLoader);
	}

	/**
	 * Set the resource loader to retrieve evaluators
	 * 
	 * @param resourceLoader
	 */
	public void setEvalResourceLoader(ResourceLoader resourceLoader) {
		resourceContainer.setEvalResourceLoader(resourceLoader);
	}
}