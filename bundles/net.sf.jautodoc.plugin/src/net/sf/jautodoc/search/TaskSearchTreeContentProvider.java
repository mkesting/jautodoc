/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

/**
 * Content provider for the search result tree viewer.
 */
public class TaskSearchTreeContentProvider extends TaskSearchContentProvider implements ITreeContentProvider {

    public static final int LEVEL_TYPE = 1;
    public static final int LEVEL_FILE = 2;
    public static final int LEVEL_PACKAGE = 3;
    public static final int LEVEL_PROJECT = 4;

    private static final int[][] JAVA_ELEMENT_TYPES= {
            {IJavaElement.TYPE},
            {IJavaElement.CLASS_FILE, IJavaElement.COMPILATION_UNIT},
            {IJavaElement.PACKAGE_FRAGMENT},
            {IJavaElement.JAVA_PROJECT, IJavaElement.PACKAGE_FRAGMENT_ROOT},
            {IJavaElement.JAVA_MODEL}};

    private static final int[][] RESOURCE_TYPES= {
            {},
            {IResource.FILE},
            {IResource.FOLDER},
            {IResource.PROJECT},
            {IResource.ROOT}};

    private static final int MAX_LEVEL = JAVA_ELEMENT_TYPES.length - 1;

    private int currentLevel;
    private Map<Object, Set<Object>> childrenMap;
    private StandardJavaElementContentProvider contentProvider;


    public TaskSearchTreeContentProvider(TaskSearchResultPage resultPage, int level) {
        super(resultPage);
        currentLevel= level;
        contentProvider= new FastJavaElementProvider();
    }

    public Object getParent(final Object child) {
        Object possibleParent = internalGetParent(child);

        if (possibleParent instanceof IJavaElement) {
            final IJavaElement javaElement = (IJavaElement) possibleParent;
            for (int j = currentLevel; j < MAX_LEVEL + 1; j++) {
                for (int i = 0; i < JAVA_ELEMENT_TYPES[j].length; i++) {
                    if (javaElement.getElementType() == JAVA_ELEMENT_TYPES[j][i]) {
                        return null;
                    }
                }
            }
        }
        else if (possibleParent instanceof IResource) {
            final IResource resource = (IResource) possibleParent;
            for (int j = currentLevel; j < MAX_LEVEL + 1; j++) {
                for (int i = 0; i < RESOURCE_TYPES[j].length; i++) {
                    if (resource.getType() == RESOURCE_TYPES[j][i]) {
                        return null;
                    }
                }
            }
        }
        return possibleParent;
    }

    private Object internalGetParent(final Object child) {
        return contentProvider.getParent(child);
    }

    public Object[] getElements(final Object inputElement) {
        return getChildren(inputElement);
    }

    protected synchronized void initialize(final AbstractTextSearchResult result) {
        super.initialize(result);

        childrenMap = new HashMap<Object, Set<Object>>();
        if (result != null) {
            final Object[] elements = result.getElements();
            for (int i = 0; i < elements.length; i++) {
                if (getResultPage().getDisplayedFindingsCount(elements[i]) > 0) {
                    insert(null, null, elements[i]);
                }
            }
        }
    }

    protected void insert(final Map<Object, Set<Object>> toAdd, final Set<Object> toUpdate, Object child) {
        Object parent = getParent(child);

        while (parent != null) {
            if (insertChild(parent, child)) {
                if (toAdd != null) {
                    insertInto(parent, child, toAdd);
                }
            }
            else {
                if (toUpdate != null) {
                    toUpdate.add(parent);
                }
                return;
            }

            child = parent;
            parent = getParent(child);
        }

        if (insertChild(getSearchResult(), child)) {
            if (toAdd != null) {
                insertInto(getSearchResult(), child, toAdd);
            }
        }
        else {
            if (toUpdate != null) {
                toUpdate.add(child);
            }
        }
    }

    private boolean insertChild(final Object parent, final Object child) {
        return insertInto(parent, child, childrenMap);
    }

    private boolean insertInto(final Object parent, final Object child, final Map<Object, Set<Object>> map) {
        Set<Object> children = map.get(parent);
        if (children == null) {
            children = new HashSet<Object>();
            map.put(parent, children);
        }
        return children.add(child);
    }

    protected void remove(final Set<Object> toRemove, final Set<Object> toUpdate, final Object element) {
        // precondition here: fResult.getMatchCount(child) <= 0

        if (hasChildren(element)) {
            if (toUpdate != null) {
                toUpdate.add(element);
            }
        }
        else {
            if (getResultPage().getDisplayedFindingsCount(element) == 0) {
                childrenMap.remove(element);
                final Object parent = getParent(element);
                if (parent != null) {
                    if (removeFromSiblings(element, parent)) {
                        remove(toRemove, toUpdate, parent);
                    }
                }
                else {
                    if (removeFromSiblings(element, getSearchResult())) {
                        if (toRemove != null)
                            toRemove.add(element);
                    }
                }
            }
            else {
                if (toUpdate != null) {
                    toUpdate.add(element);
                }
            }
        }
    }

    private boolean removeFromSiblings(final Object element, final Object parent) {
        final Set<Object> siblings = childrenMap.get(parent);
        if (siblings != null) {
            return siblings.remove(element);
        }
        else {
            return false;
        }
    }

    public Object[] getChildren(final Object parentElement) {
        final Set<Object> children = childrenMap.get(parentElement);
        if (children == null) {
            return EMPTY_ARR;
        }

        final int limit = getResultPage().getElementLimit().intValue();
        if (limit != -1 && limit < children.size()) {
            final Object[] limitedArray = new Object[limit];
            final Iterator<Object> iterator = children.iterator();
            for (int i = 0; i < limit; i++) {
                limitedArray[i] = iterator.next();
            }
            return limitedArray;
        }
        return children.toArray();
    }

    public boolean hasChildren(final Object element) {
        final Set<Object> children= childrenMap.get(element);
        return children != null && !children.isEmpty();
    }

    public synchronized void elementsChanged(final Object[] updatedElements) {
        if (getSearchResult() == null) {
            return;
        }

        final AbstractTreeViewer viewer = (AbstractTreeViewer) getResultPage().getViewer();

        final Set<Object> toRemove = new HashSet<Object>();
        final Set<Object> toUpdate = new HashSet<Object>();
        final Map<Object, Set<Object>> toAdd = new HashMap<Object, Set<Object>>();

        for (int i = 0; i < updatedElements.length; i++) {
            if (getResultPage().getDisplayedFindingsCount(updatedElements[i]) > 0) {
                insert(toAdd, toUpdate, updatedElements[i]);
            }
            else {
                remove(toRemove, toUpdate, updatedElements[i]);
            }
        }

        viewer.remove(toRemove.toArray());
        for (Iterator<Object> iter = toAdd.keySet().iterator(); iter.hasNext();) {
            Object parent = iter.next();
            final Set<Object> children = toAdd.get(parent);
            viewer.add(parent, children.toArray());
        }

        for (Iterator<Object> elementsToUpdate = toUpdate.iterator(); elementsToUpdate.hasNext();) {
            viewer.refresh(elementsToUpdate.next());
        }
    }

    public void clear() {
        initialize(getSearchResult());
        getResultPage().getViewer().refresh();
    }

    public void setLevel(int level) {
        currentLevel = level;
        initialize(getSearchResult());
        getResultPage().getViewer().refresh();
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    static class FastJavaElementProvider extends StandardJavaElementContentProvider {
        public Object getParent(Object element) {
            return internalGetParent(element);
        }
    }
}
