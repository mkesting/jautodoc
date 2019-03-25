/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

/**
 * Abstract base class for label provider of tree and table viewer.
 */
@SuppressWarnings("restriction")
public abstract class TaskSearchLabelProvider extends AppearanceAwareLabelProvider {

    public static final String PROPERTY_MATCH_COUNT = "net.sf.jautodoc.search.matchCount"; //$NON-NLS-1$

    protected static final long DEFAULT_SEARCH_TEXTFLAGS = (DEFAULT_TEXTFLAGS | JavaElementLabels.P_COMPRESSED)
            & ~JavaElementLabels.M_APP_RETURNTYPE;
    protected static final int DEFAULT_SEARCH_IMAGEFLAGS = DEFAULT_IMAGEFLAGS;


    protected TaskSearchResultPage resultPage;


    protected TaskSearchLabelProvider(final TaskSearchResultPage resultPage) {
        super(DEFAULT_SEARCH_TEXTFLAGS, DEFAULT_SEARCH_IMAGEFLAGS);

        this.resultPage = resultPage;
        addLabelDecorator(new ProblemsLabelDecorator(null));
    }

    protected final StyledString getColoredLabelWithCounts(final Object element, final StyledString coloredName) {
        final String name = coloredName.getString();
        final String decorated = getLabelWithCounts(element, name);
        if (decorated.length() > name.length()) {
            styleDecoratedString(decorated, StyledString.COUNTER_STYLER, coloredName);
        }
        return coloredName;
    }

    protected final String getLabelWithCounts(final Object element, final String elementName) {
        final int findingCount = resultPage.getDisplayedFindingsCount(element);
        if (findingCount < 2) {
            if (findingCount == 1 && hasChildren(element)) {
                return elementName + " (1 finding)";
            }
            else {
                return elementName;
            }
        }
        else {
            return elementName + " (" + String.valueOf(findingCount) + " findings)";
        }
    }

    protected boolean hasChildren(Object elem) {
        return false;
    }

    public boolean isLabelProperty(final Object element, final String property) {
        if (PROPERTY_MATCH_COUNT.equals(property)) {
            return true;
        }
        return super.isLabelProperty(element, property);
    }

    // Copied from org.eclipse.jface.viewers.StyledCellLabelProvider for compatibility of JAutodoc with Eclipse 3.4
    /**
     * Applies decoration styles to the decorated string and adds the styles of the previously
     * undecorated string.
     * <p>
     * If the <code>decoratedString</code> contains the <code>styledString</code>, then the result
     * keeps the styles of the <code>styledString</code> and styles the decorations with the
     * <code>decorationStyler</code>. Otherwise, the decorated string is returned without any
     * styles.
     *
     * @param decoratedString the decorated string
     * @param decorationStyler the styler to use for the decoration or <code>null</code> for no
     *            styles
     * @param styledString the original styled string
     *
     * @return the styled decorated string (can be the given <code>styledString</code>)
     * @since 3.5
     */
    private StyledString styleDecoratedString(String decoratedString, Styler decorationStyler, StyledString styledString) {
        String label= styledString.getString();
        int originalStart= decoratedString.indexOf(label);
        if (originalStart == -1) {
            return new StyledString(decoratedString); // the decorator did something wild
        }

        if (decoratedString.length() == label.length())
            return styledString;

        if (originalStart > 0) {
            StyledString newString= new StyledString(decoratedString.substring(0, originalStart), decorationStyler);
            newString.append(styledString);
            styledString= newString;
        }
        if (decoratedString.length() > originalStart + label.length()) { // decorator appended something
            return styledString.append(decoratedString.substring(originalStart + label.length()), decorationStyler);
        }
        return styledString; // no change
    }
}
