/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;


/**
 * Defines constants for valid template kinds.
 */
public interface ITemplateKinds {
	public static final int UNKNOWN		= 0;
	public static final int TYPE		= 1;
	public static final int FIELD		= 2;
	public static final int METHOD		= 3;
	public static final int PARAMETER	= 4;
	public static final int EXCEPTION	= 5;
}
