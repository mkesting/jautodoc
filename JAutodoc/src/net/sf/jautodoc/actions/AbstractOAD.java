/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.search.TaskSearchResultPage;
import net.sf.jautodoc.utils.SourceUtils;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ClassFileWorkingCopy;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;


/**
 * Abstract base class for object action delegates.
 */
@SuppressWarnings("restriction")
public abstract class AbstractOAD implements IObjectActionDelegate {

    private IWorkbenchPart targetPart;
    protected IWorkbenchPage workbenchPage;


    /* (non-Javadoc)
     * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction, org.eclipse.ui.IWorkbenchPart)
     */
    public void setActivePart(final IAction action, final IWorkbenchPart targetPart) {
        this.targetPart = targetPart;
        this.workbenchPage = targetPart.getSite().getPage();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(final IAction action) {
        final IStructuredSelection selection = (IStructuredSelection) workbenchPage.getSelection();
        if (selection.isEmpty()) {
            return;
        }

        Map<ICompilationUnit, List<IMember>> cus = new HashMap<ICompilationUnit, List<IMember>>();

        if (selection.getFirstElement() instanceof ICompilationUnit) {
            getCompilationUnits(selection.toArray(), cus);
            cus = filterBySearchResult(cus);
        } else if (selection.getFirstElement() instanceof IMember) {
            final IMember[] members = convert(selection.toArray(), new IMember[selection.size()]);
            getCompilationUnitsForMembers(members, cus);
            // filterBySearchResult(cus); not necessary if targetPart is already ISearchResultViewPart
        } else if (selection.getFirstElement() instanceof IPackageFragment) {
            IPackageFragment[] packages = convert(selection.toArray(), new IPackageFragment[selection.size()]);
            if (ConfigurationManager.getCurrentConfiguration().isIncludeSubPackages()) {
                final Set<IPackageFragment> pkgFragments = new HashSet<IPackageFragment>();
                for (IPackageFragment pkgFragment : packages) {
                    if (!pkgFragments.contains(pkgFragment)) {
                        pkgFragments.add(pkgFragment);
                        getSubpackages(pkgFragment, pkgFragments);
                    }
                }
                packages = pkgFragments.toArray(new IPackageFragment[pkgFragments.size()]);
            }
            getCompilationUnitsForPackages(packages, cus);
            cus = filterBySearchResult(cus);
        } else if (selection.getFirstElement() instanceof IJavaProject) {
            final Object[] projects = selection.toArray();
            getCompilationUnitsForProjects(projects, cus);
            cus = filterBySearchResult(cus);
        }

        if (cus.isEmpty()) {
            return;
        }

        final ITask task = getTask(cus);
        try {
            new ProgressMonitorDialog(workbenchPage.getWorkbenchWindow().getShell())
                            .run(false, true, task);
            task.checkSuccess();
        }
        catch (Exception e) {
            JAutodocPlugin.getDefault().handleException(
                    workbenchPage.getWorkbenchWindow().getShell(),
                    task.getCompilationUnit(), e);
        }

    }

    private Map<ICompilationUnit, List<IMember>> filterBySearchResult(final Map<ICompilationUnit, List<IMember>> cus) {
        if (!(targetPart instanceof ISearchResultViewPart)
                || !(((ISearchResultViewPart) targetPart).getActivePage() instanceof TaskSearchResultPage)) {
            return cus;
        }

        final ISearchResultViewPart resultViewPart = (ISearchResultViewPart) targetPart;
        final TaskSearchResultPage resultView = (TaskSearchResultPage)resultViewPart.getActivePage();
        final IJavaElement[] elements = resultView.getFilteredJavaElements();

        final Map<ICompilationUnit, List<IMember>> result = new HashMap<ICompilationUnit, List<IMember>>();

        for (IJavaElement element : elements) {
            if (element instanceof ICompilationUnit) {
                if (cus.containsKey(element) && result.get(element) == null) {
                    result.put((ICompilationUnit) element, new ArrayList<IMember>());
                }
            }
            else if (element instanceof IMember) {
                final IMember member = (IMember) element;
                final ICompilationUnit cu = member.getCompilationUnit();

                if (cus.containsKey(cu)) {
                    List<IMember> members = result.get(cu);
                    if (members == null) {
                        members = new ArrayList<IMember>();
                        result.put(cu, members);
                    }
                    members.add(member);
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
    }

    /**
     * Get task for modifying the given compilation units.
     *
     * @param compUnits the compilation units
     * @param members the selected members
     * @return the task
     */
    protected abstract ITask getTask(Map<ICompilationUnit, List<IMember>> cus);

    /**
     * Get compilation units of the given members.
     *
     * @param members the members
     * @param compUnits the compilation units
     */
    private void getCompilationUnitsForMembers(final IMember[] members,
            final Map<ICompilationUnit, List<IMember>> compUnits) {

        for (int i = 0; i < members.length; ++i) {
            final ICompilationUnit cu = members[i].getCompilationUnit();
            if (isValid(cu)) {
                List<IMember> ms = compUnits.get(cu);
                if (ms == null) {
                    ms = new ArrayList<IMember>();
                    compUnits.put(cu, ms);
                }
                ms.add(members[i]);
            }
        }
    }

    /**
     * Get compilation units of the given packages.
     *
     * @param packages the packages
     * @param compUnits the compilation units
     */
    private void getCompilationUnitsForPackages(final IPackageFragment[] packages,
            final Map<ICompilationUnit, List<IMember>> compUnits) {

        for (int i = 0; i < packages.length; ++i) {
            ICompilationUnit[] cus = null;
            try {
                cus = packages[i].getCompilationUnits();
            } catch (JavaModelException e) {
                continue; // ignore
            }

            for (int j = 0; j < cus.length; ++j) {
                final ICompilationUnit cu = cus[j];
                if (isValid(cu) && !(cu instanceof ClassFileWorkingCopy)) {
                    compUnits.put(cu, null);
                }
            }
        }
    }

    /**
     * Get compilation units of the given projects.
     *
     * @param projects the projects
     * @param compUnits the compilation units
     */
    private void getCompilationUnitsForProjects(final Object[] projects,
            final Map<ICompilationUnit, List<IMember>> compUnits) {

        final List<IPackageFragment> packageList= new ArrayList<IPackageFragment>();

        for (int i = 0; i < projects.length; ++i) {
            IPackageFragmentRoot[] packageRoots = null;
            try {
                packageRoots = ((IJavaProject)projects[i]).getPackageFragmentRoots();
            } catch (JavaModelException e) {
                continue; // ignore
            }

            for (int j = 0; j < packageRoots.length; ++j) {
                if (packageRoots[j].isArchive()) {
                    continue;
                }

                IJavaElement[] children = null;
                try {
                    children = packageRoots[j].getChildren();
                } catch (JavaModelException e) {
                    continue; // ignore
                }

                for (int k = 0; k < children.length; ++k) {
                    if (children[k] instanceof IPackageFragment) {
                        packageList.add((IPackageFragment)children[k]);
                    }
                }
            }
        }

        getCompilationUnitsForPackages(packageList.toArray(new IPackageFragment[packageList.size()]), compUnits);
    }

    @SuppressWarnings("unchecked")
    private <T> T[] convert(final Object[] source, final T[] target) {
        for (int i = 0; i < source.length; ++i) {
            target[i] = (T)source[i];
        }
        return target;
    }

    private void getCompilationUnits(final Object[] objects, final Map<ICompilationUnit, List<IMember>> compUnits) {
        for (Object o : objects) {
            if (o instanceof ICompilationUnit && isValid((ICompilationUnit)o)) {
                compUnits.put((ICompilationUnit)o, null);
            }
        }
    }
    
    private void getSubpackages(final IPackageFragment pkgFragment, final Set<IPackageFragment> subpackages) {
        try {
            SourceUtils.getSubpackages(pkgFragment, subpackages);
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(pkgFragment, e);
        }
    }

    private boolean isValid(final ICompilationUnit cu) {
        return cu != null && cu.findPrimaryType() != null; // ignore i.e. package-info.java
    }

    // ----------------------------------------------------
    // Task interface
    // ----------------------------------------------------

    /**
     * An implementation of this interface has to be provided
     * by the subclasses of AbstractOAD.
     */
    protected interface ITask extends IRunnableWithProgress {

        /**
         * Get the compilation unit, that is currently in progress.
         *
         * @return the compilation unit
         */
        public ICompilationUnit getCompilationUnit();

        /**
         * Check for successful completion of the task.
         *
         * @throws Exception exception, if catched while running
         */
        public void checkSuccess() throws Exception;
    }
}
