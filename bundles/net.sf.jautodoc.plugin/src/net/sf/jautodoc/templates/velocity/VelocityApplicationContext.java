/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.IApplicationContext;
import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.templates.ITemplateManager;
import net.sf.jautodoc.templates.NullTemplateManager;
import net.sf.jautodoc.templates.contentassist.AutoCloseStrategy;
import net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant;
import net.sf.jautodoc.templates.contentassist.TagContentAssistant;
import net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider;
import net.sf.jautodoc.templates.rules.GeneralRulesStrategie;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.rules.ITemplatePartitions;
import net.sf.jautodoc.templates.rules.TemplateReplacementsRulesStrategy;
import net.sf.jautodoc.templates.velocity.contentassist.AutoIndentStrategy;
import net.sf.jautodoc.templates.velocity.contentassist.DirectiveAndCommentStartStrategy;
import net.sf.jautodoc.templates.velocity.contentassist.DirectiveContentAssistant;
import net.sf.jautodoc.templates.velocity.contentassist.ReferenceContentAssistant;
import net.sf.jautodoc.templates.velocity.contentassist.ReferenceTextHover;
import net.sf.jautodoc.templates.velocity.rules.DirectiveRulesStrategie;
import net.sf.jautodoc.templates.velocity.rules.EmptyCommentRule;
import net.sf.jautodoc.templates.velocity.rules.ReferenceRulesStrategie;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.Token;


/**
 * Application context used with Velocity as template engine.
 */
public class VelocityApplicationContext implements IApplicationContext {
    private ITemplateManager templateManager;


    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplateManager()
     */
    public ITemplateManager getTemplateManager() {
        if (templateManager != null) {
            return templateManager;
        }

        templateManager = new VelocityTemplateManager();
        try {
            templateManager.initialize();
        } catch (Exception e) {
            JAutodocPlugin.getDefault().handleException(e);
            templateManager = new NullTemplateManager();
        }

        return templateManager;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplateRuleStrategies()
     */
    public IRulesStrategy[] getTemplateRuleStrategies() {
        IRulesStrategy[] ruleStrategies = {
                new ReferenceRulesStrategie(),
                new DirectiveRulesStrategie(),
                new GeneralRulesStrategie()};
        return ruleStrategies;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplatePreviewRuleStrategies(net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider)
     */
    public IRulesStrategy[] getTemplatePreviewRuleStrategies(
            ITemplateReplacementsProvider replacementsProvider) {
        IRulesStrategy[] ruleStrategies = {
                new TemplateReplacementsRulesStrategy(replacementsProvider),
                new ReferenceRulesStrategie(),
                new DirectiveRulesStrategie(),
                new GeneralRulesStrategie()};
        return ruleStrategies;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplateTextHover()
     */
    public ITextHover getTemplateTextHover(Map<String, String> properties) {
        return new ReferenceTextHover(properties);
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplateContentAssistants()
     */
    public ITemplateContentAssistant[] getTemplateContentAssistants(Map<String, String> properties) {
        ITemplateContentAssistant[] assistants = {
                new ReferenceContentAssistant(properties, false),
                new DirectiveContentAssistant(),
                new TagContentAssistant()};
        return assistants;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getHeaderTemplateContentAssistants(java.util.Map)
     */
    public ITemplateContentAssistant[] getHeaderTemplateContentAssistants(Map<String, String> properties) {
        ITemplateContentAssistant[] assistants = {
                new ReferenceContentAssistant(properties, true),
                new DirectiveContentAssistant()};
        return assistants;
    }

    public ITemplateContentAssistant[] getPackageDocTemplateContentAssistants(Map<String, String> properties) {
        ITemplateContentAssistant[] assistants = {
                new ReferenceContentAssistant(properties, true),
                new DirectiveContentAssistant(),
                new TagContentAssistant()};
        return assistants;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplateAutoEditStrategies()
     */
    public IAutoEditStrategy[] getTemplateAutoEditStrategies() {
        IAutoEditStrategy[] autoEditStrategies = {
                new AutoIndentStrategy(),
                new DirectiveAndCommentStartStrategy(),
                new AutoCloseStrategy()};
        return autoEditStrategies;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.IApplicationContext#getTemplatePartitioningRules()
     */
    public IPredicateRule[] getTemplatePartitioningRules() {
        IToken multiLineComment     = new Token(ITemplatePartitions.MULTI_LINE_COMMENT);
        IToken singleLineComment = new Token(ITemplatePartitions.SINGLE_LINE_COMMENT);

        List<IRule> rules = new ArrayList<IRule>();

        // Add rule for single line comments
        rules.add(new EndOfLineRule("##", singleLineComment));

        // Add rules for empty multi-line comments
        rules.add(new EmptyCommentRule(multiLineComment));

        // Add rules for multi-line comments
        rules.add(new MultiLineRule("#*", "*#", multiLineComment));

        return (IPredicateRule[])rules.toArray(new IPredicateRule[rules.size()]);
    }
}
