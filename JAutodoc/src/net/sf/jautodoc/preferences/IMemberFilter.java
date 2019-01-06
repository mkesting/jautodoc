/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

/**
 * Indicates which members should be filtered when adding Javadoc or searching for tasks.
 */
public interface IMemberFilter {

    boolean isIncludeTypes();
    boolean isIncludeFields();
    boolean isIncludeMethods();
    boolean isGetterSetterOnly();
    boolean isExcludeGetterSetter();
    boolean isExcludeOverriding();

    boolean isIncludePublic();
    boolean isIncludeProtected();
    boolean isIncludePackage();
    boolean isIncludePrivate();
}
