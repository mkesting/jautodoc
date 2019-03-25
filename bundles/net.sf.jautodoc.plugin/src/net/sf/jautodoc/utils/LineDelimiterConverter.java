/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;


public class LineDelimiterConverter {

	public static String convert(String text, String newDelimiter)
			throws BadLocationException {
		return convert(new Document(text), newDelimiter).get();
	}
	
	public static IDocument convert(IDocument document, String newDelimiter)
			throws BadLocationException {
		MultiTextEdit textEdit = new MultiTextEdit();
		
		int lineCount= document.getNumberOfLines();
		for (int i= 0; i < lineCount; i++) {
			String delimiter = document.getLineDelimiter(i);
			if (delimiter == null || delimiter.length() == 0
					|| delimiter.equals(newDelimiter)) {
				continue;
			}
				
			IRegion region = document.getLineInformation(i);
			textEdit.addChild(new ReplaceEdit(region.getOffset() + region.getLength(),
					delimiter.length(), newDelimiter));
		}
		
		if (textEdit.hasChildren()) {
			new TextEditHelper(document, textEdit).apply(TextEdit.NONE);
		}
		
		return document;
	}
}
