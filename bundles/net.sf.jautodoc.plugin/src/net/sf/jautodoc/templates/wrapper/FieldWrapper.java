/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.templates.ITemplateKinds;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Wrapper class for Fields.
 */
public class FieldWrapper extends AbstractMemberWrapper {
	private IField field;
	
	/**
	 * Instantiates a new field wrapper.
	 * 
	 * @param field the field
	 */
	public FieldWrapper(IField field) {
		super(field, ITemplateKinds.FIELD);
		this.field = field;
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
		return Signature.getSignatureSimpleName(field.getTypeSignature());
	}
}
