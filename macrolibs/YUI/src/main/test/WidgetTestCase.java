import hudson.zipscript.ZipEngine;
import hudson.zipscript.parser.Constants;
import hudson.zipscript.parser.exception.ExecutionException;
import hudson.zipscript.parser.exception.ParseException;
import hudson.zipscript.parser.util.IOUtil;
import hudson.zipscript.template.Template;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Person;

import junit.framework.TestCase;


public class WidgetTestCase extends TestCase {

	public void testDataTableWidget () throws Exception {
		Map context = new HashMap();
		List l = new ArrayList();
		l.add(new Person("Bill", "Cosby", "12/03/95", 8, new BigDecimal(1429978.76)));
		l.add(new Person("Bill", "Clinton", "12/03/89", 4, new BigDecimal(2875635.21)));
		l.add(new Person("Bill", "Bixby", "3/29/75", 3, new BigDecimal(192879.78)));
		l.add(new Person("Clark", "Kent", "5/11/82", 1, new BigDecimal(43987.19)));
		context.put("people", l);
		String mergeTemplate = "dataTable.zs";
		String resultFile = "/dataTable_result.html";
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testTabWidget () throws Exception {
		Map context = new HashMap();
		String mergeTemplate = "tab.zs";
		String resultFile = "/tab_result.html";
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testTreeWidget () throws Exception {
		Map context = new HashMap();
		String mergeTemplate = "tree.zs";
		String resultFile = "/tree_result.html";
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testAutoCompleteWidget () throws Exception {
		Map context = new HashMap();
		String mergeTemplate = "autoComplete.zs";
		String resultFile = "/autoComplete_result.html";
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testAccordionWidget () throws Exception {
		Map context = new HashMap();
		String mergeTemplate = "accordion.zs";
		String resultFile = "/accordion_result.html";
		evalResult(mergeTemplate, resultFile, context);
	}

	public void testAllWidgets () throws Exception {
		Map context = new HashMap();
		List l = new ArrayList();
		l.add(new Person("Bill", "Cosby", "12/03/95", 8, new BigDecimal(1429978.76)));
		l.add(new Person("Bill", "Clinton", "12/03/89", 4, new BigDecimal(2875635.21)));
		l.add(new Person("Bill", "Bixby", "3/29/75", 3, new BigDecimal(192879.78)));
		l.add(new Person("Clark", "Kent", "5/11/82", 1, new BigDecimal(43987.19)));
		context.put("people", l);
		String mergeTemplate = "allWidgets.zs";
		String resultFile = "/allWidgets_result.html";

		evalResult(mergeTemplate, resultFile, context);

		Template template = engine.getTemplate(mergeTemplate);
		long currentMilis = System.currentTimeMillis();
		int numRuns = 1000;
		for (int i=0; i<numRuns; i++) {
			template.merge(context);
		}
		long diff = System.currentTimeMillis() - currentMilis;
		float currentSeconds =  ((float) diff / (float) 1000);
		System.out.println(numRuns + " merges in " + currentSeconds + " seconds");
		assertTrue("Performance test failed!", (currentSeconds < 2));
	}

	private static ZipEngine engine = null;
	static {
		try {
			engine = ZipEngine.createInstance();
			engine.addMacroLibrary("data", "data.zsm");
			engine.addMacroLibrary("tab", "tab.zsm");
			engine.addMacroLibrary("tree", "tree.zsm");
			engine.addMacroLibrary("input", "input.zsm");
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Template layoutTemplate;

	private void evalResult (String mergeTemplate, String resultFile, Map context)
	throws ParseException, ExecutionException, IOException {
		Template bodyTemplate = engine.getTemplate(mergeTemplate);
		String result = mergeWithLayout(bodyTemplate, context);
		String expectedResult = IOUtil.toString(getClass().getResourceAsStream(resultFile));
		assertEquals(expectedResult, result);
	}

	private String mergeWithLayout (Template template, Map context)
	throws ParseException, ExecutionException, IOException {
		context.put("yuiIncludePrefix", "../yui_resources/build");
		context.put("cssIncludes", new LinkedHashMap(3));
		context.put("scriptIncludes", new LinkedHashMap(3));
		String body = merge(template, context);
		if (null == layoutTemplate)
			layoutTemplate = engine.getTemplate("layout.zs");
		context.put("screen_placeholder", body);
		return layoutTemplate.merge(context);
	}

	private String merge (Template template, Map context)
	throws ParseException, ExecutionException, IOException {
		return template.merge(context);
	}
}
