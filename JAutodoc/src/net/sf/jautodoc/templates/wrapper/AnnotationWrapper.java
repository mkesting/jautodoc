/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.utils.StringUtils;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wrapper class for annotations.
 */
public class AnnotationWrapper {
    private final IAnnotation annotation;

    /**
     * Instantiates a new annotation wrapper.
     */
    public AnnotationWrapper() {
        this(null);
    }

    /**
     * Instantiates a new annotation wrapper.
     *
     * @param annotation the wrapped annotation
     */
    public AnnotationWrapper(final IAnnotation annotation) {
        this.annotation = annotation;
    }

    /**
     * Gets the name of the wrapped annotation.
     *
     * @return the name
     */
    public String getName() {
        return annotation == null ? "" : annotation.getElementName();
    }

    /**
     * Gets the value with the give key from the wrapped annotation.
     *
     * @param key the key
     * @return the value
     */
    public String getValue(final String key) {
        if (annotation != null) {
            for (IMemberValuePair pair : getMemberValuePairs()) {
                if (pair.getMemberName().equals(key)) {
                    return StringUtils.valueOf(pair.getValue());
                }
            }
        }
        return "";
    }

    /**
     * Checks, if annotation exists.
     *
     * @return true, if annotation exists
     */
    public boolean exists() {
        return annotation != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName();
    }

    private IMemberValuePair[] getMemberValuePairs() {
        try {
            return annotation.getMemberValuePairs();
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }
        return new IMemberValuePair[0];
    }
}
