/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * Opens the Search Dialog and brings the JAutodoc search page to front
 */
public class OpenSearchPageAction implements IWorkbenchWindowActionDelegate {
    private static final String SEARCH_PAGE_ID = "net.sf.jautodoc.search.SearchPage"; //$NON-NLS-1$

    private IWorkbenchWindow window;


    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {
        if (window != null && window.getActivePage() != null) {
            NewSearchUI.openSearchDialog(window, SEARCH_PAGE_ID);
        }
    }

    public void dispose() {
        window = null;
    }

    public void selectionChanged(IAction action, ISelection selection) {
        // do nothing since the action isn't selection dependent.
    }
}
