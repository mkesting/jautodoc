/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.templates.ITemplateRegistry;
import net.sf.jautodoc.templates.TemplateEntry;
import net.sf.jautodoc.templates.TemplateSet;

import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;


/**
 * Wrapper class for the StringResourceRepository of Velocity.
 */
public class VelocityTemplateRegistry implements ITemplateRegistry {
	private Map<String, TemplateEntry> templates;
	private StringResourceRepository repository;
	
	
	/**
	 * The Constructor.
	 */
	public VelocityTemplateRegistry() {
		this.templates  = new HashMap<String, TemplateEntry>();
		this.repository = StringResourceLoader.getRepository();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateRegistry#getTemplate(java.lang.String)
	 */
	public TemplateEntry getTemplate(String templateName) {
		return templates.get(templateName);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateRegistry#putTemplates(net.sf.jautodoc.templates.TemplateSet)
	 */
	public void putTemplates(TemplateSet templates) {
		clearTemplates();
		registerTemplates(templates);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateRegistry#putTemplate(net.sf.jautodoc.templates.TemplateEntry)
	 */
	public void putTemplate(TemplateEntry entry) {
		templates.put(entry.getName(), entry);
		repository.putStringResource(entry.getName(), entry.getText());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.ITemplateRegistry#containsTemplate(java.lang.String)
	 */
	public boolean containsTemplate(String templateName) {
		return templates.containsKey(templateName);
	}
	
	private void clearTemplates() {
		Iterator<String> iter = templates.keySet().iterator();
		while (iter.hasNext()) {
			String name = iter.next();
			repository.removeStringResource(name);
		}
		templates.clear();
	}
	
	private void registerTemplates(TemplateSet templates) {
		registerTemplates(templates.getTypeTemplates());
		registerTemplates(templates.getMethodTemplates());
		registerTemplates(templates.getFieldTemplates());
		registerTemplates(templates.getParameterTemplates());
		registerTemplates(templates.getExceptionTemplates());
	}
	
	private void registerTemplates(List<TemplateEntry> templateList) {
		int size = templateList.size();
		for (int i = 0; i < size; ++i) {
			TemplateEntry entry = templateList.get(i);
			putTemplate(entry);
			registerTemplates(entry.getChildTemplates());
		}
	}
}
