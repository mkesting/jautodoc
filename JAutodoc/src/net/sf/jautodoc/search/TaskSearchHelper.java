/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkingSet;

/**
 * Helper methods for collecting all target compilation units of the current search.
 */
public class TaskSearchHelper {

    public static void collectCompilationUnitsOnWorkspace(final Collection<ICompilationUnit> cus) throws JavaModelException {
        final IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
        for (IJavaProject project : projects) {
            collectCompilationUnits(project, cus);
        }
    }

    public static void collectCompilationUnitsOnSelectedProjects(final String[] projectNames, final Collection<ICompilationUnit> cus)
            throws JavaModelException {
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

        for (int i = 0; i < projectNames.length; i++) {
            IJavaProject project = JavaCore.create(root.getProject(projectNames[i]));
            collectCompilationUnits(project, cus);
        }
    }

    public static void collectCompilationUnitsOnWorkingSets(final IWorkingSet[] workingSets, final Collection<ICompilationUnit> cus) throws JavaModelException {
        for (IWorkingSet workingSet : workingSets) {
            collectCompilationUnits(workingSet, cus);
        }
    }

    public static void collectCompilationUnitsOnSelection(final IStructuredSelection selection, final Collection<ICompilationUnit> cus) throws JavaModelException {
        for (Object object : selection.toArray()) {
            collectCompilationUnits(object, cus);
        }
    }

    public static void collectCompilationUnits(final IWorkingSet workingSet, final Collection<ICompilationUnit> cus)
            throws JavaModelException {
        final IAdaptable[] elements = workingSet.getElements();
        for (IAdaptable element : elements) {
            collectCompilationUnits(element, cus);
        }
    }

    public static void collectCompilationUnits(final IJavaProject project, final Collection<ICompilationUnit> cus)
            throws JavaModelException {
        if (project.exists() && project.isOpen()) {
            final IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
            for (int k = 0; k < roots.length; k++) {
                collectCompilationUnits(roots[k], cus);
            }
        }
    }

    public static void collectCompilationUnits(final IPackageFragmentRoot root, final Collection<ICompilationUnit> cus)
            throws JavaModelException {
        if (root.getKind() == IPackageFragmentRoot.K_SOURCE) {
            final IJavaElement[] children = root.getChildren();
            for (int i = 0; i < children.length; i++) {
                collectCompilationUnits((IPackageFragment) children[i], cus);
            }
        }
    }

    public static void collectCompilationUnits(final IPackageFragment pkg, final Collection<ICompilationUnit> cus)
            throws JavaModelException {
        cus.addAll(Arrays.asList(pkg.getCompilationUnits()));
    }

    public static void collectCompilationUnits(final Object object, final Collection<ICompilationUnit> cus)
            throws JavaModelException {
        if (object instanceof IJavaElement) {
            final IJavaElement javaElement = (IJavaElement) object;
            if (javaElement.exists()) {
                switch (javaElement.getElementType()) {
                case IJavaElement.TYPE:
                case IJavaElement.FIELD:
                case IJavaElement.METHOD:
                case IJavaElement.INITIALIZER:
                    cus.add(((IMember) javaElement).getCompilationUnit());
                    break;
                case IJavaElement.COMPILATION_UNIT:
                    cus.add((ICompilationUnit) javaElement);
                    break;
                case IJavaElement.IMPORT_CONTAINER:
                    cus.add((ICompilationUnit) javaElement.getParent());
                    break;
                case IJavaElement.PACKAGE_FRAGMENT:
                    collectCompilationUnits((IPackageFragment) javaElement, cus);
                    break;
                case IJavaElement.PACKAGE_FRAGMENT_ROOT:
                    collectCompilationUnits((IPackageFragmentRoot) javaElement, cus);
                    break;
                case IJavaElement.JAVA_PROJECT:
                    collectCompilationUnits((IJavaProject) javaElement, cus);
                }
            }
        }
    }
}
