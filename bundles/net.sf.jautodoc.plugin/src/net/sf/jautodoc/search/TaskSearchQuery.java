/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import net.sf.jautodoc.JAutodocPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;

/**
 * Represents a particular search query.
 */
public class TaskSearchQuery implements ISearchQuery {

    private final ICompilationUnit[] compUnits;
    private final TaskSearchResult searchResult;
    private final TaskSearchPattern searchPattern;


    public TaskSearchQuery(final ICompilationUnit[] compUnits, final TaskSearchPattern searchPattern) {
        this.compUnits = compUnits;
        this.searchPattern = searchPattern;
        this.searchResult = new TaskSearchResult(this);
        new TaskSearchResultUpdater(compUnits, searchPattern, searchResult);
    }

    public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
        searchResult.removeAll();

        IStatus status = Status.OK_STATUS;
        try {
            doSearch(monitor);
        } catch (Exception e) {
            status = new Status(IStatus.ERROR, JAutodocPlugin.PLUGIN_ID, "Error in JAutodoc Search", e);
        }
        return status;
    }

    private void doSearch(final IProgressMonitor monitor) throws Exception {
        monitor.beginTask(getLabel(), compUnits.length * 5);
        for (ICompilationUnit cu : compUnits) {
            final TaskSearchEngine engine = new TaskSearchEngine(cu, searchPattern, searchResult);
            engine.search(new SubProgressMonitor(monitor, 5));

            if (monitor.isCanceled()) {
                break;
            }
        }
    }

    public TaskSearchPattern getSearchPattern() {
        return searchPattern;
    }

    public String getLabel() {
        return "JAutodoc Search";
    }

    public ISearchResult getSearchResult() {
        return searchResult;
    }

    public boolean canRerun() {
        return true;
    }

    public boolean canRunInBackground() {
        return true;
    }
}
