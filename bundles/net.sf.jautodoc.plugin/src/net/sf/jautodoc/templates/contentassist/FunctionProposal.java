/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Function proposal class.
 */
public class FunctionProposal extends ElementProposal {
	private String regex;
	private Set<ElementProposal> predecessors = new LinkedHashSet<ElementProposal>();

	
	/**
	 * Instantiates a new function proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param description the description
	 * @param selectionOffset the selection offset
	 */
	public FunctionProposal(String proposal, String displayString, String description,
			int selectionOffset) {
		this(proposal, displayString, description, null, selectionOffset, 0, false);
	}
	
	/**
	 * Instantiates a new function proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param description the description
	 * @param selectionOffset the selection offset
	 * @param multiple the multiple
	 */
	public FunctionProposal(String proposal, String displayString, String description,
			int selectionOffset, boolean multiple) {
		this(proposal, displayString, description, null, selectionOffset, 0, multiple);
	}
	
	/**
	 * Instantiates a new function proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param description the description
	 * @param regex the regex
	 * @param selectionOffset the selection offset
	 * @param selectionLength the selection length
	 * @param multiple the multiple
	 */
	public FunctionProposal(String proposal, String displayString, String description,
			String regex, int selectionOffset, int selectionLength, boolean multiple) {
		super(proposal, displayString, description, selectionOffset, selectionLength, multiple);
		this.regex = regex;
	}
	
	/**
	 * Adds a predecessor.
	 * 
	 * @param predecessor a predecessor
	 */
	public void addPredecessor(ElementProposal predecessor) {
		predecessors.add(predecessor);
	}
	
	/**
	 * Checks if the given element is a valid predecessor.
	 * 
	 * @param predecessor the predecessor
	 * 
	 * @return true, if valid predecessor
	 */
	public boolean isPredecessor(ElementProposal predecessor) {
		return predecessors.contains(predecessor);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.contentassist.ElementProposal#matches(java.lang.String)
	 */
	public boolean matches(String token) {
		if (regex == null) {
			return super.matches(token);
		}
		else {
			return token.matches(regex);
		}
	}
}
