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
 * This template manager is used, when no template engine is available.
 */
public class NullTemplateManager implements ITemplateManager {
	TemplateSet templates = new TemplateSet();


	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getTemplates()
	 */
	public TemplateSet getTemplates() throws Exception {
		return templates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void setTemplates(TemplateSet templates) throws Exception {
        // empty
    }

    /* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getTypeTemplates()
	 */
	public List<TemplateEntry> getTypeTemplates() throws Exception {
		return templates.getTypeTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getFieldTemplates()
	 */
	public List<TemplateEntry> getFieldTemplates() throws Exception {
		return templates.getFieldTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getMethodTemplates()
	 */
	public List<TemplateEntry> getMethodTemplates() throws Exception {
		return templates.getMethodTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getParameterTemplates()
	 */
	public List<TemplateEntry> getParameterTemplates() throws Exception {
		return templates.getParameterTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getExceptionTemplates()
	 */
	public List<TemplateEntry> getExceptionTemplates() throws Exception {
		return templates.getExceptionTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#applyTemplate(org.eclipse.jdt.core.IMember)
	 */
	public String applyTemplate(IMember member, Map<String, String> properties) throws Exception {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#applyParameterTemplate(org.eclipse.jdt.core.IMember, java.lang.String, java.lang.String)
	 */
	public String applyParameterTemplate(IMember member, String type,
			String name, Map<String, String> properties) throws Exception {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#applyExceptionTemplate(org.eclipse.jdt.core.IMethod, java.lang.String)
	 */
	public String applyExceptionTemplate(IMethod method, String name, Map<String, String> properties)
			throws Exception {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getTemplateText(java.lang.String)
	 */
	public String getTemplateText(String templateName) {
		return "";
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#existsTemplate(java.lang.String)
	 */
	public boolean existsTemplate(String templateName) {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#evaluateTemplate(org.eclipse.jdt.core.IJavaElement, java.lang.String, java.lang.String)
	 */
	public String evaluateTemplate(IJavaElement javaElement, String template,
			String templateName, Map<String, String> properties) throws Exception {
		return template;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#evaluateTemplate(java.util.regex.Matcher, java.util.regex.Matcher, java.lang.String, net.sf.jautodoc.templates.TemplateEntry)
	 */
	public String evaluateTemplate(Matcher matcher, Matcher parentMatcher,
			String template, TemplateEntry entry, Map<String, String> properties) throws Exception {
		return template;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#validateTemplate(java.lang.String)
	 */
	public void validateTemplate(String template) throws ValidationException,
			Exception {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#initialize()
	 */
	public void initialize() throws Exception {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#loadDefaultTemplates()
	 */
	public void loadDefaultTemplates() throws Exception {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#loadTemplates()
	 */
	public void loadTemplates() throws Exception {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#loadTemplates(java.io.File)
	 */
	public void loadTemplates(File file) throws Exception {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#putTemplate(net.sf.jautodoc.templates.TemplateEntry)
	 */
	public void putTemplate(TemplateEntry entry) {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#storeTemplates()
	 */
	public void storeTemplates() throws Exception {
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#storeTemplates(java.io.File)
	 */
	public void storeTemplates(File file) throws Exception {
	}
}
