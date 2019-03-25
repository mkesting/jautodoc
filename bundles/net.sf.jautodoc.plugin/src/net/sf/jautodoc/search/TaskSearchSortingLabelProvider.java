/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.viewers.StyledString;

/**
 * Label provider for the search result table viewer.
 */
public class TaskSearchSortingLabelProvider extends TaskSearchLabelProvider {

    public static final int SHOW_ELEMENT_CONTAINER = 1; // default
    public static final int SHOW_CONTAINER_ELEMENT = 2;
    public static final int SHOW_PATH = 3;

    private static final long FLAGS_QUALIFIED = DEFAULT_SEARCH_TEXTFLAGS | JavaElementLabels.F_FULLY_QUALIFIED
            | JavaElementLabels.M_FULLY_QUALIFIED | JavaElementLabels.I_FULLY_QUALIFIED
            | JavaElementLabels.T_FULLY_QUALIFIED | JavaElementLabels.D_QUALIFIED | JavaElementLabels.CF_QUALIFIED
            | JavaElementLabels.CU_QUALIFIED | JavaElementLabels.COLORIZE;

    private int currentOrder;

    public TaskSearchSortingLabelProvider(final TaskSearchResultPage resultPage) {
        super(resultPage);
        this.currentOrder = SHOW_ELEMENT_CONTAINER;
    }

    @SuppressWarnings("restriction")
    public final String getText(Object element) {
        if (element instanceof IImportDeclaration) {
            element = ((IImportDeclaration) element).getParent().getParent();
        }

        final String text = super.getText(element);
        if (text.length() > 0) {
            String labelWithCount = getLabelWithCounts(element, text);
            if (currentOrder == SHOW_ELEMENT_CONTAINER) {
                labelWithCount += getPostQualification(element);
            }
            return labelWithCount;
        }
        return text;
    }

    @SuppressWarnings("restriction")
    public StyledString getStyledText(Object element) {
        if (element instanceof IImportDeclaration) {
            element = ((IImportDeclaration) element).getParent().getParent();
        }

        final StyledString text = super.getStyledText(element);
        if (text.length() > 0) {
            StyledString countLabel = getColoredLabelWithCounts(element, text);
            if (currentOrder == SHOW_ELEMENT_CONTAINER) {
                countLabel.append(getPostQualification(element), StyledString.QUALIFIER_STYLER);
            }
            return countLabel;
        }
        return text;
    }

    @SuppressWarnings("restriction")
    public void setOrder(final int orderFlag) {
        long flags = 0;
        if (orderFlag == SHOW_ELEMENT_CONTAINER) {
            flags = DEFAULT_SEARCH_TEXTFLAGS;
        }
        else if (orderFlag == SHOW_CONTAINER_ELEMENT) {
            flags = FLAGS_QUALIFIED;
        }
        else if (orderFlag == SHOW_PATH) {
            flags = FLAGS_QUALIFIED | JavaElementLabels.PREPEND_ROOT_PATH;
        }
        setTextFlags(flags);
        currentOrder = orderFlag;
    }

    private String getPostQualification(final Object element) {
        final String textLabel = JavaElementLabels.getTextLabel(element, JavaElementLabels.ALL_POST_QUALIFIED);
        final int indexOf = textLabel.indexOf(JavaElementLabels.CONCAT_STRING);
        if (indexOf != -1) {
            return textLabel.substring(indexOf);
        }
        return new String();
    }
}
