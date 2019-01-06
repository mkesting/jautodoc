/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;


/**
 * Element proposal class.
 */
public class ElementProposal {
	protected String proposal;
	protected String displayPrefix;
	protected String description;
	protected int selectionOffset;
	protected int selectionLength;
	protected boolean multiple;
	
	
	/**
	 * Instantiates a new element proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayPrefix the display prefix
	 * @param description the description
	 */
	public ElementProposal(String proposal, String displayPrefix, String description) {
		this(proposal, displayPrefix, description, 0, 0, false);
	}
	
	/**
	 * Instantiates a new element proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayPrefix the display prefix
	 * @param description the description
	 * @param selectionOffset the selection offset
	 */
	public ElementProposal(String proposal, String displayPrefix, String description,
			int selectionOffset) {
		this(proposal, displayPrefix, description, selectionOffset, 0, false);
	}
	
	/**
	 * Instantiates a new element proposal.
	 * 
	 * @param proposal the proposal
	 * @param displayPrefix the display prefix
	 * @param description the description
	 * @param selectionOffset the selection offset
	 * @param selectionLength the selection length
	 * @param multiple the multiple
	 */
	public ElementProposal(String proposal, String displayPrefix, String description,
			int selectionOffset, int selectionLength, boolean multiple) {
		this.proposal = proposal;
		this.displayPrefix = displayPrefix;
		this.description = description;
		this.selectionOffset = selectionOffset;
		this.selectionLength = selectionLength;
		this.multiple = multiple;
	}
	
	/**
	 * Gets the proposal.
	 * 
	 * @return the proposal
	 */
	public String getProposal() {
		return proposal;
	}
	
	/**
	 * Gets the display string.
	 * 
	 * @return the display string
	 */
	public String getDisplayString() {
		return displayPrefix + description;
	}
	
	/**
	 * Gets the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Gets the selection offset.
	 * 
	 * @return the selection offset
	 */
	public int getSelectionOffset() {
		return selectionOffset;
	}
	
	/**
	 * Gets the selection length.
	 * 
	 * @return the selection length
	 */
	public int getSelectionLength() {
		return selectionLength;
	}
	
	/**
	 * Checks if multiple occurence is allowed.
	 * 
	 * @return the boolean
	 */
	public boolean isMultiple() {
		return multiple;
	}
	
	/**
	 * Checks if the proposal matches the given token.
	 * 
	 * @param token the token
	 * 
	 * @return true, if proposal matches the given token
	 */
	public boolean matches(String token) {
		return proposal.equals(token);
	}
}
