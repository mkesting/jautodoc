/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;


/**
 * Helper class for the platform UI.
 */
public class UIHelper {

	/**
	 * Open the given file in editor.
	 * 
	 * @param file the file
	 * 
	 * @throws PartInitException part init exception occured
	 */
	public static void openInEditor(IFile file) throws PartInitException {
		IWorkbenchWindow aw = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (aw != null) {
			IWorkbenchPage page = aw.getActivePage();
			if (page != null) {
				IDE.openEditor(page, file, true);
			}
		}
	}
	
	/**
	 * Select and reveal a resource in the given workbench window.
	 * 
	 * @param resource the resource
	 * @param window the workbench window
	 */
	public static void selectAndReveal(IResource resource, IWorkbenchWindow window) {
		// validate the input
		if (window == null || resource == null) {
			return;
		}
		
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			return;
		}

		// get all the view and editor parts
		List<IWorkbenchPart> parts = new ArrayList<IWorkbenchPart>();
		IWorkbenchPartReference refs[] = page.getViewReferences();
		for (int i = 0; i < refs.length; i++) {
			IWorkbenchPart part = refs[i].getPart(false);
			if (part != null) {
				parts.add(part);
			}
		}
		
		refs = page.getEditorReferences();
		for (int i = 0; i < refs.length; i++) {
			if (refs[i].getPart(false) != null) {
				parts.add(refs[i].getPart(false));
			}
		}

		final ISelection selection = new StructuredSelection(resource);
		Iterator<IWorkbenchPart> iter = parts.iterator();
		while (iter.hasNext()) {
			IWorkbenchPart part = (IWorkbenchPart) iter.next();

			// get the part's ISetSelectionTarget implementation
			ISetSelectionTarget target = null;
			if (part instanceof ISetSelectionTarget) {
				target = (ISetSelectionTarget)part;
			} else {
				target = (ISetSelectionTarget)part.getAdapter(ISetSelectionTarget.class);
			}

			if (target == null) {
				continue;
			}
			
			// select and reveal resource
			final ISetSelectionTarget finalTarget = target;
			window.getShell().getDisplay().asyncExec(new Runnable() {
				public void run() {
					finalTarget.selectReveal(selection);
				}
			});
		}
	}
}
