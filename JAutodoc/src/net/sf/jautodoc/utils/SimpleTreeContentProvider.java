/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * A simple tree content provider.
 */
public class SimpleTreeContentProvider implements ITreeContentProvider {

    private final Object[] elements;

    public SimpleTreeContentProvider(final List<?> elements) {
        this.elements = elements.toArray(new Object[elements.size()]);
    }

    public Object[] getChildren(Object parentElement) {
        return null;
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        return false;
    }

    public Object[] getElements(Object inputElement) {
        return elements;
    }

    public void dispose() {
        // empty
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // empty
    }
}
