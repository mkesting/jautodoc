/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;


public class TextEditHelper {

	private TextEdit textEdit;
	private IDocument document;
	private DocumentRewriteSession rewriteSession;

	public TextEditHelper(IDocument document, TextEdit textEdit) {
		this.document = document;
		this.textEdit = textEdit;
	}

	public void apply() throws MalformedTreeException, BadLocationException {
		apply(TextEdit.CREATE_UNDO | TextEdit.UPDATE_REGIONS);
	}

	public void apply(int style) throws MalformedTreeException, BadLocationException {
		Map<String, IDocumentPartitioner> stateData = null;
		try {
			stateData = startSequentialRewriteMode();
			textEdit.apply(document, style);
		} finally {
			stopSequentialRewriteMode(stateData);
		}
	}

	private Map<String, IDocumentPartitioner> startSequentialRewriteMode() {
    	Map<String, IDocumentPartitioner> stateData = null;
		if (document instanceof IDocumentExtension4) {
			IDocumentExtension4 extension= (IDocumentExtension4) document;
			rewriteSession = extension.startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
		}
		else {
			stateData = TextUtilities.removeDocumentPartitioners(document);
		}

		return stateData;
	}

	private void stopSequentialRewriteMode(Map<String, IDocumentPartitioner> stateData) {
		if (document instanceof IDocumentExtension4) {
			IDocumentExtension4 extension= (IDocumentExtension4) document;
			extension.stopRewriteSession(rewriteSession);
		}
		else {
			TextUtilities.addDocumentPartitioners(document, stateData);
		}
	}
}
