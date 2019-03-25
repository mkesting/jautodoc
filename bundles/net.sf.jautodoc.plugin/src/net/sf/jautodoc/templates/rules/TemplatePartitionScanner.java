/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;


/**
 * Partition scanner for templates.
 */
public class TemplatePartitionScanner extends RuleBasedPartitionScanner {

	/**
	 * Creates the partitioner and sets up the appropriate rules.
	 * 
	 * @param partitioningRules the partitioning rules
	 */
	public TemplatePartitionScanner(IPredicateRule[] partitioningRules) {
		setPredicateRules(partitioningRules);
	}
}
