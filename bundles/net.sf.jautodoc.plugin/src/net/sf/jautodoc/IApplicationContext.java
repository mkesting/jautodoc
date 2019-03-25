/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc;

import java.util.Map;

import net.sf.jautodoc.templates.ITemplateManager;
import net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant;
import net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider;
import net.sf.jautodoc.templates.rules.IRulesStrategy;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.rules.IPredicateRule;


/**
 * Interface for JAutodoc application context.
 */
public interface IApplicationContext {

    /**
     * Gets the template manager.
     *
     * @return the template manager
     */
    public ITemplateManager getTemplateManager();

    /**
     * Gets the template rule strategies.
     *
     * @return the template rule strategies
     */
    public IRulesStrategy[] getTemplateRuleStrategies();

    /**
     * Gets the template preview rule strategies.
     *
     * @param replacementsProvider the replacements provider
     *
     * @return the template preview rule strategies
     */
    public IRulesStrategy[] getTemplatePreviewRuleStrategies(ITemplateReplacementsProvider replacementsProvider);

    /**
     * Gets the template text hover.
     *
     * @param properties the properties to use
     *
     * @return the template text hover
     */
    public ITextHover getTemplateTextHover(Map<String, String> properties);

    /**
     * Gets the template content assistants.
     *
     * @return the template content assistants
     */
    public ITemplateContentAssistant[] getTemplateContentAssistants(Map<String, String> properties);

    /**
     * Gets the header template content assistants.
     *
     * @param properties the properties to use
     *
     * @return the header template content assistants
     */
    public ITemplateContentAssistant[] getHeaderTemplateContentAssistants(Map<String, String> properties);

    /**
     * Gets the package javadoc template content assistants.
     *
     * @param properties the properties to use
     *
     * @return the package javadoc template content assistants
     */
    public ITemplateContentAssistant[] getPackageDocTemplateContentAssistants(Map<String, String> properties);

    /**
     * Gets the template auto edit strategies.
     *
     * @return the template auto edit strategies
     */
    public IAutoEditStrategy[] getTemplateAutoEditStrategies();

    /**
     * Gets the template partitioning rules.
     *
     * @return the template partitioning rules
     */
    public IPredicateRule[] getTemplatePartitioningRules();
}
