/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.templates.ITemplateKinds;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wrapper class for Exceptions.
 */
public class ExceptionWrapper extends AbstractMemberWrapper {
	private IMethod method; 
	private String name;
	

	/**
	 * Instantiates a new exception wrapper.
	 * 
	 * @param method the related method
	 * @param name the exception name
	 */
	public ExceptionWrapper(IMethod method, String name) {
		super(method, ITemplateKinds.EXCEPTION);
		this.method = method;
		this.name = name;
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
			parent = WrapperFactory.getWrapper(method);
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
		return getName();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getType()
	 */
	public String getType() throws JavaModelException {
		return "exception";
	}
}
