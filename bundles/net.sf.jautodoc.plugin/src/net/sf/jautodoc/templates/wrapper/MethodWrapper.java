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
import org.eclipse.jdt.core.Signature;


/**
 * Wrapper class for Methods.
 */
public class MethodWrapper extends AbstractMemberWrapper {
	private IMethod method;
	
	/**
	 * Instantiates a new method wrapper.
	 * 
	 * @param method the method
	 */
	public MethodWrapper(IMethod method) {
		super(method, ITemplateKinds.METHOD);
		this.method = method;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getSignature()
	 */
	public String getSignature() throws JavaModelException {
		return Signature.toString(method.getSignature(),
								  method.getElementName(),
								  method.getParameterNames(),
								  false, !method.isConstructor());
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getType()
	 */
	public String getType() throws JavaModelException {
		return Signature.getSignatureSimpleName(method.getReturnType());
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.AbstractMemberWrapper#isConstructor()
	 */
	public boolean isConstructor() throws JavaModelException {
		return method.isConstructor();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.AbstractMemberWrapper#isMainMethod()
	 */
	public boolean isMainMethod() throws JavaModelException {
		return method.isMainMethod();
	}
}
