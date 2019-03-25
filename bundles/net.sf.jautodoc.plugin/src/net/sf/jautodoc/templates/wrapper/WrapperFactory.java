/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;


/**
 * A factory for creating Wrapper objects.
 */
public final class WrapperFactory {
	
	/**
	 * Gets the member wrapper.
	 * 
	 * @param member the member
	 * @return the wrapper
	 */
	public static IMemberWrapper getWrapper(IMember member) {
		if (member instanceof IType) {
			return new TypeWrapper((IType)member);
		}
		
		if (member instanceof IField) {
			return new FieldWrapper((IField)member);
		}
		
		if (member instanceof IMethod) {
			return new MethodWrapper((IMethod)member);
		}
		
		return null;
	}
	
	/**
	 * Gets the parameter wrapper.
	 * 
	 * @param member the related method or generic type
	 * @param type the type
	 * @param name the name
	 * @return the parameter wrapper
	 */
	public static IMemberWrapper getParameterWrapper(IMember member, String type, String name) {
		return new ParameterWrapper(member, type, name);
	}
	
	/**
	 * Gets the exception wrapper.
	 * 
	 * @param method the related method
	 * @param name the name
	 * @return the exception wrapper
	 */
	public static IMemberWrapper getExceptionWrapper(IMethod method, String name) {
		return new ExceptionWrapper(method, name);
	}
}
