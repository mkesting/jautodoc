/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.rules;

import org.eclipse.jface.text.rules.IWordDetector;


/**
 * Detector for empty Velocity comments.
 */
class EmptyCommentDetector implements IWordDetector {

	/*
	 * @see IWordDetector#isWordStart
	 */
	public boolean isWordStart(char c) {
		return (c == '#');
	}

	/*
	 * @see IWordDetector#isWordPart
	 */
	public boolean isWordPart(char c) {
		return (c == '*' || c == '#');
	}
}
