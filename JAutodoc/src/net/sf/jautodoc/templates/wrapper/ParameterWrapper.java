/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.templates.ITemplateKinds;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Wrapper class for parameters.
 */
public class ParameterWrapper extends AbstractMemberWrapper {
	private IMember member; 
	private String name;
	private String type;
	

	/**
	 * Instantiates a new parameter wrapper.
	 * 
	 * @param member the related method or generic type
	 * @param type the type
	 * @param name the name
	 */
	public ParameterWrapper(IMember member, String type, String name) {
		super(member, ITemplateKinds.PARAMETER);
		this.member = member;
		this.name = name;
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.AbstractMemberWrapper#getName()
	 */
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.AbstractMemberWrapper#getParent()
	 */
	public IMemberWrapper getParent() {
		if (parent == null) {
			parent = WrapperFactory.getWrapper(member);
		}
		return parent;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.AbstractMemberWrapper#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ParameterWrapper)) {
			return false;
		}
		return getName().equals(((ParameterWrapper)obj).getName()) && super.equals(obj);
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
		return Signature.getSignatureSimpleName(type);
	}
}
