/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.contentassist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sf.jautodoc.templates.contentassist.TemplateProposal;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;


/**
 * Manager for Velocity directives.
 */
public class DirectiveManager {
	private static Set<String> SINGLE_LINE_DIRECTIVES = new HashSet<String>();
	private static Set<String> MULTI_LINE_DIRECTIVES  = new HashSet<String>();
	private static Set<String> ALL_DIRECTIVES  		  = new TreeSet<String>(); // sorted
	private static Set<String> PARANTHESIS_DIRECTIVES = new HashSet<String>();
	
	static {
		SINGLE_LINE_DIRECTIVES.add("#set");
		SINGLE_LINE_DIRECTIVES.add("#stop");
		SINGLE_LINE_DIRECTIVES.add("#parse");
		SINGLE_LINE_DIRECTIVES.add("#include");
		
		MULTI_LINE_DIRECTIVES.add("#if");
		MULTI_LINE_DIRECTIVES.add("#macro");
		MULTI_LINE_DIRECTIVES.add("#foreach");
		
		ALL_DIRECTIVES.add("#end");
		ALL_DIRECTIVES.add("#else");
		ALL_DIRECTIVES.add("#elseif");
		ALL_DIRECTIVES.addAll(SINGLE_LINE_DIRECTIVES);
		ALL_DIRECTIVES.addAll(MULTI_LINE_DIRECTIVES);
		
		PARANTHESIS_DIRECTIVES.add("#set");
		PARANTHESIS_DIRECTIVES.add("#if");
		PARANTHESIS_DIRECTIVES.add("#elseif");
		PARANTHESIS_DIRECTIVES.add("#foreach");
		PARANTHESIS_DIRECTIVES.add("#include");
		PARANTHESIS_DIRECTIVES.add("#parse");
		PARANTHESIS_DIRECTIVES.add("#macro");
	}
	
	/**
	 * Get all directives.
	 * 
	 * @return the directives
	 */
	public static String[] getDirectives() {
		return (String[])ALL_DIRECTIVES.toArray(new String[ALL_DIRECTIVES.size()]);
	}
	
	/**
	 * Checks if is multi line directive.
	 * 
	 * @param directive the directive
	 * 
	 * @return true, if is multi line directive
	 */
	public static boolean isMultiLineDirective(String directive) {
		return MULTI_LINE_DIRECTIVES.contains(directive);
	}
	
	/**
	 * Checks if the given directive has parenthesis.
	 * 
	 * @param directive the directive
	 * 
	 * @return true, if has parenthesis
	 */
	public static boolean hasParenthesis(String directive) {
		return PARANTHESIS_DIRECTIVES.contains(directive);
	}
	
	/**
	 * Gets the completion proposals for the given element.
	 * 
	 * @param element the element
	 * @param offset the offset
	 * @param length the length
	 * 
	 * @return the completion proposals
	 */
	public static List<ICompletionProposal> getCompletionProposals(String element, int offset, int length) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		
		Iterator<String> iter = ALL_DIRECTIVES.iterator();
		while (iter.hasNext()) {
			String proposal = iter.next();
			if (proposal.startsWith(element)) {
				int cursorOffset = proposal.length();
				String displayString = proposal;
				if (hasParenthesis(proposal)) {
					proposal += "()";
					cursorOffset = proposal.length() - 1;
				}
				
				proposals.add(createTemplateProposal(
						proposal, displayString, offset, length, cursorOffset));
			}
		}
		
		return proposals;
	}
	
	/**
	 * Creates a template proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param offset the offset
	 * @param length the length
	 * @param cursorOffset the cursor offset
	 * 
	 * @return the template proposal
	 */
	private static ICompletionProposal createTemplateProposal(String proposal,
			String displayString, int offset, int length, int cursorOffset) {
		return new TemplateProposal(proposal, displayString, offset, length,
				new Point(offset + cursorOffset, 0));
	}
}
