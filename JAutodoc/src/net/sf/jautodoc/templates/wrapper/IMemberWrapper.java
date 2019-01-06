/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;


/**
 * Interface for member wrapper.
 */
public interface IMemberWrapper {

	/**
	 * Gets the wrapped member.
	 *
	 * @return the wrapped member
	 */
	public IMember getMember();

	/**
	 * Gets the parent member wrapper.
	 *
	 * @return the parent member wrapper
	 */
	public IMemberWrapper getParent();

	/**
	 * Gets the name of the member.
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Gets the type of the member. For types this is one of
	 * class|interface|enum and for methods it's the return
	 * type.
	 *
	 * @return the type
	 *
	 * @throws JavaModelException the java model exception
	 */
	public String getType() throws JavaModelException;

	/**
	 * Gets the declaring type.
	 *
	 * @return the declaring type
	 *
	 * @throws JavaModelException the java model exception
	 */
	public String getDeclaringType() throws JavaModelException;

	/**
	 * Gets the signature of the member.
	 *
	 * @return the signature
	 *
	 * @throws JavaModelException the java model exception
	 */
	public String getSignature() throws JavaModelException;

	/**
	 * Checks if the member is static.
	 *
	 * @return true, if is static
	 *
	 * @throws JavaModelException the java model exception
	 */
	public boolean isStatic() throws JavaModelException;

	/**
	 * Checks if the member is final.
	 *
	 * @return true, if is final
	 *
	 * @throws JavaModelException the java model exception
	 */
	public boolean isFinal() throws JavaModelException;

	/**
	 * Checks if the member is an constructor.
	 *
	 * @return true, if is an constructor
	 *
	 * @throws JavaModelException the java model exception
	 */
	public boolean isConstructor() throws JavaModelException;

	/**
	 * Checks if the member is the main method.
	 *
	 * @return true, if is the main method
	 *
	 * @throws JavaModelException the java model exception
	 */
	public boolean isMainMethod() throws JavaModelException;

	/**
	 * Checks if member is a type.
	 *
	 * @return true, if is type
	 */
	public boolean isType();

	/**
	 * Checks if member is a field.
	 *
	 * @return true, if is field
	 */
	public boolean isField();

	/**
	 * Checks if member is a method.
	 *
	 * @return true, if is method
	 */
	public boolean isMethod();

	/**
	 * Checks if member is a parameter.
	 *
	 * @return true, if is parameter
	 */
	public boolean isParameter();

	/**
	 * Checks if member is a exception.
	 *
	 * @return true, if is exception
	 */
	public boolean isException();

	/**
     * Gets the annotation whith the given name.
     *
     * @param name the name
     * @return the annotation
     */
	public AnnotationWrapper getAnnotation(String name) throws JavaModelException;
}
