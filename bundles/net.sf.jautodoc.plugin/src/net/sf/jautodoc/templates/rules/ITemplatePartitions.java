/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.rules;


/**
 * Definition of Velocity template partitioning and its partitions.
 */
public interface ITemplatePartitions {
	
	/**
	 * Identifier of the Velocity template partitioning.
	 */
	public static final String TEMPLATE_PARTITIONING = "___template_partitioning";  //$NON-NLS-1$
	
	/**
	 * Identifier of the Velocity template single line comment partition.
	 */
	public static final String SINGLE_LINE_COMMENT 	= "__singleline_comment";  //$NON-NLS-1$
	
	/**
	 * Identifier of the Velocity template multi line comment partition.
	 */
	public static final String MULTI_LINE_COMMENT 	= "__multiline_comment";  //$NON-NLS-1$
}
