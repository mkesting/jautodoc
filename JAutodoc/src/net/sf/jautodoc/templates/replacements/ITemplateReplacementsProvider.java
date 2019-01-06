/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.replacements;

import java.util.Collection;


/**
 * The template replacements provider.
 */
public interface ITemplateReplacementsProvider {
	
	/**
	 * Gets a list of replacement strings for
	 * references in the template.
	 * 
	 * @return a list of replacement strings
	 */
	public Collection<String> getReplacements();
	
	/**
	 * Adds a template replacements listener.
	 * 
	 * @param listener the listener
	 */
	public void addTemplateReplacementsListener(ITemplateReplacementsListener listener);
}
