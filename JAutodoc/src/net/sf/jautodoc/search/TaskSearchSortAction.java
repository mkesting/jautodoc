/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.custom.BusyIndicator;

/**
 * Action for sorting search results by path, name or parent name.
 */
public class TaskSearchSortAction extends Action {
    private final int sortOrder;
    private final TaskSearchResultPage resultPage;

    /**
     * Instantiates a new task sort action.
     *
     * @param label the label
     * @param resultPage the result view page
     * @param sortOrder the related sort order. Possible values:<br>
     *        - TaskSearchSortingLabelProvider.SHOW_PATH<br>
     *        - TaskSearchSortingLabelProvider.SHOW_ELEMENT_CONTAINER<br>
     *        - TaskSearchSortingLabelProvider.SHOW_CONTAINER_ELEMENT
     */
    public TaskSearchSortAction(final String label, final TaskSearchResultPage resultPage, final int sortOrder) {
        super(label);
        this.resultPage = resultPage;
        this.sortOrder = sortOrder;
    }

    public void run() {
        BusyIndicator.showWhile(resultPage.getViewer().getControl().getDisplay(), new Runnable() {
            public void run() {
                resultPage.setSortOrder(sortOrder);
            }
        });
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
