/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.velocity.contentassist;

import net.sf.jautodoc.utils.Utils;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;


/**
 * Directives and Velocity comments should start at beginning of line. This
 * strategy removes the Javadoc indent &quot; * &quot;.
 */
public class DirectiveAndCommentStartStrategy implements IAutoEditStrategy {
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IAutoEditStrategy#customizeDocumentCommand(org.eclipse.jface.text.IDocument, org.eclipse.jface.text.DocumentCommand)
	 */
	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (d.getLength() == 0 ||
				c.offset == -1 || c.length > 0 || c.text == null || c.text.length() != 1) {
			return;
		}
		
		if (c.text.charAt(0) != '#') {
			return;
		}
		
		String partition = Utils.getPartition(d, c.offset);
		if (!IDocument.DEFAULT_CONTENT_TYPE.equals(partition)) {
			return;
		}
		
		try {
		    // escaped directive?
		    if (c.offset > 0 && d.getChar(c.offset - 1) == '\\') {
	            return;
	        }
		    
			// find start of line
			int p = (c.offset == d.getLength() ? c.offset  - 1 : c.offset);
			IRegion info = d.getLineInformationOfOffset(p);
			int start = info.getOffset();
			p = (c.offset == d.getLength() ? p : p - 1);
			
			// only ws and non char or digit at start of line?
			while (start <= p) {
				char ch = d.getChar(p);
				if (ch != ' ' && ch != '\t'
						&& (ch == '#' || Character.isLetterOrDigit(ch))) {
					break;
				}
				--p;
			}
			
			if (p < start) {
				// yes -> replace up to start of line
				c.length = c.offset - start;
				c.offset = start;
			}
		} catch (BadLocationException e) {/* ignore */}
	}
}
