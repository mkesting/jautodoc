/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preview;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ltk.core.refactoring.TextEditBasedChange;
import org.eclipse.ltk.ui.refactoring.TextEditChangeNode;

/**
 * Adapter factory for CompilationUnitChange objects.
 * <p>
 * Adaptation of org.eclipse.jdt.internal.ui.refactoring.RefactoringAdapterFactory
 * </p>
 */
public class CompilationUnitChangeAdapterFactory implements IAdapterFactory {

    @SuppressWarnings("rawtypes")
    private static final Class[] ADAPTER_LIST = new Class[] {
        TextEditChangeNode.class
    };

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public Class[] getAdapterList() {
        return ADAPTER_LIST;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Object object, final Class key) {
        if (!TextEditChangeNode.class.equals(key)) {
            return null;
        }
        if (!(object instanceof CompilationUnitChange)) {
            return null;
        }
        return new CompilationUnitChangeNode((TextEditBasedChange)object);
    }
}
