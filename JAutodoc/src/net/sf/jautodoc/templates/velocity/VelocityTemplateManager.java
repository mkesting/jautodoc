/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import net.sf.jautodoc.templates.AbstractTemplateManager;
import net.sf.jautodoc.templates.ITemplateRegistry;
import net.sf.jautodoc.templates.MatchingElement;
import net.sf.jautodoc.templates.ValidationException;
import net.sf.jautodoc.templates.wrapper.DateWrapper;
import net.sf.jautodoc.templates.wrapper.JavaElementWrapper;
import net.sf.jautodoc.templates.wrapper.PropertyWrapper;
import net.sf.jautodoc.velocity.log.VelocityLogChuteProxy;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.eclipse.jdt.core.IJavaElement;

/**
 * Manager for Velocity templates.
 */
public class VelocityTemplateManager extends AbstractTemplateManager {
	public static final String KEY_ELEMENT 		= "e";
	public static final String KEY_PROPERTIES 	= "p";
	public static final String KEY_USER 		= "user";
	public static final String KEY_DATE 		= "date";
	public static final String KEY_TIME 		= "time";
	public static final String KEY_YEAR 		= "year";
	public static final String KEY_PROJECT 		= "project_name";
	public static final String KEY_PACKAGE 		= "package_name";
	public static final String KEY_FILE 		= "file_name";
	public static final String KEY_TYPE 		= "type_name";

	private VelocityContext velocityCtx;
	private ITemplateRegistry registry;


	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.AbstractTemplateManager#validateTemplate(java.lang.String)
	 */
	public void validateTemplate(String template) throws ValidationException, Exception {
		try {
			RuntimeSingleton.parse(new StringReader(template), "TemplateValidation");
		} catch (ParseException pe) {
			if (pe.currentToken != null && pe.currentToken.next != null) {
				throw new ValidationException(pe,
						pe.currentToken.next.beginLine - 1,
						pe.currentToken.next.beginColumn - 1);
			}
			else {
				throw new ValidationException(pe);
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.AbstractTemplateManager#evaluateTemplate(org.eclipse.jdt.core.IJavaElement, java.lang.String, java.lang.String)
	 */
	public String evaluateTemplate(IJavaElement javaElement, String template,
			String templateName, Map<String, String> properties) throws Exception {
		prepareVelocityContext(javaElement, properties);
		StringWriter writer = new StringWriter();

		Velocity.evaluate(velocityCtx, writer, templateName, template);
		return resolveEscapes(writer.toString());
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.AbstractTemplateManager#applyTemplate(net.sf.jautodoc.templates.MatchingElement)
	 */
	protected String applyTemplate(MatchingElement me, Map<String, String> properties) throws Exception {
		Template template = Velocity.getTemplate(me.getEntry().getName());

		prepareVelocityContext(me, properties);
		StringWriter writer = new StringWriter();

		template.merge(velocityCtx, writer);
		return resolveEscapes(writer.toString());
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.AbstractTemplateManager#evaluateTemplate(net.sf.jautodoc.templates.MatchingElement, java.lang.String)
	 */
	protected String evaluateTemplate(MatchingElement me, String template,
			Map<String, String> properties) throws Exception {
		prepareVelocityContext(me, properties);
		StringWriter writer = new StringWriter();

		Velocity.evaluate(velocityCtx, writer, "TemplateTest", template);
		return resolveEscapes(writer.toString());
	}

	/**
	 * Due to an bug in Velocity 1.5 escaped references (\$)
	 * are not resolved, so we have to do it here.
	 *
	 * @param string the evaluated template text
	 *
	 * @return the resolved text
	 */
	private String resolveEscapes(String string) {
		return string.replaceAll("\\\\\\$", "\\$");
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.AbstractTemplateManager#onInit()
	 */
	protected void onInit() throws Exception {
		initVelocity();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.AbstractTemplateManager#getRegistry()
	 */
	protected ITemplateRegistry getRegistry() {
		if (registry == null) {
			registry = new VelocityTemplateRegistry();
		}
		return registry;
	}

	private void prepareVelocityContext(MatchingElement me, Map<String, String> properties) {
		velocityCtx.put(KEY_ELEMENT, me);
		velocityCtx.put(KEY_PROJECT, new JavaElementWrapper(me, JavaElementWrapper.PROJECT));
		velocityCtx.put(KEY_PACKAGE, new JavaElementWrapper(me, JavaElementWrapper.PACKAGE));
		velocityCtx.put(KEY_FILE, 	 new JavaElementWrapper(me, JavaElementWrapper.FILE));
		velocityCtx.put(KEY_TYPE,	 new JavaElementWrapper(me, JavaElementWrapper.TYPE));

		prepareProperties(properties);
	}

	private void prepareVelocityContext(IJavaElement je, Map<String, String> properties) {
		velocityCtx.remove(KEY_ELEMENT); // no matching element
		velocityCtx.put(KEY_PROJECT, new JavaElementWrapper(je, JavaElementWrapper.PROJECT));
		velocityCtx.put(KEY_PACKAGE, new JavaElementWrapper(je, JavaElementWrapper.PACKAGE));
		velocityCtx.put(KEY_FILE, 	 new JavaElementWrapper(je, JavaElementWrapper.FILE));
		velocityCtx.put(KEY_TYPE,	 new JavaElementWrapper(je, JavaElementWrapper.TYPE));

		prepareProperties(properties);
	}

	private void prepareProperties(Map<String, String> properties) {
		Iterator<String> keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String)keys.next();
			velocityCtx.put(key, new PropertyWrapper(key, properties));
		}
	}

	private void initVelocity() throws Exception {
		if (velocityCtx != null) return;

		// avoid class loading conflict with m2e plugin in Eclipse Luna
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
		    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

		    VelocityLogChuteProxy.setLogChute(new VelocityLogger());

            final Properties velProps = new Properties();
            velProps.load(getClass().getResourceAsStream("velocity.properties"));

            Velocity.init(velProps);

            initVelocityContext();
		} finally {
		    Thread.currentThread().setContextClassLoader(cl);
		}
	}

	private void initVelocityContext() {
		velocityCtx = new VelocityContext();
		velocityCtx.put(KEY_PROPERTIES,	new PropertyWrapper());
		velocityCtx.put(KEY_USER, new PropertyWrapper("user.name", null));

		velocityCtx.put(KEY_DATE, new DateWrapper(DateWrapper.DATE));
		velocityCtx.put(KEY_TIME, new DateWrapper(DateWrapper.TIME));
		velocityCtx.put(KEY_YEAR, new DateWrapper(DateWrapper.YEAR));
	}
}
