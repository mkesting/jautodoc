/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.templates.MatchingElement;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Wrapper class for IJavaElement. It's used to get values for the
 * variables ${project_name}, ${package_name}, ${file_name}, ${type_name}.
 */
public class JavaElementWrapper {
	public static final int PROJECT = 1;
	public static final int PACKAGE = 2;
	public static final int FILE 	= 3;
	public static final int TYPE	= 4;
	
	private int property;
	private IJavaElement javaElement;

	
	/**
	 * Instantiates a new java element wrapper.
	 * 
	 * @param me the matchning element
	 * @param property the property
	 */
	public JavaElementWrapper(MatchingElement me, int property) {
		this(me.getMember() != null ? me.getMember().getMember() : null, property);
	}
	
	/**
	 * Instantiates a new java element wrapper.
	 * 
	 * @param javaElement the java element
	 * @param property the property
	 */
	public JavaElementWrapper(IJavaElement javaElement, int property) {
		this.property = property;
		this.javaElement = javaElement;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		switch(property) {
		case PROJECT:
			return javaElement != null ? getProjectName()	 : "?project_name?";
			
		case PACKAGE:
			return javaElement != null ? getPackageName()	 : "?package_name?";
			
		case FILE:
			return javaElement != null ? getFileName()		 : "?file_name?";
			
		case TYPE: 
			return javaElement != null ? getPrimaryTypeName() : "?type_name?";
			
		default:
			return "";
		}
	}
	
	private String getProjectName() {
		return javaElement.getJavaProject().getElementName();
	}
	
	private String getPackageName() {
		if (javaElement instanceof IPackageFragment) {
			return javaElement.getElementName();
		}
		
		ICompilationUnit compUnit = getCompilationUnit();
		if (compUnit == null) {
			return "";
		}
		
		try {
			IPackageDeclaration[] packages = compUnit.getPackageDeclarations();
			return packages.length > 0 ? packages[0].getElementName() : "";
		} catch (JavaModelException e) {/* ignore */}
		
		return "";
	}
	
	private String getFileName() {
		if (javaElement instanceof IPackageFragment) {
			return javaElement.getPath().lastSegment();
		}
		
		ICompilationUnit compUnit = getCompilationUnit();
		if (compUnit == null) {
			return "";
		}
		
		return compUnit.getElementName();
	}
	
	private String getPrimaryTypeName() {
		if (javaElement instanceof IPackageFragment) {
			return "";
		}
		
		ICompilationUnit compUnit = getCompilationUnit();
		if (compUnit == null) {
			return "";
		}
		
		IType type = compUnit.findPrimaryType();
		return type != null ? type.getElementName() : "";
	}
	
	private ICompilationUnit getCompilationUnit() {
		ICompilationUnit compUnit = null;
		if (javaElement instanceof ICompilationUnit) {
			compUnit = (ICompilationUnit)javaElement;
		}
		else if (javaElement instanceof IMember) {
			compUnit = ((IMember)javaElement).getCompilationUnit();
		}
		
		return compUnit;
	}
}
