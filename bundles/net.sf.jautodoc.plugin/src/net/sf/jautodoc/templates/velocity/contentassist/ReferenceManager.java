/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.contentassist;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sf.jautodoc.templates.contentassist.ElementProposal;
import net.sf.jautodoc.templates.contentassist.FunctionProposal;
import net.sf.jautodoc.templates.contentassist.TemplateProposal;
import net.sf.jautodoc.templates.velocity.VelocityTemplateManager;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;


/**
 * Manager for Velocity references.
 */
public class ReferenceManager {
	private static final List<ElementProposal> REFERENCE_PROPOSALS = new ArrayList<ElementProposal>();
	private static final List<ElementProposal> BASE_PROPERTIES 	   = new ArrayList<ElementProposal>();

	private static final ElementProposal ELEMENT = new ElementProposal(
			VelocityTemplateManager.KEY_ELEMENT,
			VelocityTemplateManager.KEY_ELEMENT + " - ", "The matching element", -1);

	private static final ElementProposal PROPERTIES = new ElementProposal(
			VelocityTemplateManager.KEY_PROPERTIES + ".get('')",
			VelocityTemplateManager.KEY_PROPERTIES + " - ", "Get system property", -3);

	// available functions
	private static final FunctionProposal GROUP  = new FunctionProposal(
			"g(1)",  "g(n) - ", "Group with index n", "g\\(\\d*\\)", -3, 1, false);
	private static final FunctionProposal SPLIT	 = new FunctionProposal(
			"s()",   "s()    - ", "Split string", -1);
	private static final FunctionProposal REPLACE = new FunctionProposal(
			"r()",   "r()    - ", "Replace prefix", -1);
	private static final FunctionProposal REPLACE_SPLIT = new FunctionProposal(
			"rs()",  "rs()   - ", "Replace prefix, split string", -1);
	private static final FunctionProposal REPLACE_SPLIT_UPPER = new FunctionProposal(
			"rsfu()",  "rsfu() - ", "Replace prefix, split string, first to upper", -1);
	private static final FunctionProposal REPLACE_SPLIT_LOWER = new FunctionProposal(
			"rsfl()",  "rsfl() - ", "Replace prefix, split string, first to lower", -1);
	private static final FunctionProposal LOWER  = new FunctionProposal(
			"fl()",  "fl()    - ", "First to lower", -1);
	private static final FunctionProposal UPPER  = new FunctionProposal(
			"fu()",  "fu()   - ", "First to upper", -1);
	private static final FunctionProposal SPLIT_LOWER  = new FunctionProposal(
			"sfl()", "sfl()  - ", "Split string, first to lower", -1);
	private static final FunctionProposal SPLIT_UPPER  = new FunctionProposal(
			"sfu()", "sfu() - ", "Split string, first to upper", -1);
	private static final FunctionProposal PARENT = new FunctionProposal(
			"p()",   "p()    - ", "Matching parent element", -1, true);
	private static final FunctionProposal TYPE = new FunctionProposal(
			"getType()",   "getType() - ", "Get element type", -1, true);
	private static final FunctionProposal DECLARING_TYPE = new FunctionProposal(
			"getDeclaringType()",   "getDeclaringType() - ", "Get declaring type", -1, true);
	private static final FunctionProposal STATIC = new FunctionProposal(
			"isStatic()",   "isStatic() - ", "True, if static element", -1, true);
	private static final FunctionProposal FINAL = new FunctionProposal(
			"isFinal()",   "isFinal() - ", "True, if final element", -1, true);
	private static final FunctionProposal LENGTH  = new FunctionProposal(
			"length()", "length() - ", "Length of string", -1);
	private static final FunctionProposal CHAR_AT  = new FunctionProposal(
			"charAt(0)",  "charAt(n) - ", "Char at index n", "charAt\\(\\d*\\)", -3, 1, false);
	private static final FunctionProposal IS_UPPER  = new FunctionProposal(
			"isUpperCase()", "isUpperCase()", "", -1);
	private static final FunctionProposal IS_LOWER  = new FunctionProposal(
			"isLowerCase()", "isLowerCase()", "", -1);
	private static final FunctionProposal IS_LETTER  = new FunctionProposal(
			"isLetter()", "isLetter()", "", -1);
	private static final FunctionProposal IS_DIGIT  = new FunctionProposal(
			"isDigit()", "isDigit()", "", -1);
	private static final FunctionProposal IS_LETTER_OR_DIGIT  = new FunctionProposal(
			"isLetterOrDigit()", "isLetterOrDigit()", "", -1);
	private static final FunctionProposal HAS_ANNOTATION  = new FunctionProposal(
            "hasAnnotation('name')",  "hasAnnotation('name') - ", "Check for annotation by name",
            "hasAnnotation\\('.*'\\)", -7, 4, false);
	private static final FunctionProposal GET_ANNOTATION  = new FunctionProposal(
            "getAnnotation('name')",  "getAnnotation('name') - ", "Get annotation by name",
            "getAnnotation\\('.*'\\)", -7, 4, false);
	private static final FunctionProposal GET_ANNOTATION_VALUE  = new FunctionProposal(
            "getValue('key')",  "getValue('key') - ", "Get annotation value by key",
            "getValue\\('.*'\\)", -6, 3, false);

	static {
		PARENT.addPredecessor(ELEMENT);
		PARENT.addPredecessor(PARENT);

		GROUP.addPredecessor(ELEMENT);
		GROUP.addPredecessor(PARENT);

		TYPE.addPredecessor(ELEMENT);
		TYPE.addPredecessor(PARENT);

		DECLARING_TYPE.addPredecessor(ELEMENT);
		DECLARING_TYPE.addPredecessor(PARENT);

		STATIC.addPredecessor(ELEMENT);
		STATIC.addPredecessor(PARENT);

		FINAL.addPredecessor(ELEMENT);
		FINAL.addPredecessor(PARENT);

		HAS_ANNOTATION.addPredecessor(ELEMENT);
        HAS_ANNOTATION.addPredecessor(PARENT);

		GET_ANNOTATION.addPredecessor(ELEMENT);
		GET_ANNOTATION.addPredecessor(PARENT);

		GET_ANNOTATION_VALUE.addPredecessor(GET_ANNOTATION);

		CHAR_AT.addPredecessor(ELEMENT);
		CHAR_AT.addPredecessor(PARENT);
		CHAR_AT.addPredecessor(GROUP);
		CHAR_AT.addPredecessor(TYPE);
		CHAR_AT.addPredecessor(DECLARING_TYPE);

		LENGTH.addPredecessor(ELEMENT);
		LENGTH.addPredecessor(PARENT);
		LENGTH.addPredecessor(GROUP);
		LENGTH.addPredecessor(TYPE);
		LENGTH.addPredecessor(DECLARING_TYPE);

		SPLIT.addPredecessor(ELEMENT);
		SPLIT.addPredecessor(PARENT);
		SPLIT.addPredecessor(GROUP);
		SPLIT.addPredecessor(REPLACE);
		SPLIT.addPredecessor(LOWER);
		SPLIT.addPredecessor(UPPER);
		SPLIT.addPredecessor(TYPE);
		SPLIT.addPredecessor(DECLARING_TYPE);

		REPLACE.addPredecessor(ELEMENT);
		REPLACE.addPredecessor(PARENT);
		REPLACE.addPredecessor(GROUP);
		REPLACE.addPredecessor(SPLIT);
		REPLACE.addPredecessor(LOWER);
		REPLACE.addPredecessor(UPPER);
		REPLACE.addPredecessor(SPLIT_LOWER);
		REPLACE.addPredecessor(SPLIT_UPPER);
		REPLACE.addPredecessor(TYPE);
		REPLACE.addPredecessor(DECLARING_TYPE);

		REPLACE_SPLIT.addPredecessor(ELEMENT);
		REPLACE_SPLIT.addPredecessor(PARENT);
		REPLACE_SPLIT.addPredecessor(GROUP);
		REPLACE_SPLIT.addPredecessor(LOWER);
		REPLACE_SPLIT.addPredecessor(UPPER);
		REPLACE_SPLIT.addPredecessor(TYPE);
		REPLACE_SPLIT.addPredecessor(DECLARING_TYPE);

		REPLACE_SPLIT_UPPER.addPredecessor(ELEMENT);
		REPLACE_SPLIT_UPPER.addPredecessor(PARENT);
		REPLACE_SPLIT_UPPER.addPredecessor(GROUP);
		REPLACE_SPLIT_UPPER.addPredecessor(TYPE);
		REPLACE_SPLIT_UPPER.addPredecessor(DECLARING_TYPE);

		REPLACE_SPLIT_LOWER.addPredecessor(ELEMENT);
		REPLACE_SPLIT_LOWER.addPredecessor(PARENT);
		REPLACE_SPLIT_LOWER.addPredecessor(GROUP);
		REPLACE_SPLIT_LOWER.addPredecessor(TYPE);
		REPLACE_SPLIT_LOWER.addPredecessor(DECLARING_TYPE);

		LOWER.addPredecessor(ELEMENT);
		LOWER.addPredecessor(PARENT);
		LOWER.addPredecessor(GROUP);
		LOWER.addPredecessor(SPLIT);
		LOWER.addPredecessor(REPLACE);
		LOWER.addPredecessor(REPLACE_SPLIT);
		LOWER.addPredecessor(TYPE);
		LOWER.addPredecessor(DECLARING_TYPE);

		UPPER.addPredecessor(ELEMENT);
		UPPER.addPredecessor(PARENT);
		UPPER.addPredecessor(GROUP);
		UPPER.addPredecessor(SPLIT);
		UPPER.addPredecessor(REPLACE);
		UPPER.addPredecessor(REPLACE_SPLIT);
		UPPER.addPredecessor(TYPE);
		UPPER.addPredecessor(DECLARING_TYPE);

		SPLIT_LOWER.addPredecessor(ELEMENT);
		SPLIT_LOWER.addPredecessor(PARENT);
		SPLIT_LOWER.addPredecessor(GROUP);
		SPLIT_LOWER.addPredecessor(REPLACE);
		SPLIT_LOWER.addPredecessor(TYPE);
		SPLIT_LOWER.addPredecessor(DECLARING_TYPE);

		SPLIT_UPPER.addPredecessor(ELEMENT);
		SPLIT_UPPER.addPredecessor(PARENT);
		SPLIT_UPPER.addPredecessor(GROUP);
		SPLIT_UPPER.addPredecessor(REPLACE);
		SPLIT_UPPER.addPredecessor(TYPE);
		SPLIT_UPPER.addPredecessor(DECLARING_TYPE);

		IS_UPPER.addPredecessor(CHAR_AT);
		IS_LOWER.addPredecessor(CHAR_AT);
		IS_LETTER.addPredecessor(CHAR_AT);
		IS_DIGIT.addPredecessor(CHAR_AT);
		IS_LETTER_OR_DIGIT.addPredecessor(CHAR_AT);

		REFERENCE_PROPOSALS.add(ELEMENT);
		REFERENCE_PROPOSALS.add(GROUP);
		REFERENCE_PROPOSALS.add(SPLIT);
		REFERENCE_PROPOSALS.add(REPLACE);
		REFERENCE_PROPOSALS.add(REPLACE_SPLIT);
		REFERENCE_PROPOSALS.add(REPLACE_SPLIT_UPPER);
		REFERENCE_PROPOSALS.add(REPLACE_SPLIT_LOWER);
		REFERENCE_PROPOSALS.add(LOWER);
		REFERENCE_PROPOSALS.add(UPPER);
		REFERENCE_PROPOSALS.add(SPLIT_LOWER);
		REFERENCE_PROPOSALS.add(SPLIT_UPPER);
		REFERENCE_PROPOSALS.add(PARENT);
		REFERENCE_PROPOSALS.add(TYPE);
		REFERENCE_PROPOSALS.add(DECLARING_TYPE);
		REFERENCE_PROPOSALS.add(STATIC);
		REFERENCE_PROPOSALS.add(FINAL);
		REFERENCE_PROPOSALS.add(LENGTH);
		REFERENCE_PROPOSALS.add(CHAR_AT);
		REFERENCE_PROPOSALS.add(IS_UPPER);
		REFERENCE_PROPOSALS.add(IS_LOWER);
		REFERENCE_PROPOSALS.add(IS_LETTER);
		REFERENCE_PROPOSALS.add(IS_DIGIT);
		REFERENCE_PROPOSALS.add(IS_LETTER_OR_DIGIT);
		REFERENCE_PROPOSALS.add(HAS_ANNOTATION);
		REFERENCE_PROPOSALS.add(GET_ANNOTATION);
		REFERENCE_PROPOSALS.add(GET_ANNOTATION_VALUE);

		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_USER, VelocityTemplateManager.KEY_USER + " - ", "Current user"));
		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_DATE, VelocityTemplateManager.KEY_DATE + " - ", "Current date"));
		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_TIME, VelocityTemplateManager.KEY_TIME + " - ", "Current time"));
		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_YEAR, VelocityTemplateManager.KEY_YEAR + " - ", "Current year"));

		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_PROJECT, VelocityTemplateManager.KEY_PROJECT + " - ", "Project name"));
		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_PACKAGE, VelocityTemplateManager.KEY_PACKAGE + " - ", "Package name"));
		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_FILE, 	 VelocityTemplateManager.KEY_FILE 	 + "    - ", "File name"));
		BASE_PROPERTIES.add(new ElementProposal(
				VelocityTemplateManager.KEY_TYPE, 	 VelocityTemplateManager.KEY_TYPE 	 + "    - ", "Primary type name"));
	}


	/**
	 * Gets the completion proposals for the given element.
	 *
	 * @param element the element
	 * @param offset the offset
	 * @param length the length
	 * @param properties the properties
	 * @param propertiesOnly true, if properties only should be suggested
	 *
	 * @return the list of proposals
	 */
	public static List<ICompletionProposal> getCompletionProposals(String element, int offset,
			int length, Map<String, String> properties, boolean propertiesOnly) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		boolean silent = false;
		if (element.startsWith("$!{")) {
			silent = true;
			element = element.substring(3);
		}
		else if (element.startsWith("$!")) {
			silent = true;
			element = element.substring(2);
		}
		else if (element.startsWith("${")) {
			element = element.substring(2);
		}
		else if (element.startsWith("$")) {
			element = element.substring(1);
		}
		else {
			return proposals;
		}

		if (element.indexOf('.') == -1) {
			return getElementProposals(element, offset, length, silent, properties, propertiesOnly);
		}
		else if (!propertiesOnly){
			return getFunctionProposals(element, offset, length, silent);
		}

		return proposals;
	}

	/**
	 * Gets the description for the given element.
	 *
	 * @param element the element
	 *
	 * @return the element description
	 */
	public static String getElementDescription(String element, Map<String, String> properties) {
		ElementProposal referenceProposal = getMatchingProposal(element, REFERENCE_PROPOSALS);
		if (referenceProposal != null) {
			return referenceProposal.getDescription();
		}

		referenceProposal = getMatchingProposal(element, BASE_PROPERTIES);
		if (referenceProposal != null) {
			return referenceProposal.getDescription();
		}

		// try to get property
		return properties.get(element);
	}

	/**
	 * Gets the element proposals for the given element.
	 *
	 * @param element the element
	 * @param offset the offset
	 * @param length the length
	 * @param silent true for silent references ($!)
	 * @param propertiesOnly true, if properties only should be suggested
	 *
	 * @return list of element proposals
	 */
	private static List<ICompletionProposal> getElementProposals(String element, int offset,
			int length, boolean silent, Map<String, String> properties, boolean propertiesOnly) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		if (!propertiesOnly
				&& (element.length() == 0 || ELEMENT.getProposal().startsWith(element))) {
			proposals.add(createTemplateProposal(ELEMENT, offset, length, silent));
		}

		if (element.length() == 0 || PROPERTIES.getProposal().startsWith(element)) {
			proposals.add(createTemplateProposal(PROPERTIES, offset, length, silent));
		}

		Iterator<ElementProposal> iter = BASE_PROPERTIES.iterator();
		while (iter.hasNext()) {
			ElementProposal propertyProposal = iter.next();
			if (element.length() == 0 || propertyProposal.getProposal().startsWith(element)) {
				proposals.add(createTemplateProposal(propertyProposal, offset, length, silent));
			}
		}

		Iterator<String> iter2 = new TreeSet<String>(properties.keySet()).iterator(); // sorted
		while (iter2.hasNext()) {
			String key = iter2.next();
			if (element.length() == 0 || key.startsWith(element)) {
				proposals.add(createTemplateProposal(new ElementProposal(
						key, key + " - ", "Property"), offset, length, silent));
			}
		}

		return proposals;
	}

	/**
	 * Gets the function proposals for the given element.
	 *
	 * @param element the element
	 * @param offset the offset
	 * @param length the length
	 * @param silent true for silent references ($!)
	 *
	 * @return list of function proposals
	 */
	private static List<ICompletionProposal> getFunctionProposals(String element, int offset, int length, boolean silent) {
		List<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();

		String[] tokens = element.split("\\.");
		if (tokens.length == 0 || !tokens[0].equals(VelocityTemplateManager.KEY_ELEMENT)) {
			return proposals;
		}

		int level = (element.endsWith(".") ? tokens.length : tokens.length - 1);
		Set<FunctionProposal> functionProposals = getValidFunctionProposals(
				getMatchingProposal(tokens[level - 1], REFERENCE_PROPOSALS));

		for (int i = 1; i < level - 1; ++i) {
			ElementProposal rp = getMatchingProposal(tokens[i], REFERENCE_PROPOSALS);
			if (rp == null) {
				return proposals; // invalid reference -> empty proposals
			}
			else if (!rp.isMultiple()){
				functionProposals.remove(rp); // alread used
			}
		}

		String functionStart = (element.endsWith(".") ? "" : tokens[tokens.length - 1]);

		Iterator<FunctionProposal> iter = functionProposals.iterator();
		while (iter.hasNext()) {
			FunctionProposal function = iter.next();
			if (functionStart.length() == 0 || function.getProposal().startsWith(functionStart)) {
				String elementStart = element.substring(0, element.lastIndexOf('.'));
				proposals.add(createTemplateProposal(elementStart + ".", function, offset, length, silent));
			}
		}

		return proposals;
	}

	/**
	 * Gets the valid function proposals for the given predecessor.
	 *
	 * @param predecessor the predecessor
	 *
	 * @return the valid function proposals
	 */
	private static Set<FunctionProposal> getValidFunctionProposals(ElementProposal predecessor) {
		Set<FunctionProposal> functionProposals = new LinkedHashSet<FunctionProposal>();

		if (predecessor == null) {
			return functionProposals;
		}

		Iterator<ElementProposal> iter = REFERENCE_PROPOSALS.iterator();
		while (iter.hasNext()) {
			ElementProposal fp = iter.next();
			if (fp instanceof FunctionProposal &&
					((FunctionProposal)fp).isPredecessor(predecessor)) {
				functionProposals.add((FunctionProposal)fp);
			}
		}

		return functionProposals;
	}

	/**
	 * Gets the matching proposals for the given token.
	 *
	 * @param token the token
	 * @param proposals list of possible proposals
	 *
	 * @return the matching proposal
	 */
	private static ElementProposal getMatchingProposal(String token, List<ElementProposal> proposals) {
		ElementProposal referenceProposal = null;

		Iterator<ElementProposal> iter = proposals.iterator();
		while (iter.hasNext()) {
			ElementProposal fp = iter.next();
			if (fp.matches(token)) {
				referenceProposal = fp;
				break;
			}
		}

		return referenceProposal;
	}

	private static String createReferenceString(String element, boolean silent) {
		return (silent ? "$!{" : "${") + element + "}";
	}

	private static TemplateProposal createTemplateProposal(
			ElementProposal referenceProposal, int offset, int length, boolean silent) {
		return createTemplateProposal("", referenceProposal, offset, length, silent);
	}

	private static TemplateProposal createTemplateProposal(String prefix,
			ElementProposal referenceProposal, int offset, int length, boolean silent) {

		String proposal = createReferenceString(prefix + referenceProposal.getProposal(), silent);

		return new TemplateProposal(proposal, referenceProposal.getDisplayString(),
				offset, length, new Point(
						offset + proposal.length() + referenceProposal.getSelectionOffset(),
						referenceProposal.getSelectionLength()));
	}
}
