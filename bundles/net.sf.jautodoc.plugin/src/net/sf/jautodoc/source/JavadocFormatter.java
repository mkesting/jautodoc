/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jdt.internal.core.util.SimpleDocument;
import org.eclipse.jdt.internal.formatter.DefaultCodeFormatter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.utils.StringUtils;

/**
 * Wrapper for Eclipse code formatter.
 */
@SuppressWarnings("restriction")
public class JavadocFormatter {
	
	private static final int DEFAULT_TAB_SIZE = 4;
	
	private static JavadocFormatter instance;
	
	private ICompilationUnit compUnit;
	private IJavaProject currProject;
	private CodeFormatter formatter;
	
	@SuppressWarnings("rawtypes")
	private Map options;
	private int tabSize;
	
	
	/**
	 * Gets the single instance of JavadocFormatter.
	 * 
	 * @return single instance of JavadocFormatter
	 */
	public static JavadocFormatter getInstance() {
		if (instance == null) {
			instance = new JavadocFormatter();
		}
		return instance;
	}
	
	// prevent instantiation
	private JavadocFormatter() {
	}
	
	/**
	 * Start formatting.
	 * 
	 * @param compUnit the current compilation unit
	 */
	@SuppressWarnings("unchecked")
	public void startFormatting(ICompilationUnit compUnit) {
		this.compUnit = compUnit;
		
		if (currProject == null || !currProject.equals(compUnit.getJavaProject())) {
			currProject  = compUnit.getJavaProject();
			options		 = currProject.getOptions(true);
			formatter    = new DefaultCodeFormatter(options);
			tabSize		 = getTabSize();
		}
	}
	
	/**
	 * Stop formatting.
	 */
	public void stopFormatting() {
		compUnit 	= null;
		currProject = null;
		formatter   = null;
		options	    = null;
	}
	
	/**
	 * Formats the given comment.
	 * 
	 * @param comment the Javadoc comment
	 * @param indent the current indent
	 * @param lineSeparator the line separator
	 * 
	 * @return the formatted comment
	 */
	public String format(String comment, String indent, String lineSeparator) {
	    comment = indent + comment; // Hack for Eclipse 4.5 RC3 (indentationLevel seems to be ignored) + trim() on return
	    
		String    result   = comment;
		IDocument document = new SimpleDocument(comment);
		
		int indentationLevel= StringUtils.inferIndentationLevel(indent, tabSize);
		TextEdit textEdit = formatter.format(CodeFormatter.K_JAVA_DOC,
											 comment,
											 0,
											 comment.length(),
											 indentationLevel,
											 lineSeparator);
		
		try {
			textEdit.apply(document, TextEdit.NONE);
			result = document.get();
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(compUnit, e);
		}
		
		return result.trim();
	}
	
	/**
	 * Returns the value of DefaultCodeFormatterConstants#FORMATTER_TAB_SIZE
	 * from the current preferences.
	 * 
	 * @return the value of DefaultCodeFormatterConstants#FORMATTER_TAB_SIZE
	 * from the current preferences
	 */
	private int getTabSize() {
		if (options != null &&
			options.containsKey(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE)) {
			try {
				return Integer.parseInt(options.get(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE).toString());
			} catch (Exception e) {
				// use default
			}
		}
		return DEFAULT_TAB_SIZE;
	}
}
