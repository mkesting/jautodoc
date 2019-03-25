/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jface.action.Action;

/**
 * Action for grouping search results by project, package or compilation unit.
 */
public class TaskSearchGroupAction extends Action {
    private final int grouping;
    private final TaskSearchResultPage resultPage;

    /**
     * Instantiates a new task search group action.
     *
     * @param label the label
     * @param tooltip the tooltip
     * @param resultPage the result view page
     * @param grouping the related grouping level. Possible values:<br>
     *        - TaskSearchTreeContentProvider.LEVEL_PROJECT<br>
     *        - TaskSearchTreeContentProvider.LEVEL_PACKAGE<br>
     *        - TaskSearchTreeContentProvider.LEVEL_FILE
     */
    public TaskSearchGroupAction(final String label, final String tooltip, final TaskSearchResultPage resultPage,
            final int grouping) {
        super(label);
        setToolTipText(tooltip);
        this.resultPage = resultPage;
        this.grouping = grouping;
    }

    public void run() {
        resultPage.setGrouping(grouping);
    }

    public int getGrouping() {
        return grouping;
    }
}
