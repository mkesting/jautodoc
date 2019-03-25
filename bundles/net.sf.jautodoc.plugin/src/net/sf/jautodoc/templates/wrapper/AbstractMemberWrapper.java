/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates.wrapper;

import net.sf.jautodoc.templates.ITemplateKinds;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;


/**
 * Abstract base class for member wrappers.
 */
public abstract class AbstractMemberWrapper implements IMemberWrapper {
	protected int kind;
	protected IMember member;
	protected IMemberWrapper parent;


	/**
	 * Instantiates a new abstract member wrapper.
	 *
	 * @param member the member
	 * @param kind the kind of the member
	 */
	protected AbstractMemberWrapper(IMember member, int kind) {
		this.kind	= kind;
		this.member = member;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getMember()
	 */
	public IMember getMember() {
		return member;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getName()
	 */
	public String getName() {
		return member.getElementName();
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getDeclaringType()
	 */
	public String getDeclaringType() throws JavaModelException {
		IMember declaringType = member.getDeclaringType();
		return declaringType != null ? declaringType.getElementName() : "";
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#getParent()
	 */
	public IMemberWrapper getParent() {
		if (parent == null && member.getParent() instanceof IMember) {
			parent = WrapperFactory.getWrapper((IMember)member.getParent());
		}
		return parent;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof IMemberWrapper)) {
			return false;
		}
		return member.equals(((IMemberWrapper)obj).getMember());
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isStatic()
	 */
	public boolean isStatic() throws JavaModelException {
		return Flags.isStatic(member.getFlags());
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isFinal()
	 */
	public boolean isFinal() throws JavaModelException {
		return Flags.isFinal(member.getFlags());
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isConstructor()
	 */
	public boolean isConstructor() throws JavaModelException {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isMainMethod()
	 */
	public boolean isMainMethod() throws JavaModelException {
		return false;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isType()
	 */
	public boolean isType() {
		return kind == ITemplateKinds.TYPE;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isField()
	 */
	public boolean isField() {
		return kind == ITemplateKinds.FIELD;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isMethod()
	 */
	public boolean isMethod() {
		return kind == ITemplateKinds.METHOD;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isParameter()
	 */
	public boolean isParameter() {
		return kind == ITemplateKinds.PARAMETER;
	}

	/* (non-Javadoc)
	 * @see net.sf.jautodoc.templates.wrapper.IMemberWrapper#isException()
	 */
	public boolean isException() {
		return kind == ITemplateKinds.EXCEPTION;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationWrapper getAnnotation(final String name) throws JavaModelException {
        if (member instanceof IAnnotatable) {
            final IAnnotatable annotatable = (IAnnotatable)member;
            for (IAnnotation annotation : annotatable.getAnnotations()) {
                if (annotation.getElementName().equals(name)) {
                    return new AnnotationWrapper(annotation);
                }
            }
        }
        return new AnnotationWrapper();
    }
}
