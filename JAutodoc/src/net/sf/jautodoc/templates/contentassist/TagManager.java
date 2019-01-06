/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.contentassist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;


/**
 * Manager for Javadoc tags.
 */
public class TagManager {
	private static Set<String> TYPE_TAGS  	= new HashSet<String>();
	private static Set<String> FIELD_TAGS 	= new HashSet<String>();
	private static Set<String> METHOD_TAGS 	= new HashSet<String>();
	private static Set<String> ALL_TAGS 	= new TreeSet<String>(); // sorted
	private static Set<String> INLINE_TAGS 	= new HashSet<String>();
	
	static {
		TYPE_TAGS.add("@see");
		TYPE_TAGS.add("@since");
		TYPE_TAGS.add("@deprecated");
		TYPE_TAGS.add("@serial");
		TYPE_TAGS.add("@author");
		TYPE_TAGS.add("@version");
		TYPE_TAGS.add("{@link}");
		TYPE_TAGS.add("{@linkplain}");
		TYPE_TAGS.add("{@docRoot}");
		TYPE_TAGS.add("{@code}");
		TYPE_TAGS.add("{@literal}");
		
		FIELD_TAGS.add("@see");
		FIELD_TAGS.add("@since");
		FIELD_TAGS.add("@deprecated");
		FIELD_TAGS.add("@serial");
		FIELD_TAGS.add("@serialField");
		FIELD_TAGS.add("{@link}");
		FIELD_TAGS.add("{@linkplain}");
		FIELD_TAGS.add("{@docRoot}");
		FIELD_TAGS.add("{@value}");
		FIELD_TAGS.add("{@code}");
		FIELD_TAGS.add("{@literal}");
		
		METHOD_TAGS.add("@see");
		METHOD_TAGS.add("@since");
		METHOD_TAGS.add("@deprecated");
		METHOD_TAGS.add("@param");
		METHOD_TAGS.add("@return");
		METHOD_TAGS.add("@throws");
		METHOD_TAGS.add("@serialField");
		METHOD_TAGS.add("{@link}");
		METHOD_TAGS.add("{@linkplain}");
		METHOD_TAGS.add("{@inheritDoc}");
		METHOD_TAGS.add("{@docRoot}");
		METHOD_TAGS.add("{@code}");
		METHOD_TAGS.add("{@literal}");
		
		ALL_TAGS.addAll(TYPE_TAGS);
		ALL_TAGS.addAll(FIELD_TAGS);
		ALL_TAGS.addAll(METHOD_TAGS);
		
		INLINE_TAGS.add("{@link}");
		INLINE_TAGS.add("{@linkplain}");
		INLINE_TAGS.add("{@inheritDoc}");
		INLINE_TAGS.add("{@docRoot}");
		INLINE_TAGS.add("{@value}");
		INLINE_TAGS.add("{@code}");
		INLINE_TAGS.add("{@literal}");
	}
	
	
	/**
	 * Checks if is inline tag {@ }.
	 * 
	 * @param tag the tag
	 * 
	 * @return true, if is inline tag
	 */
	public static boolean isInlineTag(String tag) {
		return INLINE_TAGS.contains(tag);
	}
	
	/**
	 * Gets the completion proposals for the given element.
	 * 
	 * @param element the element
	 * @param offset the offset
	 * @param length the length
	 * 
	 * @return list of completion proposals
	 */
	public static List<ICompletionProposal> getCompletionProposals(String element, int offset, int length) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		
		Iterator<String> iter = ALL_TAGS.iterator();
		while (iter.hasNext()) {
			String proposal = iter.next();
			if (proposal.startsWith(element)) {
				int cursorOffset = proposal.length();
				String displayString = proposal;
				if (isInlineTag(proposal)) {
					cursorOffset -= 1;
				}
				else {
					proposal += " ";
					cursorOffset += 1;
				}
				proposals.add(createTemplateProposal(proposal, displayString, offset, length, cursorOffset));
			}
		}
		
		return proposals;
	}
	
	private static TemplateProposal createTemplateProposal(String proposal,
			String displayString, int offset, int length, int cursorOffset) {
		return new TemplateProposal(proposal, displayString, offset, length,
				new Point(offset + cursorOffset, 0));
	}
}
