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

import net.sf.jautodoc.utils.Utils;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;

/**
 * Tracks changes of elements in the current search result.
 */
public class TaskSearchResultUpdater implements IElementChangedListener, IQueryListener {

    private static final int CHANGED_FLAGS = IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_CHILDREN
            | IJavaElementDelta.F_PRIMARY_RESOURCE;
    private static final int REMOVED_FLAGS = IJavaElementDelta.F_REMOVED_FROM_CLASSPATH | IJavaElementDelta.F_CLOSED;

    private final TaskSearchResult searchResult;
    private final TaskSearchPattern searchPattern;
    private final Set<ICompilationUnit> scope;


    public TaskSearchResultUpdater(final ICompilationUnit[] compUnits, final TaskSearchPattern searchPattern,
            final TaskSearchResult searchResult) {
        this.scope = Utils.asSet(compUnits);
        this.searchResult = searchResult;
        this.searchPattern = searchPattern;

        NewSearchUI.addQueryListener(this);
        JavaCore.addElementChangedListener(this);
        //JavaCore.addElementChangedListener(this, ElementChangedEvent.POST_CHANGE);
    }

    public void elementChanged(final ElementChangedEvent event) {
        final IJavaElementDelta delta = event.getDelta();
        final Set<Object> removedElements = new HashSet<Object>();
        final Set<ICompilationUnit> changedCompilationUnits = new HashSet<ICompilationUnit>();

        collectAffected(changedCompilationUnits, removedElements, delta);
        handleChanged(changedCompilationUnits);
        handleRemoved(removedElements);
    }

    private void handleChanged(final Set<ICompilationUnit> changedCompilationUnits) {
        for (ICompilationUnit compilationUnit : changedCompilationUnits) {
            new UpdateJob(compilationUnit).schedule();
        }
    }

    private void handleRemoved(final Set<Object> removedElements) {
        for (Object element : searchResult.getElements()) {
            if (isContainedInRemoved(removedElements, element) && element instanceof IJavaElement) {
                final IJavaElement javaElement = (IJavaElement) element;
                if (!javaElement.exists()) {
                    searchResult.removeMatches(searchResult.getMatches(javaElement));
                }
            }
        }
    }

    private boolean isContainedInRemoved(final Set<Object> removedElements, final Object object) {
        for (Object element : removedElements) {
            if (isParentOf(element, object)) {
                return true;
            }
        }
        return false;
    }

    private boolean isParentOf(final Object ancestor, Object descendant) {
        while (descendant != null && !ancestor.equals(descendant)) {
            descendant = getParent(descendant);
        }
        return descendant != null;
    }

    private Object getParent(final Object object) {
        if (object instanceof IJavaElement) {
            return ((IJavaElement) object).getParent();
        }
        return null;
    }

    private void collectAffected(final Set<ICompilationUnit> changedCompilationUnits, final Set<Object> removedElements,
            final IJavaElementDelta delta) {

        if (delta.getKind() == IJavaElementDelta.REMOVED) {
            removedElements.add(delta.getElement());
        }
        else if (delta.getKind() == IJavaElementDelta.CHANGED) {
            final int flags = delta.getFlags();
            if (delta.getElement() instanceof ICompilationUnit) {
                final ICompilationUnit cu = (ICompilationUnit)delta.getElement();
                if (scope.contains(cu) && (flags & CHANGED_FLAGS) != 0) {
                    removedElements.add(cu);
                    changedCompilationUnits.add(cu);
                }
            }
            else {
                if ((flags & REMOVED_FLAGS) != 0) {
                    removedElements.add(delta.getElement());
                }
                else {
                    for (IJavaElementDelta childDelta : delta.getAffectedChildren()) {
                        collectAffected(changedCompilationUnits, removedElements, childDelta);
                    }
                }
            }
        }
    }

    public void queryRemoved(ISearchQuery query) {
        if (searchResult.equals(query.getSearchResult())) {
            JavaCore.removeElementChangedListener(this);
            NewSearchUI.removeQueryListener(this);
        }
    }

    public void queryAdded(ISearchQuery query) {
        // don't care
    }

    public void queryStarting(ISearchQuery query) {
        // not interested
    }

    public void queryFinished(ISearchQuery query) {
        // not interested
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    private final class UpdateJob extends Job {

        private final ICompilationUnit cu;

        public UpdateJob(final ICompilationUnit cu) {
            super("Update JAutodoc Search");

            this.cu = cu;
            setSystem(true);
            setRule(cu.getResource());
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                final TaskSearchEngine engine = new TaskSearchEngine(cu, searchPattern, searchResult);
                engine.search(monitor);
            } catch (Exception e) {
                // ignore
            }
            return Status.OK_STATUS;
        }
    }
}
