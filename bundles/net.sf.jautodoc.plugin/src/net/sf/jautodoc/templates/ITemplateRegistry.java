/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;


/**
 * Interface for template registries.
 */
public interface ITemplateRegistry {

	/**
	 * Gets the template for the given name.
	 * 
	 * @param templateName the template name
	 * 
	 * @return the template
	 */
	public TemplateEntry getTemplate(String templateName);
	
	/**
	 * Replaces all templates by the given template set.
	 * 
	 * @param templates the new templates
	 */
	public void putTemplates(TemplateSet templates);
	
	/**
	 * Adds a new template or replaces an existing.
	 * 
	 * @param entry the template entry
	 */
	public void putTemplate(TemplateEntry entry);

	/**
	 * Checks if a template with the given name already exists.
	 * 
	 * @param templateName the template name
	 * 
	 * @return true, if template exists
	 */
	public boolean containsTemplate(String templateName);
}
