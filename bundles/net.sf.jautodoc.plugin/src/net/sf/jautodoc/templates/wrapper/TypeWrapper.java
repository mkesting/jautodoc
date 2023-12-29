/*******************************************************************
 * Copyright (c) 2006 - 2023, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.templates.ITemplateKinds;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wrapper class for Types.
 */
public class TypeWrapper extends AbstractMemberWrapper {
    private IType type;


    /**
     * Instantiates a new type wrapper.
     *
     * @param type the type
     */
    protected TypeWrapper(IType type) {
        super(type, ITemplateKinds.TYPE);
        this.type = type;
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getSignature()
     */
    public String getSignature() throws JavaModelException {
        return getType() + " " + getName();
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getType()
     */
    public String getType() throws JavaModelException {
        if (type.isClass()) {
            return "class";
        }

        if (type.isInterface()) {
            return "interface";
        }

        if (type.isEnum()) {
            return "enum";
        }

        if (type.isRecord()) {
            return "record";
        }

        return "";
    }
}
