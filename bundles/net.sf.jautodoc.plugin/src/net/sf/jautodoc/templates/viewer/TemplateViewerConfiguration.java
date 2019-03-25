/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.viewer;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.rules.ITemplatePartitions;
import net.sf.jautodoc.templates.rules.SingleTokenScanner;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


/**
 * Viewer configuration for Javadoc templates.
 */
public class TemplateViewerConfiguration extends SourceViewerConfiguration {
	
	private ITextHover				textHover;
	private ITokenScanner 			tokenScanner;
	private IAutoEditStrategy[]		autoEditStrategies;
	private IContentAssistProcessor assistProcessor;
	

	/**
	 * Instantiates a new template viewer configuration.
	 * 
	 * @param tokenScanner the token scanner
	 * @param textHover the text hover
	 */
	public TemplateViewerConfiguration(ITokenScanner tokenScanner, ITextHover textHover) {
		this(tokenScanner, textHover, null, null);
	}
	
	/**
	 * Instantiates a new template viewer configuration.
	 * 
	 * @param tokenScanner the token scanner
	 * @param textHover the text hover
	 * @param autoEditStrategies the auto edit strategies
	 * @param assistProcessor the assist processor
	 */
	public TemplateViewerConfiguration(ITokenScanner tokenScanner, ITextHover textHover,
			IAutoEditStrategy[] autoEditStrategies,
			IContentAssistProcessor assistProcessor) {
		this.textHover		 	= textHover;
		this.tokenScanner	 	= tokenScanner;
		this.assistProcessor 	= assistProcessor;
		this.autoEditStrategies = autoEditStrategies;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredContentTypes(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
							  ITemplatePartitions.SINGLE_LINE_COMMENT,
							  ITemplatePartitions.MULTI_LINE_COMMENT};
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		return autoEditStrategies;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getContentAssistant(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if (assistProcessor == null) return null;
		
		ContentAssistant assistant= new ContentAssistant();
		assistant.setDocumentPartitioning(ITemplatePartitions.TEMPLATE_PARTITIONING);
		
		assistant.setContentAssistProcessor(assistProcessor, IDocument.DEFAULT_CONTENT_TYPE);

		assistant.enableAutoInsert(true);
		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(200);
		
		return assistant;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer, java.lang.String)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) {
			return textHover;
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getPresentationReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler rec = new PresentationReconciler();
		rec.setDocumentPartitioning(getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(tokenScanner);
		rec.setDamager (dr, IDocument.DEFAULT_CONTENT_TYPE);
		rec.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		IToken commentToken = ResourceManager.getToken(ResourceManager.COMMENT);
		
		dr = new DefaultDamagerRepairer(new SingleTokenScanner(commentToken));
		rec.setDamager (dr, ITemplatePartitions.SINGLE_LINE_COMMENT);
		rec.setRepairer(dr, ITemplatePartitions.SINGLE_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(new SingleTokenScanner(commentToken));
		rec.setDamager (dr, ITemplatePartitions.MULTI_LINE_COMMENT);
		rec.setRepairer(dr, ITemplatePartitions.MULTI_LINE_COMMENT);
		
		return rec;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getConfiguredDocumentPartitioning(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer) {
		return ITemplatePartitions.TEMPLATE_PARTITIONING;
	}
}
