/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.templates.wrapper.IMemberWrapper;
import net.sf.jautodoc.templates.wrapper.WrapperFactory;

import org.apache.velocity.runtime.parser.ParseException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;


/**
 * Abstract base class for template managers.
 */
public abstract class AbstractTemplateManager implements ITemplateManager {

	private static final String	DEFAULT_TEMPLATES = new String("default_templates.xml");
	private static final File	USER_TEMPLATES = JAutodocPlugin.getDefault().getStateLocation()
										.append("user_templates.xml").toFile();

	private TemplateSet		 templates;

	private MatchingElement matchingParent;
	private MatchingElement matchingMethod;


	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#initialize()
	 */
	public void initialize() throws Exception {
		onInit();
		loadTemplates();
	}

	/**
	 * This method is called during initialization and has
	 * to be overridden by concrete sub classes.
	 *
	 * @throws Exception initialization exception
	 */
	protected abstract void onInit() throws Exception;

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#loadTemplates()
	 */
	public void loadTemplates() throws Exception {
		if (USER_TEMPLATES.exists()) {
			loadTemplates(USER_TEMPLATES);
		}
		else {
			loadDefaultTemplates();
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#loadTemplates(java.io.File)
	 */
	public void loadTemplates(File file) throws Exception {
		templates = TemplateSerializer.loadTemplates(file);
		getRegistry().putTemplates(templates);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#loadDefaultTemplates()
	 */
	public void loadDefaultTemplates() throws Exception {
		templates = TemplateSerializer.loadTemplates(getClass().getResourceAsStream(DEFAULT_TEMPLATES));
		getRegistry().putTemplates(templates);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#storeTemplates()
	 */
	public void storeTemplates() throws Exception {
		storeTemplates(USER_TEMPLATES);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#storeTemplates(java.io.File)
	 */
	public void storeTemplates(File file) throws Exception {
		TemplateSerializer.storeTemplates(templates, file);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getTemplates()
	 */
	public TemplateSet getTemplates() throws Exception {
		if (templates == null) {
			loadTemplates();
		}
		return templates;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
    public void setTemplates(final TemplateSet templates) throws Exception {
	    this.templates = templates;
        getRegistry().putTemplates(templates);
    }

    /* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getTypeTemplates()
	 */
	public List<TemplateEntry> getTypeTemplates() throws Exception {
		return getTemplates().getTypeTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getFieldTemplates()
	 */
	public List<TemplateEntry> getFieldTemplates() throws Exception {
		return getTemplates().getFieldTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getMethodTemplates()
	 */
	public List<TemplateEntry> getMethodTemplates() throws Exception {
		return getTemplates().getMethodTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getParameterTemplates()
	 */
	public List<TemplateEntry> getParameterTemplates() throws Exception {
		return getTemplates().getParameterTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getExceptionTemplates()
	 */
	public List<TemplateEntry> getExceptionTemplates() throws Exception {
		return getTemplates().getExceptionTemplates();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#applyTemplate(org.eclipse.jdt.core.IMember)
	 */
	public String applyTemplate(IMember member, Map<String, String> properties) throws Exception {
		return applyTemplate(WrapperFactory.getWrapper(member), properties);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#applyParameterTemplate(org.eclipse.jdt.core.IMember, java.lang.String, java.lang.String)
	 */
	public String applyParameterTemplate(IMember member, String type,
			String name, Map<String, String> properties) throws Exception {
		return applyTemplate(WrapperFactory.getParameterWrapper(member, type, name), properties);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#applyExceptionTemplate(org.eclipse.jdt.core.IMethod, java.lang.String)
	 */
	public String applyExceptionTemplate(IMethod method, String name,
			Map<String, String> properties) throws Exception {
		return applyTemplate(WrapperFactory.getExceptionWrapper(method, name), properties);
	}

	/**
	 * Try to apply a template to the given member.
	 *
	 * @param member the member
	 * @param properties the properties to use
	 *
	 * @return the resulting string
	 *
	 * @throws Exception thrown if an exception occured
	 */
	private String applyTemplate(IMemberWrapper member, Map<String, String> properties) throws Exception {
		MatchingElement me = searchMatchingElement(member);
		if (me != null) {
			return applyTemplate(me, properties);
		}

		return "";
	}

	/**
	 * Apply a template to the given matching element. This method has to be
	 * overridden by concrete sub classes.
	 *
	 * @param me the matching element
	 * @param properties the properties to use
	 *
	 * @return the resulting string
	 *
	 * @throws Exception thrown if an exception occured
	 */
	protected abstract String applyTemplate(MatchingElement me, Map<String, String> properties) throws Exception;

	/**
	 * Ensures that the matching parent for the given member is set.
	 *
	 * @param member the member
	 *
	 * @throws Exception thrown if an exception occured
	 */
	private void ensureMatchingParent(IMemberWrapper member) throws Exception {
		IMemberWrapper parent = member.getParent();
		if (parent != null) {
			if (parent.isType()) {
			    matchingMethod = null;
				if (matchingParent == null || !matchingParent.getMember().equals(parent)) {
					matchingParent = searchMatchingElement(parent);
				}
			}
			else if (parent.isMethod()) {
				if (matchingMethod == null || !matchingMethod.getMember().equals(parent)) {
					matchingMethod = searchMatchingElement(parent);
				}
			}
		}
		else {
			if (member.isParameter() || member.isException()) {
			    matchingMethod = null;
			}
			else {
			    matchingParent = null;
			}
		}
	}

	/**
	 * Searches the matching element for the given member.
	 *
	 * @param member the member
	 *
	 * @return the matching element
	 *
	 * @throws Exception thrown if an exception occured
	 */
	private MatchingElement searchMatchingElement(IMemberWrapper member) throws Exception {
		ensureMatchingParent(member);

		List<TemplateEntry> templates = null;
		if (member.isType()) {
			if (matchingParent != null) {
				templates = new ArrayList<TemplateEntry>(matchingParent.getChildTemplates().getTypeTemplates());
				templates.addAll(getTypeTemplates());
			}
			else {
				templates = getTypeTemplates();
			}
		}
		else if (member.isField()) {
			if (matchingParent != null) {
				templates = new ArrayList<TemplateEntry>(matchingParent.getChildTemplates().getFieldTemplates());
				templates.addAll(getFieldTemplates());
			}
			else {
				templates = getFieldTemplates();
			}
		}
		else if (member.isMethod()) {
			if (matchingParent != null) {
				templates = new ArrayList<TemplateEntry>(matchingParent.getChildTemplates().getMethodTemplates());
				templates.addAll(getMethodTemplates());
			}
			else {
				templates = getMethodTemplates();
			}
		}
		else if (member.isParameter()) {
			if (matchingMethod != null) {
				templates = new ArrayList<TemplateEntry>(matchingMethod.getChildTemplates().getParameterTemplates());
				templates.addAll(getParameterTemplates());
			}
			else {
				templates = getParameterTemplates();
			}
		}
		else if (member.isException()) {
			if (matchingMethod != null) {
				templates = new ArrayList<TemplateEntry>(matchingMethod.getChildTemplates().getExceptionTemplates());
				templates.addAll(getExceptionTemplates());
			}
			else {
				templates = getExceptionTemplates();
			}
		}

		MatchingElement me = searchMatchingElement(member, templates);
		if (member.isType()) {
			matchingParent = me;
		}
		else if (member.isMethod()) {
			matchingMethod = me;
		}

		return me;
	}

	/**
	 * Searches the matching element for the given member an the
	 * selected templates.
	 *
	 * @param member the member
	 * @param templates the templates
	 *
	 * @return the matching element
	 *
	 * @throws Exception thrown if an exception occured
	 */
	private MatchingElement searchMatchingElement(IMemberWrapper member, List<TemplateEntry> templates)
															throws Exception {
		int size = templates.size();
		for (int i = 0; i < size; ++i) {
			MatchingElement me = getMatchingElement(member, (TemplateEntry)templates.get(i));
			if (me != null) {
				return me;
			}
		}

		return null;
	}

	/**
	 * Checks if the given member matches the given template entry
	 * and returns the corresponding matching element.
	 *
	 * @param member the member
	 * @param entry the entry
	 *
	 * @return the matching element or null in case of no match
	 *
	 * @throws Exception thrown if an exception occured
	 */
	private MatchingElement getMatchingElement(IMemberWrapper member, TemplateEntry entry)
															throws Exception {
		String text = entry.isUseSignature() ? member.getSignature() : member.getName();
		Matcher matcher = entry.getPattern().matcher(text);
		if (!matcher.matches()) {
			return null;
		}

		MatchingElement parent = (member.isParameter() || member.isException()
				? matchingMethod : matchingParent);
		return new MatchingElement(member, entry, matcher, parent);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#evaluateTemplate(java.util.regex.Matcher, java.util.regex.Matcher, java.lang.String, net.sf.jautodoc.templates.TemplateEntry)
	 */
	public String evaluateTemplate(Matcher matcher, Matcher parentMatcher,
			String template, TemplateEntry entry, Map<String, String> properties) throws Exception {
		MatchingElement parentMe = null;
		if (parentMatcher != null && parentMatcher.matches()) {
			parentMe = new MatchingElement(null, entry.getParent(), parentMatcher, null);
		}

		MatchingElement me = new MatchingElement(null, entry, matcher, parentMe);

		return evaluateTemplate(me, template, properties);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#evaluateTemplate(org.eclipse.jdt.core.IJavaElement, java.lang.String, java.lang.String)
	 */
	public abstract String evaluateTemplate(IJavaElement javaElement, String template,
			String templateName, Map<String, String> properties) throws Exception;

	/**
	 * Evaluate template for the given matching element.
	 *
	 * @param me the matching element
	 * @param template the template
	 * @param properties the properties to use
	 *
	 * @return the resulting string
	 *
	 * @throws Exception thrown if an exception occured
	 */
	protected abstract String evaluateTemplate(MatchingElement me,
			String template, Map<String, String> properties) throws Exception;

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#validateTemplate(java.lang.String)
	 */
	public abstract void validateTemplate(String template) throws ParseException, Exception;

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#getTemplateText(java.lang.String)
	 */
	public String getTemplateText(String templateName) {
		TemplateEntry entry = getRegistry().getTemplate(templateName);
		if (entry != null) {
			return entry.getText();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#existsTemplate(java.lang.String)
	 */
	public boolean existsTemplate(String templateName) {
		return getRegistry().containsTemplate(templateName);
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateManager#putTemplate(net.sf.jautodoc.templates.TemplateEntry)
	 */
	public void putTemplate(TemplateEntry entry) {
		getRegistry().putTemplate(entry);
	}

	/**
	 * Gets the used template registry.
	 *
	 * @return the registry
	 */
	protected abstract ITemplateRegistry getRegistry();
}
