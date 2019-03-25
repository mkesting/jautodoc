/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledString;

/**
 * Label provider for the search result tree viewer.
 */
public class TaskSearchPostfixLabelProvider extends TaskSearchLabelProvider {

    private ITreeContentProvider contentProvider;

    public TaskSearchPostfixLabelProvider(TaskSearchResultPage resultPage) {
        super(resultPage);
        this.contentProvider = new TaskSearchTreeContentProvider.FastJavaElementProvider();
    }

    public String getText(Object element) {
        String labelWithCounts = getLabelWithCounts(element, internalGetText(element));
        return labelWithCounts + getQualification(element);
    }

    private String getQualification(final Object element) {
        final StringBuffer res = new StringBuffer();

        final ITreeContentProvider provider = (ITreeContentProvider) resultPage.getViewer().getContentProvider();
        final Object visibleParent = provider.getParent(element);

        Object realParent = contentProvider.getParent(element);
        Object lastElement = element;

        while (realParent != null && !(realParent instanceof IJavaModel) && !realParent.equals(visibleParent)) {
            if (!isSameInformation(realParent, lastElement)) {
                res.append(JavaElementLabels.CONCAT_STRING).append(internalGetText(realParent));
            }
            lastElement = realParent;
            realParent = contentProvider.getParent(realParent);
        }
        return res.toString();
    }

    protected boolean hasChildren(final Object element) {
        final ITreeContentProvider contentProvider = (ITreeContentProvider) resultPage.getViewer().getContentProvider();
        return contentProvider.hasChildren(element);
    }

    @SuppressWarnings("restriction")
    private String internalGetText(final Object element) {
        return super.getText(element);
    }

    @SuppressWarnings("restriction")
    private StyledString internalGetStyledText(final Object element) {
       return super.getStyledText(element);
    }

    private boolean isSameInformation(final Object realParent, final Object lastElement) {
        if (lastElement instanceof IType) {
            final IType type = (IType) lastElement;
            if (realParent instanceof IClassFile) {
                if (type.getClassFile().equals(realParent)) {
                    return true;
                }
            }
            else if (realParent instanceof ICompilationUnit) {
                if (type.getCompilationUnit().equals(realParent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.ui.viewsupport.JavaUILabelProvider#getStyledText(java.lang.Object)
     */
    public StyledString getStyledText(final Object element) {
        final StyledString styledString = getColoredLabelWithCounts(element, internalGetStyledText(element));
        styledString.append(getQualification(element), StyledString.QUALIFIER_STYLER);
        return styledString;
    }
}
