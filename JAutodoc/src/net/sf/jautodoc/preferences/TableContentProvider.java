/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Content provider for tables.
 */
public class TableContentProvider implements IStructuredContentProvider {

    private List<Object> elements = new ArrayList<Object>();

    private ViewerSorter sorter = null;
    private TableViewer tableViewer;

    /**
     * Adds an object.
     *
     * @param o the object
     */
    public void add(Object o) {
        if (elements.contains(o)) {
            return;
        }
        elements.add(o);
        tableViewer.add(o);
        tableViewer.setSelection(new StructuredSelection(o), true);
    }

    /**
     * Removes an object.
     *
     * @param o the object
     */
    public void remove(Object o) {
        elements.remove(o);
        tableViewer.remove(o);
    }

    /**
     * Removes the selection.
     *
     * @param selection the selection
     */
    public void remove(IStructuredSelection selection) {
        Object[] array = selection.toArray();
        elements.removeAll(Arrays.asList(array));
        tableViewer.remove(array);
    }

    public void dispose() {
    }

    public Object[] getElements(Object inputElement) {
        return elements.toArray();
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        tableViewer = (TableViewer) viewer;
        elements.clear();
        if (newInput != null) {
            tableViewer.setSorter(getSorter());
            List<?> list;
            if (newInput instanceof List<?>) {
                list = (List<?>) newInput;
            } else {
                list = Arrays.asList((Object[]) newInput);
            }
            elements.addAll(list);
        }
    }

    private ViewerSorter getSorter() {
        if (sorter != null)
            return sorter;

        sorter = new ViewerSorter() {
            @Override
            @SuppressWarnings({ "unchecked", "rawtypes" })
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof Comparable && e2 instanceof Comparable) {
                    return ((Comparable) e1).compareTo(e2);
                }
                return e1.toString().compareTo(e2.toString());
            }
        };
        return sorter;
    }
}
