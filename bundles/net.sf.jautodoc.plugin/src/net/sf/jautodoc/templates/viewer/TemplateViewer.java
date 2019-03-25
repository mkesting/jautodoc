/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.viewer;

import java.util.HashMap;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.templates.rules.ITemplatePartitions;
import net.sf.jautodoc.templates.rules.TemplatePartitionScanner;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;


/**
 * Viewer for Javadoc templates.
 */
public class TemplateViewer extends SourceViewer {

	/**
	 * Instantiates a new template viewer.
	 * 
	 * @param parent the parent
	 * @param styles the styles
	 */
	public TemplateViewer(Composite parent, int styles) {
		super(parent, null, styles);
		
		Font font= JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
		getTextWidget().setFont(font);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewer#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {
		if (document != null) {
			IPredicateRule[] partitioningRules = JAutodocPlugin.getContext()
					.getTemplatePartitioningRules();
			
			Map<String, IDocumentPartitioner> partitioners = new HashMap<String, IDocumentPartitioner>();
			partitioners.put(ITemplatePartitions.TEMPLATE_PARTITIONING,
					new FastPartitioner(new TemplatePartitionScanner(
							partitioningRules), new String[] {
							ITemplatePartitions.SINGLE_LINE_COMMENT,
							ITemplatePartitions.MULTI_LINE_COMMENT }));
			
			TextUtilities.addDocumentPartitioners(document, partitioners);
		}
		super.setDocument(document);
	}
}
