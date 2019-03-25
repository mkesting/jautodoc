/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;


/**
 * Interface for template managers.
 */
public interface ITemplateManager {

	/**
	 * Initialization.
	 *
	 * @throws Exception thrown if an exception occured
	 */
	public void initialize() throws Exception;

	/**
	 * Get all templates.
	 *
	 * @return the templates
	 * @throws Exception thrown if an exception occured
	 */
	public TemplateSet getTemplates() throws Exception;

	/**
	 * Set new templates.
	 *
	 * @param templates the new templates
	 * @throws Exception thrown if an exception occured
	 */
	public void setTemplates(TemplateSet templates) throws Exception;

	/**
	 * Get type templates.
	 *
	 * @return the type templates
	 * @throws Exception thrown if an exception occured
	 */
	public List<TemplateEntry> getTypeTemplates() throws Exception;

	/**
	 * Get method templates.
	 *
	 * @return the method templates
	 * @throws Exception thrown if an exception occured
	 */
	public List<TemplateEntry> getMethodTemplates() throws Exception;

	/**
	 * Get field templates.
	 *
	 * @return the field templates
	 * @throws Exception thrown if an exception occured
	 */
	public List<TemplateEntry> getFieldTemplates() throws Exception;

	/**
	 * Get parameter templates.
	 *
	 * @return the parameter templates
	 * @throws Exception thrown if an exception occured
	 */
	public List<TemplateEntry> getParameterTemplates() throws Exception;

	/**
	 * Get exception templates.
	 *
	 * @return the exception templates
	 * @throws Exception thrown if an exception occured
	 */
	public List<TemplateEntry> getExceptionTemplates() throws Exception;

	/**
	 * Load templates.
	 *
	 * @throws Exception thrown if an exception occured
	 */
	public void loadTemplates() throws Exception;

	/**
	 * Load templates from file.
	 *
	 * @param file the file
	 * @throws Exception thrown if an exception occured
	 */
	public void loadTemplates(File file) throws Exception;

	/**
	 * Load default templates.
	 *
	 * @throws Exception thrown if an exception occured
	 */
	public void loadDefaultTemplates() throws Exception;

	/**
	 * Store templates.
	 *
	 * @throws Exception thrown if an exception occured
	 */
	public void storeTemplates() throws Exception;

	/**
	 * Store templates to file.
	 *
	 * @param file the file
	 * @throws Exception thrown if an exception occured
	 */
	public void storeTemplates(File file) throws Exception;

	/**
	 * Try to apply a template to the given member.
	 *
	 * @param member the member
	 * @param properties the properties to use
	 * @return the resulting string
	 * @throws Exception thrown if an exception occured
	 */
	public String applyTemplate(IMember member, Map<String, String> properties) throws Exception;

	/**
	 * Try to apply a template to the given parameter.
	 *
	 * @param member the related method or generic type
	 * @param type the parameter type
	 * @param name the parameter name
	 * @param properties the properties to use
	 * @return the resulting string
	 * @throws Exception thrown if an exception occured
	 */
	public String applyParameterTemplate(IMember member, String type,
			String name, Map<String, String> properties) throws Exception;

	/**
	 * Try to apply a template to the given exception.
	 *
	 * @param method the related method
	 * @param name the exception name
	 * @param properties the properties to use
	 * @return the resulting string
	 * @throws Exception thrown if an exception occured
	 */
	public String applyExceptionTemplate(IMethod method, String name, Map<String, String> properties)
			throws Exception;

	/**
	 * Evaluate the given template.
	 *
	 * @param matcher the matcher
	 * @param parentMatcher the parent matcher
	 * @param template the template
	 * @param entry the related template entry
	 * @param properties the properties to use
	 * @return the resulting string
	 * @throws Exception thrown if an exception occured
	 */
	public String evaluateTemplate(Matcher matcher, Matcher parentMatcher,
			String template, TemplateEntry entry, Map<String, String> properties) throws Exception;

	/**
	 * Evaluate the given template.
	 *
	 * @param javaElement the java element
	 * @param template the template
	 * @param templateName the template name
	 * @param properties the properties to use
	 * @return the resulting string
	 * @throws Exception thrown if an exception occured
	 */
	public String evaluateTemplate(IJavaElement javaElement, String template,
			String templateName, Map<String, String> properties) throws Exception;

	/**
	 * Validate template.
	 *
	 * @param template the template
	 * @throws ValidationException the validation exception
	 * @throws Exception thrown if an exception occured
	 */
	public void validateTemplate(String template) throws ValidationException,
			Exception;

	/**
	 * Gets the template text.
	 *
	 * @param templateName the template name
	 * @return the template text
	 */
	public String getTemplateText(String templateName);

	/**
	 * Checks if a template with the given name exists.
	 *
	 * @param templateName the template name
	 * @return true, if template exists
	 */
	public boolean existsTemplate(String templateName);

	/**
	 * Adds a new template or replaces an existing.
	 * @param entry the template entry
	 */
	public void putTemplate(TemplateEntry entry);
}
