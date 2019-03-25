/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

/**
 * Abstract base class for table and tree content provider of search result view.
 */
public abstract class TaskSearchContentProvider implements IStructuredContentProvider {
    protected final Object[] EMPTY_ARR = new Object[0];

    private final TaskSearchResultPage resultPage;
    private AbstractTextSearchResult searchResult;


    public TaskSearchContentProvider(final TaskSearchResultPage resultPage) {
        this.resultPage= resultPage;
    }

    public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        initialize((AbstractTextSearchResult) newInput);

    }

    protected void initialize(final AbstractTextSearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public abstract void elementsChanged(Object[] updatedElements);
    public abstract void clear();

    public void dispose() {
        // nothing to do
    }

    public TaskSearchResultPage getResultPage() {
        return resultPage;
    }

    public AbstractTextSearchResult getSearchResult() {
        return searchResult;
    }
}
