/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jautodoc.search.TaskSearchMatch.Finding;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.FilterUpdateEvent;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchFilter;
import org.eclipse.ui.IEditorPart;

/**
 * Represents the result of a search.
 */
public class TaskSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter {
    private static final Match[] NO_MATCHES = new Match[0];

    private TaskSearchQuery searchQuery;


    public TaskSearchResult(final TaskSearchQuery searchQuery) {
        this.searchQuery = searchQuery;
        setActiveMatchFilters(TaskSearchMatchFilter.getLastUsedFilters());
    }

    @Override
    public MatchFilter[] getAllMatchFilters() {
        return TaskSearchMatchFilter.getAllFilters();
    }

    @Override
    public void setActiveMatchFilters(final MatchFilter[] filters) {
        TaskSearchMatchFilter.setLastUsedFilters(filters);
        updateFilterStateForAllMatches(getElements(),filters);
        super.setActiveMatchFilters(filters);
    }

    public void applyFilterState(final Object[] elements) {
        updateFilterStateForAllMatches(getElements(), getActiveMatchFilters());
    }

    public TaskSearchMatch.Finding[] getFindings(final Object element) {
        final List<TaskSearchMatch.Finding> findings = new ArrayList<TaskSearchMatch.Finding>();
        for (Match match : getMatches(element)) {
            final TaskSearchMatch taskSearchMatch = (TaskSearchMatch)match;
            findings.addAll(Arrays.asList(taskSearchMatch.getFindings()));
        }
        return findings.toArray(new TaskSearchMatch.Finding[findings.size()]);
    }

    public TaskSearchMatch.Finding[] getFilteredFindings(final Object element) {
        final List<TaskSearchMatch.Finding> findings = new ArrayList<TaskSearchMatch.Finding>();
        for (Match match : getMatches(element)) {
            final TaskSearchMatch taskSearchMatch = (TaskSearchMatch)match;
            for (Finding finding : taskSearchMatch.getFindings()) {
                if (!finding.isFiltered()) {
                    findings.add(finding);
                }
            }
        }
        return findings.toArray(new TaskSearchMatch.Finding[findings.size()]);
    }

    public int getFindingCount() {
        int count = 0;
        for (Object element : getElements()) {
            count += getFindingCount(element);
        }
        return count;
    }

    public int getFindingCount(final Object element) {
        return getFindings(element).length;
    }

    public int getFilteredFindingCount() {
        int count = 0;
        for (Object element : getElements()) {
            count += getFilteredFindingCount(element);
        }
        return count;
    }

    public int getFilteredFindingCount(final Object element) {
        return getFilteredFindings(element).length;
    }

    public ISearchQuery getQuery() {
        return searchQuery;
    }

    public String getLabel() {
        final int findingCount = getFindingCount();
        final int filteredOut = findingCount - getFilteredFindingCount();

        String postfix = findingCount == 1 ? "1 finding" : "" + findingCount + " findings";
        postfix +=  " (" + filteredOut + " filtered from view)";

        return "'" + searchQuery.getSearchPattern().getDescription() + "' - " + postfix;
    }

    public String getTooltip() {
        return "JAutodoc Search";
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public IEditorMatchAdapter getEditorMatchAdapter() {
        return this;
    }

    @Override
    public IFileMatchAdapter getFileMatchAdapter() {
        return this;
    }

    public Match[] computeContainedMatches(final AbstractTextSearchResult result, final IEditorPart editor) {
        return computeContainedMatches(editor.getEditorInput());
    }

    public boolean isShownInEditor(final Match match, final IEditorPart editor) {
        final Object element = match.getElement();
        if (element instanceof IJavaElement) {
            final IOpenable openable = ((IJavaElement) element).getOpenable(); // class file or compilation unit
            return openable != null && openable.equals(editor.getEditorInput().getAdapter(IJavaElement.class));
        }
        else if (element instanceof IFile) {
            return element.equals(editor.getEditorInput().getAdapter(IFile.class));
        }
        return false;
    }

    public Match[] computeContainedMatches(final AbstractTextSearchResult result, final IFile file) {
        return computeContainedMatches(file);
    }

    public IFile getFile(final Object element) {
        if (element instanceof IJavaElement) {
            final IJavaElement javaElement = (IJavaElement) element;
            final ICompilationUnit cu = (ICompilationUnit) javaElement.getAncestor(IJavaElement.COMPILATION_UNIT);
            if (cu != null) {
                return (IFile) cu.getResource();
            }
            else {
                final IClassFile cf = (IClassFile) javaElement.getAncestor(IJavaElement.CLASS_FILE);
                if (cf != null) {
                    return (IFile) cf.getResource();
                }
            }
            return null;
        }
        else if (element instanceof IFile) {
            return (IFile) element;
        }
        return null;
    }

    private Match[] computeContainedMatches(final IAdaptable adaptable) {
        final Set<Match> matches = new HashSet<Match>();

        final IJavaElement javaElement = (IJavaElement) adaptable.getAdapter(IJavaElement.class);
        if (javaElement != null) {
            collectMatches(matches, javaElement);
        }

        final IFile file = (IFile) adaptable.getAdapter(IFile.class);
        if (file != null) {
            collectMatches(matches, file);
        }

        if (!matches.isEmpty()) {
            return matches.toArray(new Match[matches.size()]);
        }
        return NO_MATCHES;
    }

    private void collectMatches(final Set<Match> matches, final IFile element) {
        final Match[] m = getMatches(element);
        for (int i = 0; i < m.length; i++) {
            matches.add(m[i]);
        }
    }

    private void collectMatches(final Set<Match> matches, final IJavaElement element) {
        final Match[] m = getMatches(element);
        for (int i = 0; i < m.length; i++) {
            matches.add(m[i]);
        }

        if (element instanceof IParent) {
            final IParent parent = (IParent) element;
            try {
                final IJavaElement[] children = parent.getChildren();
                for (int i = 0; i < children.length; i++) {
                    collectMatches(matches, children[i]);
                }
            } catch (JavaModelException e) {
                // we will not be tracking these results
            }
        }
    }

    private void updateFilterStateForAllMatches(final Object[] elements, MatchFilter[] matchFilters) {
        if (matchFilters == null) {
            matchFilters = TaskSearchMatchFilter.NO_FILTERS;
        }

        final List<Object> changed = new ArrayList<Object>();
        for (Object element : elements) {
            for (Match match : getMatches(element)) {
                if (updateFilterState((TaskSearchMatch) match, matchFilters)) {
                    changed.add(match);
                }
            }
        }

        final Match[] allChanges = changed.toArray(new Match[changed.size()]);
        fireChange(new FilterUpdateEvent(this, allChanges, matchFilters));
    }

    private boolean updateFilterState(final TaskSearchMatch match, final MatchFilter[] matchFilters) {
        boolean findingFilterChanged = false;

        for (Finding finding : match.getFindings()) {
            boolean wasFiltered = finding.isFiltered();
            finding.setFiltered(false);
            for (MatchFilter matchFilter : matchFilters) {
                final TaskSearchMatchFilter taskSearchMatchFilter = (TaskSearchMatchFilter)matchFilter;
                if (taskSearchMatchFilter.filters(finding)) {
                    finding.setFiltered(true);
                }
            }

            if (finding.isFiltered() != wasFiltered) {
                findingFilterChanged = true;
            }
        }
        return findingFilterChanged;
    }
}
