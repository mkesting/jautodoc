/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.internal.text.InformationControlReplacer;
import org.eclipse.jface.text.AbstractHoverInformationControlManager;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Shell;

/**
 * Under construction...
 */
@SuppressWarnings("restriction")
public class HoverInformationControlManager extends AbstractHoverInformationControlManager {

    private final ColumnViewer viewer;

    public HoverInformationControlManager(final ColumnViewer viewer) {
        super(new DefaultInformationControlCreator());
        setAnchor(ANCHOR_TOP);

        this.viewer = viewer;
        install(viewer.getControl());
    }

    @Override
    protected void computeInformation() {
        final ViewerCell cell = viewer.getCell(getHoverEventLocation());
        if (cell == null) {
            setInformation(null, null);
            return;
        }

        final Object element = cell.getElement();
        if (!(element instanceof IMember)) {
            setInformation(null, null);
            return;
        }

        final IMember member = (IMember)element;
//        String rawJavadoc = null;
//        try {
////            Reader contentReader= JavadocContentAccess.getHTMLContentReader(member, false, false);
//            Reader contentReader= JavadocContentAccess.getContentReader(member, false);
//            if (contentReader != null)
//                rawJavadoc = getString(contentReader).replaceAll("\n", "<br/>");
//        } catch (JavaModelException e) {
//            e.printStackTrace();
//        }

        String rawJavadoc= getJavadocSource(member);
        if (rawJavadoc == null) {
            setInformation(null, null);
            return;
        }

        rawJavadoc = rawJavadoc.replaceAll("\r\n", "<br/>");

        setInformation(rawJavadoc, cell.getBounds());
    }

    @SuppressWarnings("unused")
    private static String getString(Reader reader) {
        StringBuffer buf= new StringBuffer();
        char[] buffer= new char[1024];
        int count;
        try {
            while ((count= reader.read(buffer)) != -1)
                buf.append(buffer, 0, count);
        } catch (IOException e) {
            return null;
        }
        return buf.toString();
    }

    public void installInformationControlReplacer(final InformationControlReplacer replacer) {
        getInternalAccessor().setInformationControlReplacer(replacer);
    }

    private String getJavadocSource(final IMember member) {
        try {
            final IBuffer buf= member.getOpenable().getBuffer();
            if (buf != null) {
                ISourceRange javadocRange= member.getJavadocRange();
                if (javadocRange != null) {
                    String rawJavadoc= buf.getText(javadocRange.getOffset(), javadocRange.getLength());
                    return rawJavadoc;
                }
            }
        } catch (JavaModelException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static class DefaultInformationControlCreator extends AbstractReusableInformationControlCreator {
        public IInformationControl doCreateInformationControl(Shell shell) {
            return new DefaultInformationControl(shell, "Do Something");
        }
    }
}
