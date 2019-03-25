/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import org.eclipse.jface.text.rules.ICharacterScanner;


/**
 * Resettable scanner that will forward calls to
 * a given scanner, but stores a marked position.
 */
public class ResettableScanner implements ICharacterScanner {
	private int readCount;
	private ICharacterScanner delegate;
	

	/**
	 * Instantiates a new resettable scanner.
	 */
	public ResettableScanner() {
		readCount = 0;
		delegate = null;
	}
	
	/**
	 * Instantiates a new resettable scanner.
	 * 
	 * @param scanner the delegation scanner
	 */
	public ResettableScanner(ICharacterScanner scanner) {
		setScanner(scanner);
	}

	/**
	 * Sets the delegation scanner.
	 * 
	 * @param scanner the delegation scanner
	 */
	public void setScanner(ICharacterScanner scanner) {
		delegate = scanner;
		mark();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getColumn()
	 */
	public int getColumn() {
		return delegate.getColumn();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#getLegalLineDelimiters()
	 */
	public char[][] getLegalLineDelimiters() {
		return delegate.getLegalLineDelimiters();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#read()
	 */
	public int read() {
		int ch = delegate.read();
		if (ch != ICharacterScanner.EOF) {
			++readCount;
		}
		return ch;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.rules.ICharacterScanner#unread()
	 */
	public void unread() {
		if (readCount > 0) readCount--;
		delegate.unread();
	}

	/**
	 * Marks an offset in the scanned content.
	 */
	public void mark() {
		readCount= 0;
	}

	/**
	 * Resets the scanner to the marked position.
	 */
	public void reset() {
		while (readCount > 0) unread();
		while (readCount < 0) read();
	}

	/**
	 * Gets the read count.
	 * 
	 * @return the read count
	 */
	public int getReadCount() {
		return readCount;
	}
}
