/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.swt.widgets.Table;

/**
 * Content provider for the search result table viewer.
 */
public class TaskSearchTableContentProvider extends TaskSearchContentProvider {

    public TaskSearchTableContentProvider(final TaskSearchResultPage resultPage) {
        super(resultPage);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(final Object inputElement) {
        if (inputElement instanceof AbstractTextSearchResult) {
            final Set<Object> filteredElements = new HashSet<Object>();
            final int limit = getResultPage().getElementLimit().intValue();
            final Object[] rawElements = ((AbstractTextSearchResult) inputElement).getElements();

            for (Object element : rawElements) {
                if (getResultPage().getDisplayedFindingsCount(element) > 0) {
                    filteredElements.add(element);
                    if (limit != -1 && filteredElements.size() >= limit) {
                        break;
                    }
                }
            }
            return filteredElements.toArray();
        }
        return EMPTY_ARR;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.search.TaskSearchContentProvider#elementsChanged(java.lang.Object[])
     */
    public void elementsChanged(final Object[] updatedElements) {
        if (getSearchResult() == null) {
            return;
        }

        int addLimit = getAddLimit();

        final Set<Object> added = new HashSet<Object>();
        final Set<Object> updated = new HashSet<Object>();
        final Set<Object> removed = new HashSet<Object>();

        final TableViewer viewer = (TableViewer) getResultPage().getViewer();

        for (Object element : updatedElements) {
            if (getResultPage().getDisplayedFindingsCount(element) > 0) {
                if (viewer.testFindItem(element) != null) {
                    updated.add(element);
                }
                else {
                    if (addLimit > 0) {
                        added.add(element);
                        addLimit--;
                    }
                }
            }
            else {
                removed.add(element);
            }
        }

        viewer.add(added.toArray());
        viewer.remove(removed.toArray());
        viewer.update(updated.toArray(), new String[] { TaskSearchLabelProvider.PROPERTY_MATCH_COUNT });
    }

    private int getAddLimit() {
        final int limit = getResultPage().getElementLimit().intValue();
        if (limit != -1) {
            final Table table = (Table) getResultPage().getViewer().getControl();
            final int itemCount = table.getItemCount();
            if (itemCount >= limit) {
                return 0;
            }
            return limit - itemCount;
        }
        return Integer.MAX_VALUE;
    }

    public void clear() {
        getResultPage().getViewer().refresh();
    }
}
